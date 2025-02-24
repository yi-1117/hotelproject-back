package tw.com.ispan.eeit195_01_back.shop.bean;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

@Entity
@Table(name = "shopping_cart")

public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shoppingCartId;

    @OneToOne // 這是一對一關聯
    @JsonIgnore
    @JoinColumn(name = "member_id", referencedColumnName = "member_id") // 指向購買者的外鍵
    private MemberBean member;

    private int originalPrice;
    private int discountAmount;
    private int shoppingCartAmount;

    @OneToMany(mappedBy = "shoppingCart", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CartItem> cartItems = new ArrayList<>(); // 購物車中的商品
    // public ShoppingCart(int shoppingCartId) {
    // this.shoppingCartId = shoppingCartId;
    // }

    public int getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(int shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    public List<CartItem> getCartItems() {
        return cartItems; // 返回購物車中的所有商品
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public int getShoppingCartAmount() {
        return shoppingCartAmount;
    }

    public void setShoppingCartAmount(int shoppingCartAmount) {
        this.shoppingCartAmount = shoppingCartAmount;
    }

    @Override
    public String toString() {
        return "ShoppingCart [shoppingCartId=" + shoppingCartId + 
               ", memberId=" + (member != null ? member.getMemberId() : "null") + 
               ", originalPrice=" + originalPrice + 
               ", discountAmount=" + discountAmount + 
               ", shoppingCartAmount=" + shoppingCartAmount + 
               ", itemCount=" + (cartItems != null ? cartItems.size() : 0) + "]";
    }

}
