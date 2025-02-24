package tw.com.ispan.eeit195_01_back.shop.controller;

import java.util.List;
import java.util.Optional; // 如果使用了 Optional 也需要這個

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.shop.bean.ProductCategory; // 假設有 ProductCategoryBean 類別
import tw.com.ispan.eeit195_01_back.shop.service.ProductCategoryService; // 假設有 ProductCategoryService 類別

@CrossOrigin
@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    // 獲取所有產品分類
    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllProductCategories() {
        List<ProductCategory> categories = productCategoryService.select(null); // 獲取全部類別的方法
        return ResponseEntity.ok(categories);
    }

    // 根據 ID 獲取單一產品分類
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getProductCategoryById(@PathVariable Integer id) {
        ProductCategory category = productCategoryService.findById(id);
        if (category != null) {
            return ResponseEntity.ok(category);
        }
        return ResponseEntity.notFound().build();
    }

    // 創建新的產品分類
    @PostMapping(path = { "/create" })
    public ResponseEntity<ProductCategory> createProductCategory(@RequestBody ProductCategory productCategory) {
        ProductCategory createdCategory = productCategoryService.insert(productCategory);
        return ResponseEntity.status(201).body(createdCategory);
    }

    // 更新現有的產品分類
    @PutMapping("/update")
    public ResponseEntity<ProductCategory> updateProductCategory(@RequestBody ProductCategory productCategory) {
        ProductCategory updatedCategory = productCategoryService.update(productCategory);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        }
        return ResponseEntity.notFound().build();
    }

    // 刪除產品分類
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProductCategory(@PathVariable Integer id) {
        if (productCategoryService.remove(id)) {
            // 返回 200 OK，並提供成功刪除的訊息
            return ResponseEntity.ok("Product category with ID " + id + " has been successfully deleted.");
        } else {
            // 返回 404 Not Found，若找不到對應的產品分類
            return ResponseEntity.notFound().build();
        }
    }

    // // 計算產品分類數量 (示例)
    // @GetMapping("/count")
    // public ResponseEntity<Long> countProductCategories() {
    // long count = productCategoryService.count("{}"); // 這裡可以設置適當的 JSON 條件
    // return ResponseEntity.ok(count);
    // }

    // 假設的查詢方法
    @PostMapping("/find")
    public ResponseEntity<ProductCategory> findProductCategory(@RequestBody String json) {
        Optional<ProductCategory> optionalCategory = productCategoryService.findByProductCategory(json);
        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(optionalCategory.get());
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
