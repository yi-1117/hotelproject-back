package tw.com.ispan.eeit195_01_back.shop.repositiory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;

public interface ShoppingOrderRepository extends JpaRepository<ShoppingOrder, Integer> {
    List<ShoppingOrder> findByMember(MemberBean member);

    // 根據訂單狀態查詢訂單
    List<ShoppingOrder> findByOrderStatus(ShoppingOrder.OrderStatus orderStatus); // 添加這個方法

    public Optional<ShoppingOrder> findByShoppingOrderNumber(String shoppingOrderNumber);
}
