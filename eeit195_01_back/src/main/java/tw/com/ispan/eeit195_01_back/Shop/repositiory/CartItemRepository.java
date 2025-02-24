package tw.com.ispan.eeit195_01_back.shop.repositiory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.eeit195_01_back.shop.bean.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // Optional<CartItem> findByShoppingCartShoppingCartIdAndProductProductId(int shoppingCartId, int productId);
}
