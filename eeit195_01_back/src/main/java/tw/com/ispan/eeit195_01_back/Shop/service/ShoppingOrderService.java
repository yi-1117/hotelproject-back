package tw.com.ispan.eeit195_01_back.shop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;
import tw.com.ispan.eeit195_01_back.points.service.PointsService;
import tw.com.ispan.eeit195_01_back.shop.bean.CartItem;
import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingDetail;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder.OrderStatus;
import tw.com.ispan.eeit195_01_back.shop.repositiory.CartItemRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingCartRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingDetailRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingOrderRepository;

@Transactional
@Service
public class ShoppingOrderService {
    @Autowired
    private ShoppingOrderRepository shoppingOrderRepository;

    @Autowired
    private ShoppingDetailRepository shoppingDetailRepository;

    @SuppressWarnings("unused")
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private PointsService pointsService;

    public ShoppingOrder createOrder(int memberId, List<Integer> selectedProductIds,
            String shopperFirstName, String shopperLastName,
            int shopperPhone, String shopperEmail,
            String shopperAddress, int pointsToUse) {
        // 獲取會員
        MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        // 獲取會員的購物車
        ShoppingCart shoppingCart = shoppingCartRepository.findByMember(member);

        // 獲取會員的點數
        PointsBean pointsBean = member.getPoints(); // 假設 MemberBean 中有一個指向 PointsBean 的關聯

        // 創建新的訂單
        ShoppingOrder order = new ShoppingOrder(); // 正確初始化 order
        order.setMember(member);
        order.setCreateOrderDate(LocalDateTime.now());
        order.setOrderStatus(ShoppingOrder.OrderStatus.待付款);

        // 生成訂單編號
        String shoppingOrderNumber = generateOrderNumber();
        order.setShoppingOrderNumber(shoppingOrderNumber);

        // 設置購物者信息
        order.setShopperFirstName(shopperFirstName);
        order.setShopperLastName(shopperLastName);
        order.setShopperPhone(shopperPhone);
        order.setShopperEmail(shopperEmail);
        order.setShopperAddress(shopperAddress);

        // 初始化總金額
        BigDecimal totalPayment = BigDecimal.ZERO;

        // 檢查選中的產品的數量，確保至少有一個產品的數量大於 0
        boolean hasValidQuantity = false;

        // 遍歷選中的產品 IDs
        for (Integer productId : selectedProductIds) {
            // 在購物車中找到對應的商品和數量
            Optional<CartItem> optionalCartItem = shoppingCart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getProduct().getProductId().equals(productId))
                    .findFirst();

            if (!optionalCartItem.isPresent()) {
                throw new IllegalArgumentException("CartItem not found for product ID: " + productId);
            }
            CartItem cartItem = optionalCartItem.get();

            // 獲取數量
            int quantity = cartItem.getQuantity();

            // 檢查是否有有效的數量
            if (quantity > 0) {
                hasValidQuantity = true;
            }

            // 查詢產品
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            // 計算總金額
            totalPayment = totalPayment.add(product.getProductUnitPrice().multiply(BigDecimal.valueOf(quantity)));

            // 扣除庫存
            int newStockQuantity = product.getStockQuantity() - quantity;
            if (newStockQuantity < 0) {
                throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
            }
            product.setStockQuantity(newStockQuantity);
            productRepository.save(product);

            // 創建訂單詳細並將其添加到訂單中
            ShoppingDetail detail = new ShoppingDetail();
            detail.setProduct(product);
            detail.setQuantity(quantity);
            detail.setTotalAmount(product.getProductUnitPrice().multiply(BigDecimal.valueOf(quantity)));
            detail.setShoppingOrder(order); // 確保一定要在這行設置 order
            order.getShoppingDetails().add(detail);

            // 將購物車商品的數量設置為零
            cartItem.setQuantity(0);
            cartItem.setAmount(0);
        }

        // 如果沒有有效的購買數量，則拋出異常
        if (!hasValidQuantity) {
            throw new IllegalArgumentException("No products selected for purchase.");
        }

        // 減少使用的點數
        if (pointsToUse > 0) {
            if (pointsBean == null || pointsBean.getCurrentPoints() < pointsToUse) {
                throw new IllegalArgumentException("Insufficient points.");
            }
            // 減少會員的點數
            pointsService.reducePoints(pointsBean, pointsToUse);

            // 設定總付款金額，假設每點對應一元
            BigDecimal discountAmount = BigDecimal.valueOf(pointsToUse);
            totalPayment = totalPayment.subtract(discountAmount);

            // 設置使用的點數和未使用的折價金額
            order.setUsedPoints(pointsToUse);
            order.setUnusedDiscountAmount(totalPayment.add(discountAmount));
        } else {
            // 如果未使用點數，將未使用折扣金額設置為總付款金額
            order.setUnusedDiscountAmount(totalPayment);
        }

        // 設定最終付款金額
        order.setTotalPayment(totalPayment);

        // 持久化訂單
        shoppingOrderRepository.save(order);

        return order;
    }

    // 用於生成訂單編號的輔助方法
    private String generateOrderNumber() {
        // 獲取當前時間並格式化
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeStamp = now.format(formatter); // 格式化時間為 yyyyMMddHHmmss

        // 隨機生成一個大寫字母
        char randomLetter1 = (char) ('A' + new Random().nextInt(26)); // 產生 A-Z 之間的隨機字母
        char randomLetter2 = (char) ('A' + new Random().nextInt(26)); // 產生 A-Z 之間的隨機字母

        // 組合隨機字母和時間戳
        return "" + randomLetter1 + randomLetter2 + timeStamp; // 例如：CA20250205113030
    }

    // 更新訂單狀態
    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
        ShoppingOrder order = shoppingOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // 當訂單狀態變更為已付款時，不需要特別操作庫存（庫存在創建訂單時已經扣除）
        order.setOrderStatus(newStatus);
        order.setLastOrderStatusUpdate(LocalDateTime.now()); // 更新狀態時間

        shoppingOrderRepository.save(order); // 保存更新
    }

    // 自動取消訂單的邏輯（如在一定時間內未付款）
    @Transactional(readOnly = true)
    public ShoppingOrder getOrderById(int orderId) {
        return shoppingOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }

    public ShoppingOrder getOrderByOrderNumber(String orderNumber) {
        // 用於根據訂單編號查詢
        return shoppingOrderRepository.findByShoppingOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with Order Number: " + orderNumber));
    }

    // 根據會員 ID 獲取該會員的所有訂單
    @Transactional(readOnly = true)
    public List<ShoppingOrder> getOrdersByMemberId(int memberId) {
        MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
        return shoppingOrderRepository.findByMember(member); // 返回會員的所有訂單
    }

    // 根據訂單狀態獲取訂單
    @Transactional(readOnly = true)
    public List<ShoppingOrder> getOrdersByStatus(ShoppingOrder.OrderStatus status) {
        return shoppingOrderRepository.findByOrderStatus(status); // 返回指定狀態的所有訂單
    }

    // 自動取消訂單的邏輯
    @Transactional
    public void cancelOrder(int orderId) {
        ShoppingOrder order = shoppingOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // 恢復庫存邏輯：當訂單被取消時，將每個訂單詳情中的商品數量加回庫存
        if (order.getOrderStatus() != ShoppingOrder.OrderStatus.已取消) {
            List<ShoppingDetail> shoppingDetails = shoppingDetailRepository.findByShoppingOrder(order);

            for (ShoppingDetail detail : shoppingDetails) {
                Product product = detail.getProduct();
                int quantity = detail.getQuantity();
                product.setStockQuantity(product.getStockQuantity() + quantity); // 恢復庫存數量
                productRepository.save(product); // 保存庫存更新
            }
        }

        // 設定訂單狀態為已取消
        order.setOrderStatus(ShoppingOrder.OrderStatus.已取消);
        order.setLastOrderStatusUpdate(LocalDateTime.now()); // 更新最後狀態時間

        // 保存訂單狀態更新
        shoppingOrderRepository.save(order); // 保存更新的訂單
    }

    public List<ShoppingDetail> findDetailsByOrderId(int orderId) {
        // 獲取訂單
        ShoppingOrder order = shoppingOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // 根據訂單查找所有購物明細
        return shoppingDetailRepository.findByShoppingOrder(order);
    }

    // 取消訂單
    public void cancelOrderByOrderNumber(String orderNumber) {
        // 獲取訂單
        ShoppingOrder order = shoppingOrderRepository.findByShoppingOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with Order Number: " + orderNumber));

        // 更新訂單狀態為已取消
        order.setOrderStatus(ShoppingOrder.OrderStatus.已取消);

        // 更新最後訂單狀態更新時間
        order.setLastOrderStatusUpdate(LocalDateTime.now());

        // 恢復庫存
        for (ShoppingDetail detail : order.getShoppingDetails()) {
            Integer productId = detail.getProduct().getProductId(); // 獲取商品ID
            Integer quantity = detail.getQuantity(); // 獲取訂單中的商品數量

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            int newStockQuantity = product.getStockQuantity() + quantity; // 加回數量
            product.setStockQuantity(newStockQuantity); // 更新庫存
            productRepository.save(product); // 持久化庫存變更
        }

        // 恢復使用的點數
        int usedPoints = order.getUsedPoints();
        if (usedPoints > 0) {
            PointsBean pointsBean = order.getMember().getPoints(); // 獲取會員的點數
            pointsService.addPoints(pointsBean, usedPoints); // 假設addPoints方法可以增加點數
        }

        shoppingOrderRepository.save(order); // 持久化更新到資料庫
    }

    public void updateOrderStatusByOrderNumber(String orderNumber, ShoppingOrder.OrderStatus newStatus) {
        ShoppingOrder order = shoppingOrderRepository.findByShoppingOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with Order Number: " + orderNumber));

        // 更新訂單狀態
        order.setOrderStatus(newStatus);

        // 更新最後訂單狀態更新時間
        order.setLastOrderStatusUpdate(LocalDateTime.now());

        shoppingOrderRepository.save(order); // 持久化更新到資料庫
    }
}