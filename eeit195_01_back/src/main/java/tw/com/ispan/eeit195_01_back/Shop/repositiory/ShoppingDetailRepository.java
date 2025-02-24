package tw.com.ispan.eeit195_01_back.shop.repositiory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingDetail;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;

public interface ShoppingDetailRepository extends JpaRepository<ShoppingDetail, Integer> {
    // 根據訂單查詢購物明細
    List<ShoppingDetail> findByShoppingOrder(ShoppingOrder shoppingOrder);

    // 根據產品 ID 查詢購物明細
    List<ShoppingDetail> findByProduct_ProductId(Integer productId);
}
