package tw.com.ispan.eeit195_01_back.shop.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "shopping_detail")

public class ShoppingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopping_detail_id")
    private int shoppingDetailId; // shopping_detail_id

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "shopping_order_id", referencedColumnName = "shopping_order_id")
    private ShoppingOrder shoppingOrder; // ShoppingOrder refers to this ShoppingDetail

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity; // product_quantity
    private BigDecimal totalAmount; // total_amount

    public int getshoppingdetail_id() {
        return shoppingDetailId;
    }

    public void setId(int shoppingDetailId) {
        this.shoppingDetailId = shoppingDetailId;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getShoppingDetailId() {
        return shoppingDetailId;
    }

    public void setShoppingDetailId(int shoppingDetailId) {
        this.shoppingDetailId = shoppingDetailId;
    }

    public ShoppingOrder getShoppingOrder() {
        return shoppingOrder;
    }

    public void setShoppingOrder(ShoppingOrder shoppingOrder) {
        this.shoppingOrder = shoppingOrder;
    }

    @Override
    public String toString() {
        return "ShoppingDetail [shoppingDetailId=" + shoppingDetailId + ", shoppingOrder=" + shoppingOrder
                + ", product=" + product + ", quantity=" + quantity + ", totalAmount=" + totalAmount + "]";
    }

}
