package tw.com.ispan.eeit195_01_back.shop.bean;

import java.util.Arrays;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_photo")
public class ProductPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productPhotoId; // 主鍵 ID

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // 關聯到產品表的外鍵
    private Product product; // 與 Product 實體的多對一關係

    @Column(name = "photo", nullable = false)
    private byte[] productPhoto; // 儲存圖片的二進位數據

    // Getters and Setters
    public Integer getProductPhotoId() {
        return productPhotoId;
    }

    public void setProductPhotoId(Integer productPhotoId) {
        this.productPhotoId = productPhotoId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public byte[] getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(byte[] productPhoto) {
        this.productPhoto = productPhoto;
    }

    @Override
    public String toString() {
        return "ProductPhoto [productPhotoId=" + productPhotoId + ", product=" + product + ", productPhoto="
                + Arrays.toString(productPhoto) + "]";
    }
}
