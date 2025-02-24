package tw.com.ispan.eeit195_01_back.shop.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingDetail;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingDetailRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingOrderRepository;

@Transactional

@Service
public class ShoppingDetailSerivce {
    @Autowired
    private ShoppingDetailRepository shoppingDetailRepository;
    @Autowired
    private ShoppingOrderRepository shoppingOrderRepository;

    // 新增購物明細
    public ShoppingDetail createShoppingDetail(ShoppingDetail shoppingDetail) {
        return shoppingDetailRepository.save(shoppingDetail);
    }

    // 根據訂單 ID 查詢購物明細
    public List<ShoppingDetail> findDetailsByOrderId(int orderId) {
        ShoppingOrder shoppingOrder = shoppingOrderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("No ShoppingOrder found for orderId: " + orderId));

        List<ShoppingDetail> details = shoppingDetailRepository.findByShoppingOrder(shoppingOrder);
        if (details.isEmpty()) {
            throw new EntityNotFoundException("No ShoppingDetails found for orderId: " + orderId);
        }
        return details;
    }

    // 根據產品 ID 查詢購物明細
    public List<ShoppingDetail> findDetailsByProductId(int productId) {
        return shoppingDetailRepository.findByProduct_ProductId(productId);
    }

    // 更新購物明細
    public ShoppingDetail updateShoppingDetail(ShoppingDetail shoppingDetail) {
        if (shoppingDetailRepository.existsById(shoppingDetail.getshoppingdetail_id())) {
            return shoppingDetailRepository.save(shoppingDetail);
        } else {
            throw new EntityNotFoundException(
                    "ShoppingDetail not found with id: " + shoppingDetail.getshoppingdetail_id());
        }
    }

    // 刪除購物明細
    public void deleteShoppingDetail(Integer detailId) {
        if (shoppingDetailRepository.existsById(detailId)) {
            shoppingDetailRepository.deleteById(detailId);
        } else {
            throw new EntityNotFoundException("ShoppingDetail not found with id: " + detailId);
        }
    }

    // 查詢所有購物明細
    public List<ShoppingDetail> findAllShoppingDetails() {
        return shoppingDetailRepository.findAll();
    }

    // 計算訂單的總金額
    public BigDecimal calculateTotalAmountForOrder(int orderId) {
        List<ShoppingDetail> details = findDetailsByOrderId(orderId);
        return details.stream()
                .map(ShoppingDetail::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
