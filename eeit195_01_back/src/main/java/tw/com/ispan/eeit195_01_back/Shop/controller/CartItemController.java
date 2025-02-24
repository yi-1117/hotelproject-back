package tw.com.ispan.eeit195_01_back.shop.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.shop.bean.CartItem;
import tw.com.ispan.eeit195_01_back.shop.bean.Product;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.repositiory.CartItemRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ProductRepository;
import tw.com.ispan.eeit195_01_back.shop.repositiory.ShoppingCartRepository;
import tw.com.ispan.eeit195_01_back.shop.service.CartItemService;

@CrossOrigin
@RestController
@RequestMapping("/api/cart-item")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository; // 自動注入購物車倉庫

    @Autowired
    private ProductRepository productRepository; // 自動注入產品倉庫

    @Autowired
    private CartItemRepository cartItemRepository;

    // 新增商品到購物車
    @PostMapping("/add")
    public ResponseEntity<CartItem> addProductToCart(@RequestBody CartItem cartItem) {
        try {
            // 確保請求的 cartItem 不是 null
            if (cartItem == null) {
                return ResponseEntity.badRequest().body(null);
            }
            System.out.println("Received CartItem: " + cartItem);

            // 只從 cartItem 中提取 shoppingCartId 和 productId
            Integer shoppingCartId = cartItem.getShoppingCart().getShoppingCartId();
            Integer productId = cartItem.getProduct().getProductId();
            int quantity = cartItem.getQuantity();
            System.out.println("123" + productId);

            // 查詢 ShoppingCart 和 Product
            Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findById(shoppingCartId);
            Optional<Product> productOptional = productRepository.findById(productId);
            System.out.println(productOptional);

            // 確認購物車和產品均已初始化
            if (shoppingCartOptional.isEmpty() || productOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(null); // 返回 400 錯誤
            }

            ShoppingCart shoppingCart = shoppingCartOptional.get();
            Product product = productOptional.get();

            // 執行產品添加邏輯
            CartItem createdCartItem = cartItemService.addProductToCart(shoppingCart, product, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCartItem);
        } catch (Exception e) {
            System.err.println("Error occurred while adding product to cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 返回 400 錯誤
        }
    }

    // 獲取所有購物車項目
    @GetMapping("/findall")
    public ResponseEntity<List<CartItem>> getAllCartItems(@PathVariable Integer shoppingCartId) {
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findById(shoppingCartId);
        if (shoppingCartOptional.isPresent()) {
            List<CartItem> cartItems = cartItemService.getAllCartItems(shoppingCartOptional.get()); // 傳遞購物車物件
            return ResponseEntity.ok(cartItems); // 返回 200 OK 和購物車項目列表
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 返回 404 錯誤如果未找到該購物車
        }
    }

    // // 獲取特定購物車商品項目
    @GetMapping("/{id}")
    public ResponseEntity<CartItem> getCartItemById(@PathVariable int id) {
        CartItem cartItem = cartItemService.getCartItemById(id);
        return cartItem != null ? ResponseEntity.ok(cartItem) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    // // 更新商品數量
    @SuppressWarnings("unused")
    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable int id,
            @RequestBody CartItem updatedCartItem) {
        System.out.println(id);
        System.out.println(updatedCartItem.toString());
        try {
            // 確保傳入的更新內容不為 null
            if (updatedCartItem == null) {
                return ResponseEntity.badRequest().body(null);
            }

            // 使用更新服務方法
            CartItem cartItem = cartItemService.updateCartItem(id, updatedCartItem);
            System.out.println(cartItem.toString());

            // 返回更新後的 CartItem
            return ResponseEntity.ok(cartItem);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    // 將購物車項目變為0
    @PutMapping("/remove/{id}")
    public ResponseEntity<CartItem> removeCartItem(@PathVariable int id) {
        try {
            // 根據 ID 獲取現有的 CartItem
            CartItem existingCartItem = cartItemService.getCartItemById(id);

            if (existingCartItem == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
            }

            // 將數量設置為 0
            existingCartItem.setQuantity(0);
            existingCartItem.setAmount(0); // 將總價設置為 0

            // 保存變更到資料庫
            cartItemRepository.save(existingCartItem);

            return ResponseEntity.ok(existingCartItem); // 返回更新後的 CartItem
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @GetMapping("/shoppingCart/{shoppingCartId}")
    public ResponseEntity<List<CartItem>> getCartItemsByShoppingCartId(@PathVariable int shoppingCartId) {
        List<CartItem> cartItems = cartItemService.getCartItemsByShoppingCartId(shoppingCartId);
        return ResponseEntity.ok(cartItems); // 返回查詢到的商品列表
    }
}
