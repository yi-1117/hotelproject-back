package tw.com.ispan.eeit195_01_back.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.service.ShoppingCartService;

@CrossOrigin
@RestController
@RequestMapping("/api/shopping-cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 創建新的購物車
    @PostMapping("/create/{memberId}")
    public ResponseEntity<ShoppingCart> createShoppingCart(@PathVariable int memberId) {
        try {
            ShoppingCart shoppingCart = shoppingCartService.createShoppingCart(memberId);
            return ResponseEntity.status(HttpStatus.CREATED).body(shoppingCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // 獲取指定會員的購物車
    @GetMapping("/find/{memberId}")
    public ResponseEntity<ShoppingCart> getShoppingCartByMemberId(@PathVariable int memberId) {
        try {
            ShoppingCart shoppingCart = shoppingCartService.getShoppingCartByMemberId(memberId);
            return ResponseEntity.ok(shoppingCart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 刪除購物車
    @DeleteMapping("/{shoppingCartId}")
    public ResponseEntity<Void> deleteShoppingCart(@PathVariable int shoppingCartId) {
        try {
            shoppingCartService.deleteShoppingCart(shoppingCartId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
