package tw.com.ispan.eeit195_01_back.shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.shop.bean.CartItem;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.repositiory.CartItemRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingCartRepository;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    @SuppressWarnings("unused")
    private CartItemRepository cartItemRepository;
    @Autowired
    private MemberRepository memberRepository;

    public ShoppingCart createShoppingCart(int memberId) {
        Optional<MemberBean> optionalMember = memberRepository.findById(memberId); // 根據 ID 找會員

        ShoppingCart shoppingCart = new ShoppingCart();
        if (optionalMember.isPresent()) {
            MemberBean member = optionalMember.get(); // 提取 MemberBean
            shoppingCart.setMember(member); // 關聯會員
        } else {
            throw new IllegalArgumentException("Member not found with ID: " + memberId); // 處理找不到會員的情況
        }

        return shoppingCartRepository.save(shoppingCart); // 保存到資料庫
    }

    public ShoppingCart getShoppingCartById(int shoppingCartId) {
        return shoppingCartRepository.findById(shoppingCartId).orElse(null);
    }

    // 獲取指定會員的購物車
    public ShoppingCart getShoppingCartByMemberId(int memberId) {
        MemberBean member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
        return shoppingCartRepository.findByMember(member); // 根據會員尋找購物車
    }

    // 獲取購物車中的所有商品
    public List<CartItem> getAllCartItems(ShoppingCart shoppingCart) {
        return shoppingCart.getCartItems(); // 返回購物車中的所有商品
    }

    public void deleteShoppingCart(int shoppingCartId) {
        if (!shoppingCartRepository.existsById(shoppingCartId)) {
            throw new IllegalArgumentException("Shopping cart not found with ID: " + shoppingCartId);
        }
        shoppingCartRepository.deleteById(shoppingCartId); // 根據 ID 刪除購物車
    }

    // 此方法已在cartitem中出現
    // 計算購物車中的總價格
    // public int calculateTotalAmount(ShoppingCart shoppingCart) {
    // int total = 0;

    // for (CartItem item : shoppingCart.getCartItems()) {
    // total += item.getAmount(); // 累加每個商品的總價
    // }
    // return total; // 返回整個購物車的總金額
    // }

    // // 清空購物車中的所有商品
    // 現在由CartItem來進行動作
    // public void clearCart(ShoppingCart shoppingCart) {
    // shoppingCart.getCartItems().clear(); // 清空購物車中的所有商品
    // shoppingCartRepository.save(shoppingCart); // 更新購物車狀態
    // }
}
