package tw.com.ispan.eeit195_01_back.shop.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

@Entity
@Table(name = "shopping_order")
public class ShoppingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shopping_order_id")
    private Integer shoppingOrderId; // shopping_order_id

    @OneToMany(mappedBy = "shoppingOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShoppingDetail> shoppingDetails = new ArrayList<>();

    @ManyToOne // 一個訂單屬於一個會員
    @JsonIgnore
    @JoinColumn(name = "member_id", referencedColumnName = "member_id") // 確保 customer_id 是在 shopping_order表中，而member_id
    // // 是在 member 表中的主鍵
    private MemberBean member; // 訂單所屬的會員
    private String shoppingOrderNumber;
    private String paymentMethod;
    private BigDecimal totalPayment;
    private LocalDateTime createOrderDate;
    // private String orderStatus; // pending, delivery, done
    private LocalDateTime lastOrderStatusUpdate;
    private String description; // shopping_order_description
    private Boolean paymentStatus;
    private int shopperPhone;
    private String shopperFirstName;
    private String shopperLastName;
    private String shopperEmail;
    private String shopperPassword;
    private String shopperAddress;
    private boolean shopperGender;
    @Column(name = "usedPoints")
    private Integer usedPoints; // 記錄使用的點數
    private BigDecimal unusedDiscountAmount; // 記錄未使用點數的折價金額

    @Enumerated(EnumType.STRING) // 使用字符串方式保存狀態
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus; // 訂單狀態

    // 內部 OrderStatus 枚舉
    public enum OrderStatus {
        待付款, // 待處理
        已付款, // 已完成
        已取消, // 已取消
        外送中; // 外送中
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public LocalDateTime getCreateOrderDate() {
        return createOrderDate;
    }

    public void setCreateOrderDate(LocalDateTime createOrderDate) {
        this.createOrderDate = createOrderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getLastOrderStatusUpdate() {
        return lastOrderStatusUpdate;
    }

    public void setLastOrderStatusUpdate(LocalDateTime lastOrderStatusUpdate) {
        this.lastOrderStatusUpdate = lastOrderStatusUpdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    public int getShopping_order_id() {
        return shoppingOrderId;
    }

    public void setShopping_order_id(int shopping_order_id) {
        this.shoppingOrderId = shopping_order_id;
    }

    public Integer getShoppingOrderId() {
        return shoppingOrderId;
    }

    public void setShoppingOrderId(Integer shoppingOrderId) {
        this.shoppingOrderId = shoppingOrderId;
    }

    public List<ShoppingDetail> getShoppingDetails() {
        return shoppingDetails;
    }

    public void setShoppingDetails(List<ShoppingDetail> shoppingDetails) {
        this.shoppingDetails = shoppingDetails;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getShopperPhone() {
        return shopperPhone;
    }

    public void setShopperPhone(int shopperPhone) {
        this.shopperPhone = shopperPhone;
    }

    public String getShopperFirstName() {
        return shopperFirstName;
    }

    public void setShopperFirstName(String shopperFirstName) {
        this.shopperFirstName = shopperFirstName;
    }

    public String getShopperLastName() {
        return shopperLastName;
    }

    public void setShopperLastName(String shopperLastName) {
        this.shopperLastName = shopperLastName;
    }

    public String getShopperEmail() {
        return shopperEmail;
    }

    public void setShopperEmail(String shopperEmail) {
        this.shopperEmail = shopperEmail;
    }

    public String getShopperPassword() {
        return shopperPassword;
    }

    public void setShopperPassword(String shopperPassword) {
        this.shopperPassword = shopperPassword;
    }

    public String getShopperAddress() {
        return shopperAddress;
    }

    public void setShopperAddress(String shopperAddress) {
        this.shopperAddress = shopperAddress;
    }

    public boolean isShopperGender() {
        return shopperGender;
    }

    public void setShopperGender(boolean shopperGender) {
        this.shopperGender = shopperGender;
    }

    public String getShoppingOrderNumber() {
        return shoppingOrderNumber;
    }

    public void setShoppingOrderNumber(String shoppingOrderNumber) {
        this.shoppingOrderNumber = shoppingOrderNumber;
    }

    public Integer getUsedPoints() {
        return usedPoints;
    }

    public void setUsedPoints(Integer usedPoints) {
        this.usedPoints = usedPoints;
    }

    public BigDecimal getUnusedDiscountAmount() {
        return unusedDiscountAmount;
    }

    public void setUnusedDiscountAmount(BigDecimal unusedDiscountAmount) {
        this.unusedDiscountAmount = unusedDiscountAmount;
    }

    @Override
    public String toString() {
        return "ShoppingOrder [shoppingOrderId=" + shoppingOrderId + ", shoppingDetails=" + shoppingDetails
                + ", member=" + member + ", shoppingOrderNumber=" + shoppingOrderNumber + ", paymentMethod="
                + paymentMethod + ", totalPayment=" + totalPayment + ", createOrderDate=" + createOrderDate
                + ", lastOrderStatusUpdate=" + lastOrderStatusUpdate + ", description=" + description
                + ", paymentStatus=" + paymentStatus + ", shopperPhone=" + shopperPhone + ", shopperFirstName="
                + shopperFirstName + ", shopperLastName=" + shopperLastName + ", shopperEmail=" + shopperEmail
                + ", shopperPassword=" + shopperPassword + ", shopperAddress=" + shopperAddress + ", shopperGender="
                + shopperGender + ", usedPoints=" + usedPoints + ", unusedDiscountAmount=" + unusedDiscountAmount
                + ", orderStatus=" + orderStatus + "]";
    }

}
