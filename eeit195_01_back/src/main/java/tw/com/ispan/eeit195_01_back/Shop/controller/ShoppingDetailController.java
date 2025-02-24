package tw.com.ispan.eeit195_01_back.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.service.ShoppingDetailService;

@CrossOrigin
@RestController
@RequestMapping("/api/shoppingDetailController")
public class ShoppingDetailController {

    @Autowired
    private ShoppingDetailService shoppingDetailService; // 檢查這個依賴

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductByShoppingDetailId(@PathVariable int id) {
        try {
            Product product = shoppingDetailService.findProductByShoppingDetailId(id);
            return ResponseEntity.ok(product); // 返回產品詳細資訊
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 如果找不到商品，返回404
        }
    }

}
