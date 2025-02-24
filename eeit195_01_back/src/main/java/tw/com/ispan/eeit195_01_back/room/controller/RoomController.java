package tw.com.ispan.eeit195_01_back.room.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tw.com.ispan.eeit195_01_back.room.bean.Room;
import tw.com.ispan.eeit195_01_back.room.dto.RoomDTO;
import tw.com.ispan.eeit195_01_back.room.service.RoomService;
import tw.com.ispan.eeit195_01_back.room.service.RoomTypeAvailabilityInit;
import tw.com.ispan.eeit195_01_back.room.service.RoomTypeAvailabilityService;

@RestController
@RequestMapping("/api/room")
@CrossOrigin
public class RoomController {

    private final RoomService roomService;
    private final RoomTypeAvailabilityService roomTypeAvailabilityService;
    private final RoomTypeAvailabilityInit roomTypeAvailabilityInit;

    public RoomController(RoomService roomService,
            RoomTypeAvailabilityService roomTypeAvailabilityService,
            RoomTypeAvailabilityInit roomTypeAvailabilityInit) {
        this.roomService = roomService;
        this.roomTypeAvailabilityService = roomTypeAvailabilityService;
        this.roomTypeAvailabilityInit = roomTypeAvailabilityInit;
    }

    @PostMapping("/create/{roomCount}")
    public ResponseEntity<Map<String, String>> createRooms(
            @PathVariable Integer roomCount,
            @RequestBody RoomDTO roomDTO) {
        if (roomCount == null || roomCount <= 0) {
            Map<String, String> message = new HashMap<>();
            message.put("errorRoomCount", "新增房間數量必須為正整數");
            return ResponseEntity.badRequest().body(message);
        }
        if (roomDTO == null) {
            Map<String, String> message = new HashMap<>();
            message.put("errorRoomDTO", "房間相關資料遺失");
            return ResponseEntity.badRequest().body(message);
        }
        Map<String, String> message = roomService.createRooms(roomDTO, roomCount).getMessage();
        if (message.containsKey("success")) {
            // 新增房間後初始化房況
            roomTypeAvailabilityInit.init();
            roomTypeAvailabilityService.initializeRedis();
            return ResponseEntity.ok().body(message);
        } else {
            return ResponseEntity.badRequest().body(message);
        }
    }

    @PutMapping("/update/{roomCount}")
    public ResponseEntity<Map<String, String>> updateRooms(
            @PathVariable Integer roomCount,
            @Valid @RequestBody RoomDTO roomDTO) {
        if (roomCount == null || roomCount <= 0) {
            Map<String, String> message = new HashMap<>();
            message.put("errorRoomCount", "修改房間數量必須為正整數");
            return ResponseEntity.badRequest().body(message);
        }
        if (roomDTO == null) {
            Map<String, String> message = new HashMap<>();
            message.put("errorRoomDTO", "房間相關資料遺失");
            return ResponseEntity.badRequest().body(message);
        }
        Map<String, String> message = roomService.updateRooms(roomDTO, roomCount).getMessage();
        if (message.containsKey("success")) {
            return ResponseEntity.ok().body(message);
        } else {
            return ResponseEntity.badRequest().body(message);
        }
    }

    @DeleteMapping("/delete/{roomNumberStart}/{roomCount}")
    public ResponseEntity<Map<String, String>> deleteRooms(
            @PathVariable Integer roomNumberStart,
            @PathVariable Integer roomCount) {
        if (roomCount == null || roomCount <= 0) {
            Map<String, String> message = new HashMap<>();
            message.put("errorRoomCount", "修改房間數量必須為正整數");
            return ResponseEntity.badRequest().body(message);
        }
        if (roomNumberStart == null) {
            Map<String, String> message = new HashMap<>();
            message.put("errorRoomDTO", "沒有指定起始房號");
            return ResponseEntity.badRequest().body(message);
        }
        Map<String, String> message = roomService.deleteRooms(roomNumberStart, roomCount).getMessage();
        if (message.containsKey("success")) {
            return ResponseEntity.ok().body(message);
        } else {
            return ResponseEntity.badRequest().body(message);
        }
    }

    @GetMapping("/select/{roomNumberStart}/{roomNumberEnd}")
    public ResponseEntity<List<Room>> selectRoomType(
            @PathVariable Integer roomNumberStart,
            @PathVariable Integer roomNumberEnd) {
        if (roomNumberStart > roomNumberEnd) {
            return ResponseEntity.badRequest().body(new ArrayList<Room>());
        }
        return ResponseEntity.ok().body(roomService.selectWithRange(roomNumberStart, roomNumberEnd));
    }

    // 處理 service 回傳的異常
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
