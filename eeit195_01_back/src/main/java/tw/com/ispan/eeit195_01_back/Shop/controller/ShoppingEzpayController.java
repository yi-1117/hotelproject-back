package tw.com.ispan.eeit195_01_back.shop.controller;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutOneTime;
import ecpay.payment.integration.exception.EcpayException;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;
import tw.com.ispan.eeit195_01_back.points.repository.PointsRepository;
import tw.com.ispan.eeit195_01_back.points.service.PointsService;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;
import tw.com.ispan.eeit195_01_back.shop.service.ShoppingOrderService;

@SuppressWarnings("unused")
@CrossOrigin
@RestController
@RequestMapping("/api/ezpay")
public class ShoppingEzpayController {
    private static final Logger log = LoggerFactory.getLogger(ShoppingEzpayController.class);

    @Autowired
    private ShoppingOrderService shoppingOrderService;
    @Autowired
    private PointsService pointsService;
    @Autowired
    private PointsRepository pointsRepository;

    @PostMapping("/pay")
    public ResponseEntity<String> processPayment(@RequestBody Map<String, String> request) {

        String shoppingOrderNumber = request.get("shoppingOrderNumber"); // 從請求體獲取

        // 檢查參數是否存在
        if (shoppingOrderNumber == null || shoppingOrderNumber.isEmpty()) {
            return ResponseEntity.badRequest().body("Required parameter 'shoppingOrderNumber' is missing.");
        }

        String paymentHtml;
        try {
            // 1. 獲取訂單
            ShoppingOrder order = shoppingOrderService.getOrderByOrderNumber(shoppingOrderNumber);

            // 2. 使用Map或其他資料結構直接儲存參數
            AioCheckOutOneTime aioCheckOutOneTime = new AioCheckOutOneTime();

            aioCheckOutOneTime.setMerchantID("3002607"); // 確保這裡是正確的商戶ID
            aioCheckOutOneTime.setMerchantTradeNo(order.getShoppingOrderNumber()); // 設置訂單編號
            aioCheckOutOneTime.setMerchantTradeDate(
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))); // 設置交易時間
            aioCheckOutOneTime.setTotalAmount(String.valueOf(order.getTotalPayment().intValue())); // 確保金額的格式正確
            aioCheckOutOneTime.setTradeDesc("交易描述"); // 設置交易描述
            aioCheckOutOneTime.setItemName("商品名稱"); // 設置商品名稱
            aioCheckOutOneTime.setReturnURL("https://7655-1-160-29-99.ngrok-free.app/api/ezpay/returnURL"); // 確保使用完整的URL
            aioCheckOutOneTime.setClientBackURL("http://192.168.23.148:5173/shop/");

            // 3. 調用生成HTML的方法
            AllInOne allInOne = new AllInOne("");
            try {
                paymentHtml = allInOne.aioCheckOut(aioCheckOutOneTime, null);
            } catch (EcpayException e) {
                log.error("綠界支付錯誤: " + (e.getMessage() != null ? e.getMessage() : "無法獲取錯誤消息"));
                if (e.getCause() != null) {
                    log.error("根本原因: " + e.getCause().getMessage());
                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("支付處理失敗：" + (e.getMessage() != null ? e.getMessage() : "無法獲取錯誤消息"));
            }

            // 返回生成的HTML
            return ResponseEntity.ok(paymentHtml);
        } catch (Exception e) {
            log.error("處理付款失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("支付處理失敗");
        }
    };

    @PostMapping("/returnURL")
    public ResponseEntity<String> handlePaymentCallback(@RequestParam Map<String, String> request) {
        log.info("接收到支付回調請求: {}", request);

        String shoppingOrderNumber = request.get("MerchantTradeNo");
        String paymentStatus = request.get("RtnCode");

        try {
            ShoppingOrder order = shoppingOrderService.getOrderByOrderNumber(shoppingOrderNumber);
            if (order != null) {
                if ("1".equals(paymentStatus)) { // 假設 "1" 代表支付成功
                    shoppingOrderService.updateOrderStatusByOrderNumber(shoppingOrderNumber,
                            ShoppingOrder.OrderStatus.已付款);
                    log.info("訂單 {} 狀態已更新為已付款", shoppingOrderNumber);

                    // 獲取會員 ID
                    int memberId = order.getMember().getMemberId(); // 取會員 ID

                    // 獲取 PointsBean
                    PointsBean pointsBean = pointsRepository.findByMember_MemberId(memberId);// 假設有此方法根據會員 ID 獲取措施點數Bean

                    // 計算點數
                    int pointsToAdd = (int) (order.getTotalPayment().doubleValue() / 100); // 每筆訂單金額 / 100 的點數
                    pointsService.addPoints(pointsBean, pointsToAdd); // 呼叫 addPoints 方法

                    return ResponseEntity.ok("訂單狀態已更新為已付款，已增加 " + pointsToAdd + " 點數");
                } else {
                    log.warn("訂單 {} 支付失敗，狀態碼: {}", shoppingOrderNumber, paymentStatus);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("支付失敗，狀態碼: " + paymentStatus);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("未找到相對應的訂單");
            }
        } catch (Exception e) {
            log.error("處理支付回調失敗: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("付款處理失敗");
        }
    }
};
