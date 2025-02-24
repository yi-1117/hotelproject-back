package tw.com.ispan.eeit195_01_back.shop.repositiory;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {
    ShoppingCart findByMember(MemberBean member);
    // Optional<ShoppingCart> findByMemberId(int memberId);
}
