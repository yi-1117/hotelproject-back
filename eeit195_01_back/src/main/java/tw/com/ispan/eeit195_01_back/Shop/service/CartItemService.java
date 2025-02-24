package tw.com.ispan.eeit195_01_back.shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit195_01_back.shop.bean.CartItem;
import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.repositiory.CartItemRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingCartRepository;

@Transactional
@Service
public class CartItemService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository; // 可根據需要引入
    // 可根據需要引入
    @Autowired
    private ShoppingCartRepository shoppingCartRepository; // 可根據需要引入

    // 新增商品到購物車中
    public CartItem addProductToCart(ShoppingCart shoppingCart, Product product, int quantity) {
        CartItem existingCartItem = getCartItem(shoppingCart, product);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            existingCartItem
                    .setAmount(calculateTotalAmount(existingCartItem.getProduct(), existingCartItem.getQuantity())); // 更新金額
            return cartItemRepository.save(existingCartItem); // 儲存更新後的項目
        } else {
            CartItem newItem = new CartItem();
            newItem.setShoppingCart(shoppingCart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setAmount(calculateTotalAmount(product, quantity)); // 計算並設置新項目的金額

            shoppingCart.getCartItems().add(newItem);
            return cartItemRepository.save(newItem); // 保存新的購物車項目
        }
    }

    // 獲取購物車中的所有商品
    public List<CartItem> getAllCartItems(ShoppingCart shoppingCart) {
        return cartItemRepository.findAll(); // 返回所有 CartItem
    }

    // 根據商品查詢特定商品
    public CartItem getCartItem(ShoppingCart shoppingCart, Product product) {
        for (CartItem item : shoppingCart.getCartItems()) {
            if (item.getProduct().equals(product)) {
                return item; // 找到該商品，返回 CartItem
            }
        }
        return null; // 如果該商品不存在，返回 null
    }

    // 更新商品數量
    public CartItem updateCartItem(int id, CartItem updatedCartItem) {
        // 根據 ID 查找現有的 CartItem
        Optional<CartItem> existingCartItemOptional = cartItemRepository.findById(id);
        if (existingCartItemOptional.isPresent()) {
            CartItem existingCartItem = existingCartItemOptional.get();

            // 獲取產品 ID
            Integer productId = updatedCartItem.getProduct().getProductId();

            // 使用產品 ID 查詢完整的 Product 對象
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                // 更新現有的 CartItem 的屬性
                existingCartItem.setQuantity(updatedCartItem.getQuantity());
                existingCartItem.setProduct(product); // 用查詢的完整產品更新
                existingCartItem.setAmount(calculateTotalAmount(product, updatedCartItem.getQuantity()));
            } else {
                throw new RuntimeException("Product not found for ID: " + productId); // 找不到產品時拋出例外
            }

            // 保存更新後的 CartItem 並返回
            return cartItemRepository.save(existingCartItem);
        } else {
            throw new RuntimeException("CartItem not found for ID: " + id); // 找不到時拋出例外
        }
    }

    // 從購物車中刪除商品
    public void removeProductFromCart(ShoppingCart shoppingCart, Product product) {
        CartItem item = getCartItem(shoppingCart, product);
        if (item != null) {
            item.setQuantity(0); // 將商品數量設置為零
            item.setAmount(0);
            cartItemRepository.save(item); // 將總價設置為零，假設這裡簡單處理
            // 如果需要，可考慮在這裡不更新到資料庫，只在記憶體中操作
        }
    }

    // 計算單個 CartItem 的總價
    public int calculateTotalAmount(Product product, int quantity) {
        if (product == null || product.getProductUnitPrice() == null) {
            throw new IllegalArgumentException("Product or its unit price cannot be null");
        }
        System.out.println("Calculating total amount: " + product.getProductUnitPrice() + " * " + quantity);
        return product.getProductUnitPrice().intValue() * quantity; // 返回金額
    }

    // 計算購物車中所有商品的總價
    public int calculateTotalAmount(ShoppingCart shoppingCart) {
        int total = 0;
        for (CartItem item : shoppingCart.getCartItems()) {
            total += item.getAmount(); // 累加每個商品的總價
        }
        return total; // 返回購物車的總價
    }

    public void clearCartItemsQuantities(ShoppingCart shoppingCart) {
        for (CartItem item : shoppingCart.getCartItems()) {
            item.setQuantity(0); // 將所有商品數量設置為 0
            item.setAmount(0); // 將總價設置為 0
            // 如果需要，您可以選擇是否在這裡保存至資料庫
            // cartItemRepository.save(item); // 可以保留，也可以不保留
        }
    }

    public CartItem getCartItemById(int id) {
        Optional<CartItem> itemOptional = cartItemRepository.findById(id);
        if (itemOptional.isPresent()) {
            return itemOptional.get();
        } else {
            return null; // 或者您可以拋出異常
        }
    }

    public List<CartItem> getCartItemsByShoppingCartId(int shoppingCartId) {
        // 檢索特定購物車
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findById(shoppingCartId);
        if (shoppingCartOptional.isPresent()) {
            ShoppingCart shoppingCart = shoppingCartOptional.get();
            return shoppingCart.getCartItems(); // 返回該購物車的所有 CartItem
        } else {
            throw new RuntimeException("ShoppingCart not found for ID: " + shoppingCartId); // 找不到購物車時拋出例外
        }
    }

    
    // public void clearCart(ShoppingCart shoppingCart) {
    // shoppingCart.getCartItems().clear();
    // }
}
