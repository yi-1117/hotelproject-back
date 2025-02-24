package tw.com.ispan.eeit195_01_back.shop.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductPhoto;
import tw.com.ispan.eeit195_01_back.shop.service.ProductPhotoService;

@CrossOrigin // 替換此URL為前端的URL
@RestController
@RequestMapping("/api/product-photos")
public class ProductPhotoController {

    @Autowired
    private ProductPhotoService productPhotoService;

    // 上傳商品照片
    @PostMapping("/upload")
    public ResponseEntity<String> uploadProductPhoto(@RequestParam("productId") Integer productId,
            @RequestParam("file") MultipartFile file) throws IOException {
        // 將檔案轉換為二進位數據
        byte[] photoData = file.getBytes();

        // 調用服務層方法保存圖片
        productPhotoService.saveProductPhoto(productId, photoData);

        return ResponseEntity.ok("圖片上傳成功");
    }

    // 根據商品 ID 獲取所有照片
    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductPhoto>> getPhotosByProductId(@PathVariable Integer productId) {
        List<ProductPhoto> photos = productPhotoService.findPhotosByProductId(productId);
        return ResponseEntity.ok(photos);
    }

    // 刪除商品照片
    @DeleteMapping("/{productPhotoId}")
    public ResponseEntity<String> deletePhoto(@PathVariable Integer productPhotoId) {
        productPhotoService.deleteProductPhoto(productPhotoId);
        return ResponseEntity.ok("商品照片已刪除");
    }

    @GetMapping("") // 更改此行，確保能獲得所有商品照片
    public ResponseEntity<List<ProductPhoto>> getAllPhotos() {
        List<ProductPhoto> photos = productPhotoService.findAllPhotos(); // 確保使用服務層方法來獲取所有照片
        return ResponseEntity.ok(photos);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateProductPhoto(
            @RequestParam("productPhotoId") int productPhotoId,
            @RequestParam("productId") int productId,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            ProductPhoto productPhoto = new ProductPhoto(); // 構造 ProductPhoto
            productPhoto.setProductPhotoId(productPhotoId);

            // 呼叫服務器方法，處理文件上傳
            productPhotoService.updateProductPhoto(productPhoto, file, productId);

            return ResponseEntity.ok("商品照片更新成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失敗");
        }
    }

}