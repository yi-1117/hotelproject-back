package tw.com.ispan.eeit195_01_back.points.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;
import tw.com.ispan.eeit195_01_back.points.repository.PointsRepository;
import tw.com.ispan.eeit195_01_back.points.service.PointsService;
import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;

@RestController
@RequestMapping("/points")
@CrossOrigin
public class PointsController {
    @Autowired
    private PointsService pointsService;
    @Autowired
    private PointsRepository pointsRepository;
    @Autowired
    private MemberRepository memberRepository;

    @PostMapping("/add/{memberId}/{pointsToAdd}")
    public ResponseEntity<Map<String, String>> addPoints(@PathVariable Integer memberId,
            @PathVariable Integer pointsToAdd) {
        // @RequestBody: 通常用於處理 JSON 格式的數據。
        Map<String, String> response = new HashMap<>();
        // 檢查會員是否存在
        MemberBean member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            response.put("error", "會員不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // 查找該會員的積分資料
        PointsBean pointsBean = pointsRepository.findByMember(member);
        if (pointsBean == null) {
            response.put("error", "會員積分資料不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (pointsToAdd < 0) {
            response.put("error", "增加的積分不能為零或負數");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        PointsBean addedPointsBean = pointsService.addPoints(pointsBean, pointsToAdd);
        if (addedPointsBean != null) {
            response.put("sucess", "積分增加成功");
            response.put("points", addedPointsBean.getCurrentPoints().toString());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "積分增加失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("reduce/{memberId}/{pointsToReduce}")
    public ResponseEntity<Map<String, String>> reducePoints(@PathVariable Integer memberId,
            @PathVariable Integer pointsToReduce) {
        Map<String, String> response = new HashMap<>();
        // 檢查會員是否存在
        MemberBean member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            response.put("error", "會員不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // 查找該會員的積分資料
        PointsBean pointsBean = pointsRepository.findByMember(member);
        if (pointsBean == null) {
            response.put("error", "會員積分資料不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (pointsToReduce < 0) {
            response.put("error", "減少的積分不能為零或負數");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // PointsBean pointsBean = pointsRepository.findByMember_MemberId(memberId);
        PointsBean reducedPointsBean = pointsService.reducePoints(pointsBean, pointsToReduce);
        if (reducedPointsBean != null) {
            response.put("sucess", "積分減少成功");
            response.put("points", reducedPointsBean.getCurrentPoints().toString());
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "積分減少失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("get/{memberId}")
    public ResponseEntity<Map<String, String>> getCurrentPoints(@PathVariable Integer memberId) {
        Map<String, String> response = new HashMap<>();
        // 檢查會員是否存在
        MemberBean member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            response.put("error", "會員不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        // 查找該會員的積分資料
        PointsBean pointsBean = pointsRepository.findByMember(member);
        if (pointsBean == null) {
            response.put("error", "會員積分資料不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("success", pointsBean.getCurrentPoints().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("update/{memberId}/{pointsToUpdated}")
    public ResponseEntity<Map<String, String>> updatedPoints(@PathVariable Integer memberId,
            @PathVariable Integer pointsToUpdated) {
        Map<String, String> response = new HashMap<>();
        // 檢查會員是否存在
        MemberBean member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            response.put("error", "會員不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        PointsBean pointsBean = pointsRepository.findByMember(member);
        if (pointsBean == null) {
            response.put("error", "會員積分資料不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (pointsToUpdated < 0) {
            response.put("error", "更新的積分不能為零或負數");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        PointsBean updatedPoints = pointsService.updatedCurrentPoints(pointsBean, pointsToUpdated);
        response.put("success", updatedPoints.getCurrentPoints().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("zero/{memberId}")
    public ResponseEntity<Map<String, String>> resetCurrentPointsToZero(@PathVariable Integer memberId) {
        Map<String, String> response = new HashMap<>();
        MemberBean member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            response.put("error", "會員不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        PointsBean pointsBean = pointsRepository.findByMember(member);
        if (pointsBean == null) {
            response.put("error", "會員積分資料不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        PointsBean zeroPointBean = pointsService.resetCurrentPointsToZero(memberId);
        response.put("success", zeroPointBean.getCurrentPoints().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("allZero/{isResetAll}")
    public ResponseEntity<Map<String, String>> allPointsReset(@PathVariable Boolean isResetAll) {
        Map<String, String> response = new HashMap<>();
        if (!isResetAll) {
            response.put("error", "重置所有積分失敗");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        pointsService.allPointsReset();
        List<PointsBean> allPointsBeans = pointsRepository.findAll();
        response.put("success", "重置所有積分成功");
        response.put("row", String.valueOf(allPointsBeans.size()));
        return ResponseEntity.ok(response);
    }
    @PostMapping("earn-points-by-room-order")
    public ResponseEntity<Map<String, String>> earningPointsByRoomOrder(@RequestBody RoomOrder roomOrder) {
        Map<String, String> response = new HashMap<>();
        Double roomPointExchangeRate =1000.0;
        try {
            // 調用服務層的 earningPointsByRoomOrder 方法
            PointsBean updatedPoints = pointsService.earningPointsByRoomOrder(roomOrder, roomPointExchangeRate);
            
            // 如果成功，回傳 HTTP 200 並提供成功訊息和結果
            response.put("success", "點數加值成功");
            response.put("currentPoints", String.valueOf(updatedPoints.getCurrentPoints()));  // 回傳加值後的點數
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 如果捕獲到非法的參數異常，回傳 HTTP 400 並提供錯誤訊息
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // 捕獲其它異常，回傳 HTTP 500 並提供通用錯誤訊息
            response.put("error", "伺服器錯誤，請稍後再試");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("points-redemption/room-order/{used-points}/{is-used-all-points}")
    public ResponseEntity<Map<String,String>> pointsRedemption(@RequestBody RoomOrder roomOrder,@PathVariable("used-points") Integer usedPoints ,@PathVariable("is-used-all-points") boolean isUsedAllPoints) {
        Map<String,String> response = new HashMap<>();

        try {
            // 調用服務層的 pointsRedemption 方法
            Double discountAmount = pointsService.pointsRedemption(roomOrder, usedPoints,isUsedAllPoints);
            MemberBean member = roomOrder.getMember();
            if(member == null) throw new IllegalArgumentException("會員不存在");
            PointsBean pointsBean = pointsRepository.findByMember(member);
            // 如果成功，回傳 HTTP 200 並提供成功訊息和結果
            response.put("success", "點數兌換成功");
            response.put("currentPoints", String.valueOf(pointsBean.getCurrentPoints()));  // 回傳兌換後的點數
            response.put("discountAmount", discountAmount.toString());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 如果捕獲到非法的參數異常，回傳 HTTP 400 並提供錯誤訊息
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // 捕獲其它異常，回傳 HTTP 500 並提供通用錯誤訊息
            response.put("error", "伺服器錯誤，請稍後再試");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
