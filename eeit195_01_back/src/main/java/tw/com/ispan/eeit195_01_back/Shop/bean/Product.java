package tw.com.ispan.eeit195_01_back.shop.bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId; // 商品ID (主鍵)
    private String productName; // 商品名稱
    private String sku; // SKU 庫存單位
    private String brandName; // 供應商品牌
    private BigDecimal productUnitPrice; // 產品定價
    private BigDecimal discount; // 折扣
    private String productDescription; // 產品描述
    private LocalDateTime createdDate; // 上架時間
    private LocalDateTime lastUpdatedDate; // 最新更新時間
    private Integer capacity; // 容量
    private Integer stockQuantity; // 庫存數量
    private String sellerName; // 賣方聯絡人
    private String sellerPhone; // 賣家電話
    private String sellerEmail; // 賣家信箱
    private String sellerAddress; // 賣家住址

    // 多對一關聯到 ProductCategory
    @ManyToOne
    // @JsonIgnore
    @JoinColumn(name = "product_category_id", nullable = false) // 外鍵
    private ProductCategory productCategory;

    // 一對多關聯到 ProductPhoto
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<ProductPhoto> photos = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShoppingDetail> shoppingDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>(); // 與 CartItem 的關聯

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public BigDecimal getProductUnitPrice() {
        return productUnitPrice;
    }

    public void setProductUnitPrice(BigDecimal productUnitPrice) {
        this.productUnitPrice = productUnitPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public List<ProductPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ProductPhoto> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId
                + ", productName=" + productName
                + ", sku=" + sku
                + ", brandName=" + brandName
                + ", productUnitPrice=" + productUnitPrice
                + ", discount=" + discount
                + ", productDescription=" + productDescription
                + ", createdDate=" + createdDate
                + ", lastUpdatedDate=" + lastUpdatedDate
                + ", capacity=" + capacity
                + ", stockQuantity=" + stockQuantity
                + ", sellerName=" + sellerName
                + ", sellerPhone=" + sellerPhone
                + ", sellerEmail=" + sellerEmail
                + ", sellerAddress=" + sellerAddress
                + ", productCategory=" + (productCategory != null ? productCategory.getCategoryName() : "null")
                + ", photos=" + (photos != null ? photos.size() + " items" : "none")
                + ", shoppingDetails=" + (shoppingDetails != null ? shoppingDetails.size() + " items" : "none")
                + "]";
    }

}
