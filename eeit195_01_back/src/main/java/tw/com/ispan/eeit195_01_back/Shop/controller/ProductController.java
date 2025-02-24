package tw.com.ispan.eeit195_01_back.shop.controller;

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
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.service.ProductService;

@CrossOrigin
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // 新增產品
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody String json) {
        Product product = productService.create(json);
        if (product != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 可以返回更详细的错误信息
        }
    }

    // 取得所有產品
    @GetMapping("")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.select(null); // 若需特定条件，请传入相应的 Product 对象
        return ResponseEntity.ok(products);
    }

    // 用 ID 查詢產品
    @GetMapping("/find/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 更新產品
    @PutMapping("/update")
    public ResponseEntity<Product> modifyProduct(@RequestBody String json) {
        Product updatedProduct = productService.modify(json);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 可以返回更详细的错误信息
        }
    }

    // 刪除產品
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        boolean deleted = productService.remove(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // // 根据 JSON 搜索产品（如果您有实现）
    // @PostMapping("/find")
    // public ResponseEntity<List<Product>> findProducts(@RequestBody String json) {
    // List<Product> foundProducts = productService.find(json);
    // if (foundProducts != null && !foundProducts.isEmpty()) {
    // return ResponseEntity.ok(foundProducts);
    // } else {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    // }
    // }
}
