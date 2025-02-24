package tw.com.ispan.eeit195_01_back.room.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutOneTime;
import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;
import tw.com.ispan.eeit195_01_back.room.dto.RoomOrderDTO;
import tw.com.ispan.eeit195_01_back.room.service.RoomOrderService;
import tw.com.ispan.eeit195_01_back.room.service.RoomTypeAvailabilityService;

@RestController
@RequestMapping("/api/order")
@CrossOrigin
public class RoomOrderController {

    private final RoomOrderService roomOrderService;
    private final RoomTypeAvailabilityService roomTypeAvailabilityService;

    public RoomOrderController(RoomOrderService roomOrderService,
            RoomTypeAvailabilityService roomTypeAvailabilityService) {
        this.roomOrderService = roomOrderService;
        this.roomTypeAvailabilityService = roomTypeAvailabilityService;
    }

    @PostMapping("/preview")
    public ResponseEntity<RoomOrderDTO> previewOrder(@RequestBody RoomOrderDTO roomOrderDTO) {
        if (roomOrderDTO.getTotalPaymentBeforeUsedPoints() == null) {
            throw new NullPointerException();
        }
        // 驗證用資料是否和資料庫資料有衝突
        RoomOrderDTO responseDTO = roomOrderService.previewOrder(roomOrderDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmOrder(@RequestBody RoomOrderDTO roomOrderDTO) {
        // 已付款完成，準備建立訂單
        roomOrderService.confirmOrder(roomOrderDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment")
    public String payByCreditCard(@RequestBody RoomOrderDTO roomOrderDTO) {
        // 創建綠界物件
        AioCheckOutOneTime aioCheckOutOneTime = new AioCheckOutOneTime();

        // 設定商家資訊（請確保你的 MerchantID 正確）
        aioCheckOutOneTime.setMerchantID("3002607");
        aioCheckOutOneTime.setPlatformID("3002599");
        aioCheckOutOneTime.setInvoiceMark("N");

        // 設定交易編號（應確保唯一性）
        String tradeNo = "EC" + System.currentTimeMillis();
        roomOrderDTO.setRoomOrderId(tradeNo);
        aioCheckOutOneTime.setMerchantTradeNo(tradeNo);

        // 設定交易時間
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        aioCheckOutOneTime.setMerchantTradeDate(sdf.format(new Date()));

        // 設定交易金額（從 DTO 取得）
        aioCheckOutOneTime.setTotalAmount(String.valueOf((int) Math.round(roomOrderDTO.getTotalPayment())));

        // 交易描述
        aioCheckOutOneTime.setTradeDesc("Room Booking Payment");

        String itemName = roomOrderDTO.getStartingDate() + " 至 " + roomOrderDTO.getLeavingDate() + " # "
                + roomOrderDTO.getRoomTypeOrderDTOs().toString();
        System.out.println(itemName);
        // 設定商品名稱（可傳入多個，以 "#" 分隔）
        aioCheckOutOneTime.setItemName(itemName);

        // 付款完成後通知（通常設置為商家的 API 端點）
        aioCheckOutOneTime.setReturnURL("https://b54c-1-160-29-99.ngrok-free.app/api/order/returnURL");

        // 交易完成後跳轉的頁面
        aioCheckOutOneTime.setClientBackURL("http://192.168.23.148:5173/order-history");

        // 綠界 SDK 初始化
        AllInOne all = new AllInOne("");

        // 產生交易網址（提交至綠界）
        String form = all.aioCheckOut(aioCheckOutOneTime, null);

        // System.out.println("convert");
        RoomOrder roomOrder = roomOrderService.convertDTO(roomOrderDTO);
        System.out.println(roomOrder.getTotalPaymentBeforeUsingPoints());
        // roomOrderService.save(roomOrder);

        // **更新 Redis 空房數量**
        roomTypeAvailabilityService.updateRoomAvailabilityAfterPayment(roomOrderDTO);
        return form;
    }

    @PostMapping("/returnURL")
    public ResponseEntity<String> handlePaymentCallback(@RequestParam Map<String, String> params) {
        try {
            // 打印回傳的參數
            params.forEach((key, value) -> System.out.println(key + ": " + value));

            // 綠界回傳的參數通常會包含交易結果，驗證資料是否正確
            String tradeStatus = params.get("TradeStatus");
            String merchantTradeNo = params.get("MerchantTradeNo");
            System.out.println("tradeStatus: " + tradeStatus);
            System.out.println("merchantTradeNo: " + merchantTradeNo);

            // 處理交易狀態
            if ("SUCCESS".equals(tradeStatus)) {
                // 交易成功的邏輯（例如，更新訂單狀態）
                System.out.println("交易成功，訂單號: " + merchantTradeNo);

                return ResponseEntity.ok("1|OK"); // 必須回傳 "1|OK" 給綠界
            } else {
                // 交易失敗的邏輯
                System.out.println("交易失敗，訂單號: " + merchantTradeNo);
                return ResponseEntity.ok("1|OK"); // 即便是失敗，也需要回應給綠界 "1|OK"
            }
        } catch (Exception e) {
            // 處理錯誤情況
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing callback");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<RoomOrderDTO> putMethodName(@PathVariable String id,
            @Validated @RequestBody RoomOrderDTO roomOrderDTO) {
        Map<String, String> message = new HashMap<>();
        if (id == null) {
            message.put("errorId", "必須指定 id");
            roomOrderDTO = RoomOrderDTO.builder().message(message).build();
            return ResponseEntity.badRequest().body(roomOrderDTO);
        }
        if (roomOrderDTO == null) {
            message.put("errorId", "必須加入修改內容");
            roomOrderDTO = RoomOrderDTO.builder().message(message).build();
            return ResponseEntity.badRequest().body(roomOrderDTO);
        }
        roomOrderService.updateOrder(roomOrderDTO);
        message.put("success", "成功修改訂單");
        roomOrderDTO = RoomOrderDTO.builder().message(message).build();
        return ResponseEntity.ok().body(roomOrderDTO);
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable String orderId) {
        roomTypeAvailabilityService.updateRoomAvailabilityBeforeDeletion(orderId);
        roomOrderService.deleteRoomOrderById(orderId);
        Map<String, String> message = new HashMap<>();
        message.put("success", "訂單 " + orderId + " 已成功刪除");
        return ResponseEntity.ok(message);

    }

    @GetMapping("get-all/{memberId}")
    public ResponseEntity<List<RoomOrderDTO>> getAllRoomOrderByMemberId(@PathVariable Integer memberId) {
        List<RoomOrderDTO> roomOrderDTOs = roomOrderService.findByMemberId(memberId);
        if (roomOrderDTOs.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(roomOrderDTOs);
    }

    @PostMapping("/query")
    public List<RoomOrder> findByConditions(@RequestBody Map<String, Object> conditions) {
        return roomOrderService.queryOrderByConditions(conditions);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
