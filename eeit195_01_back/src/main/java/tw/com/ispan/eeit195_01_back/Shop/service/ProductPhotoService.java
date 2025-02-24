package tw.com.ispan.eeit195_01_back.shop.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.bean.ProductPhoto;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductPhotoRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductRepository;

@Service
@Transactional
public class ProductPhotoService {

    @Autowired
    private ProductPhotoRepository productPhotoRepository;
    @Autowired
    private ProductRepository productRepository;

    // 保存商品照片
    @Autowired
    private ProductService productService; // 注入 ProductService，以便能夠根據 productId 獲取相應的 Product

    public void saveProductPhoto(Integer productId, byte[] photoData) {
        // 確保 productId 合法並存在的 Product
        Product product = productService.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("無效的產品 ID");
        }

        // 創建新的 ProductPhoto 對象並設定值
        ProductPhoto productPhoto = new ProductPhoto();
        productPhoto.setProduct(product); // 設定商品
        productPhoto.setProductPhoto(photoData); // 設定圖片的二進位數據

        // 保存 ProductPhoto
        productPhotoRepository.save(productPhoto);
    }

    // 獲取特定商品的所有照片
    public List<ProductPhoto> findPhotosByProductId(Integer productId) {
        return productPhotoRepository.findByProduct_ProductId(productId);
    }

    // 刪除商品照片
    public void deleteProductPhoto(Integer productPhotoId) {
        productPhotoRepository.deleteById(productPhotoId);
    }

    // 更新商品照片 - 假設此方法用於更新指定的商品照片
    public void updateProductPhoto(ProductPhoto productPhoto, MultipartFile file, int productId) {
        // 檢查商品照片 ID 是否存在
        if (!productPhotoRepository.existsById(productPhoto.getProductPhotoId())) {
            throw new IllegalArgumentException("商品照片 ID 不存在。");
        }

        // 查找對應的產品並設置
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("產品 ID 不存在。"));
        productPhoto.setProduct(product); // 設定與產品的關聯

        // 如果有新文件，則保存文件
        if (file != null && !file.isEmpty()) {
            try {
                byte[] imageBytes = file.getBytes();
                productPhoto.setProductPhoto(imageBytes); // 更新圖片內容
            } catch (IOException e) {
                throw new RuntimeException("圖片上傳失敗: " + e.getMessage());
            }
        }

        // 更新商品照片，實際的保存操作進行更新
        productPhotoRepository.save(productPhoto);
    }

    public List<ProductPhoto> findAllPhotos() {
        return productPhotoRepository.findAll();
    }

    // 其他 CRUD 方法可以依需求添加
}