package tw.com.ispan.eeit195_01_back.shop.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cartitem")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ShoppingCart_Item_ID; // 購物車商品 ID (PK)

    @ManyToOne // 多對一關係，指向購物車
    @JoinColumn(name = "shopping_cart_id", nullable = false) // 對應購物車表的外鍵
    private ShoppingCart shoppingCart;

    @ManyToOne // 多對一關係，指向商品
    @JoinColumn(name = "product_id", nullable = false) // 對應商品表的外鍵

    private Product product;

    @Column(nullable = false)
    private int quantity; // 數量

    @Column(nullable = false)
    private int amount; // 商品總價

    public int getShoppingCart_Item_ID() {
        return ShoppingCart_Item_ID;
    }

    public void setShoppingCart_Item_ID(int ShoppingCart_Item_ID) {
        this.ShoppingCart_Item_ID = ShoppingCart_Item_ID;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CartItem [ShoppingCart_Item_ID=" + ShoppingCart_Item_ID +
                ", quantity=" + quantity +
                ", amount=" + amount +
                "product=" + (product != null ? product.getProductId() : "null"); // 僅返回產品 ID
    }

}
