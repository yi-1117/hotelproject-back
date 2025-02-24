package tw.com.ispan.eeit195_01_back.shop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingDetail;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;
import tw.com.ispan.eeit195_01_back.shop.service.ShoppingDetailService;
import tw.com.ispan.eeit195_01_back.shop.service.ShoppingOrderService;

@CrossOrigin
@RestController
@RequestMapping("/api/orders")
public class ShoppingOrderController {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingOrderController.class);

    @Autowired
    private ShoppingOrderService shoppingOrderService;
    @Autowired
    private ShoppingDetailService shoppingDetailSerivce;

    @PostMapping("/create")
    public ResponseEntity<ShoppingOrder> createOrder(@RequestBody Map<String, Object> request) {
        try {
            logger.debug("Received request to create order: {}", request);

            int memberId = (Integer) request.get("memberId");
            @SuppressWarnings("unchecked") // 可能會錯誤
            List<Integer> selectedProductIds = (List<Integer>) request.get("selectedProductIds");
            String shopperFirstName = (String) request.get("shopperFirstName");
            String shopperLastName = (String) request.get("shopperLastName");
            int shopperPhone = (Integer) request.get("shopperPhone");
            String shopperEmail = (String) request.get("shopperEmail");
            String shopperAddress = (String) request.get("shopperAddress");
            int pointsToUse = (Integer) request.get("pointsToUse"); // 獲取點數

            logger.debug("Member ID: {}", memberId);
            logger.debug("Selected Products: {}", selectedProductIds);
            logger.debug("Shopper Info - Name: {} {}, Phone: {}, Email: {}, Address: {}",
                    shopperFirstName, shopperLastName, shopperPhone, shopperEmail, shopperAddress);
            logger.debug("Points to use: {}", pointsToUse);

            // 調用服務層創建訂單
            ShoppingOrder order = shoppingOrderService.createOrder(memberId, selectedProductIds,
                    shopperFirstName, shopperLastName, shopperPhone, shopperEmail, shopperAddress, pointsToUse);

            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        } catch (Exception e) {
            logger.error("An error occurred while creating the order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }

    // 查詢單筆訂單
    @GetMapping("/find/{orderNumber}")
    public ResponseEntity<ShoppingOrder> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            // 獲取訂單
            ShoppingOrder order = shoppingOrderService.getOrderByOrderNumber(orderNumber);

            return ResponseEntity.ok(order); // 返回完整的訂單
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // 根據會員ID獲取所有訂單
    @GetMapping("/findmemberorder/{memberId}")
    public ResponseEntity<List<ShoppingOrder>> getOrdersByMemberId(@PathVariable int memberId) {
        try {
            List<ShoppingOrder> orders = shoppingOrderService.getOrdersByMemberId(memberId);
            return ResponseEntity.ok(orders); // 返回會員的所有訂單
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    // // 獲取訂單詳情
    // @GetMapping("/{orderId}")
    // public ResponseEntity<ShoppingOrder> getOrderById(@PathVariable int orderId)
    // {
    // try {
    // ShoppingOrder order = shoppingOrderService.getOrderById(orderId);
    // return ResponseEntity.ok(order); // 返回訂單
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.notFound().build(); // 404 Not Found
    // }
    // }

    // // 獲取訂單的明細
    // @GetMapping("/{orderId}/details")
    // public ResponseEntity<List<ShoppingDetail>> getOrderDetails(@PathVariable int
    // orderId) {
    // try {
    // List<ShoppingDetail> details =
    // shoppingOrderService.findDetailsByOrderId(orderId);
    // return ResponseEntity.ok(details); // 返回該訂單的詳情
    // } catch (EntityNotFoundException e) {
    // return ResponseEntity.notFound().build(); // 404 Not Found
    // }
    // }

    // 更新訂單狀態
    @PutMapping("/status/{orderNumber}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderNumber,
            @RequestBody Map<String, String> requestBody) {
        try {
            // 獲取新的狀態並轉換為 OrderStatus 類型
            String newStatusString = requestBody.get("newStatus");
            ShoppingOrder.OrderStatus newStatus = ShoppingOrder.OrderStatus.valueOf(newStatusString); // 此行將字符串轉換為OrderStatus
            shoppingOrderService.updateOrderStatusByOrderNumber(orderNumber, newStatus);
            return ResponseEntity.noContent().build(); // 返回204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

    @PutMapping("/cancel/{orderNumber}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderNumber) {
        try {
            shoppingOrderService.cancelOrderByOrderNumber(orderNumber); // 使用訂單編號取消訂單
            return ResponseEntity.noContent().build(); // 返回204 No Content
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }
    // // 刪除訂單
    // @DeleteMapping("/{orderId}")
    // public ResponseEntity<Void> cancelOrder(@PathVariable int orderId) {
    // try {
    // shoppingOrderService.cancelOrder(orderId);
    // return ResponseEntity.noContent().build(); // 返回204 No Content
    // } catch (IllegalArgumentException e) {
    // return ResponseEntity.notFound().build(); // 404 Not Found
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); //
    // 500 Internal Server Error
    // }
    // }
}
