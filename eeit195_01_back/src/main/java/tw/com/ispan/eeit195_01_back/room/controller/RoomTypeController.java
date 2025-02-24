package tw.com.ispan.eeit195_01_back.room.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.dto.RoomTypeDTO;
import tw.com.ispan.eeit195_01_back.room.service.RoomTypeAvailabilityService;
import tw.com.ispan.eeit195_01_back.room.service.RoomTypeService;

@RestController
@RequestMapping("/api/room-type")
@CrossOrigin
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    private final RoomTypeAvailabilityService roomTypeAvailabilityService;

    public RoomTypeController(RoomTypeService roomTypeService,
            RoomTypeAvailabilityService roomTypeAvailabilityService) {
        this.roomTypeService = roomTypeService;
        this.roomTypeAvailabilityService = roomTypeAvailabilityService;
    }

    @PostMapping("/create")
    public ResponseEntity<RoomTypeDTO> createRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        System.out.println("roomTypeDTO: " + roomTypeDTO.toString());

        Map<String, String> message = new HashMap<>();

        // Step 1: 確認房型是否已存在
        if (roomTypeService.createRoomType(roomTypeDTO.getRoomType()).isEmpty()) {
            System.out.println("if判斷");
            message.put("errorCreate", "房型已存在");
            roomTypeDTO = RoomTypeDTO.builder().message(message).build();
            System.out.println("roomTypeDTO: " + roomTypeDTO.toString());
            return ResponseEntity.badRequest().body(roomTypeDTO);
        }

        System.out.println("新增結束");
        // Step 2: Redis 初始化
        roomTypeAvailabilityService.initializeRedis();

        // Step 3: 回應成功訊息
        message.put("success", "新增成功");
        roomTypeDTO = RoomTypeDTO.builder().roomType(roomTypeDTO.getRoomType()).message(message).build();
        roomTypeAvailabilityService.initializeRedis();
        System.out.println("roomTypeDTO: " + roomTypeDTO.toString());
        return ResponseEntity.ok(roomTypeDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<RoomTypeDTO> upateRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        Map<String, String> message = new HashMap<>();
        if (roomTypeService.updateRoomType(roomTypeDTO.getRoomType()).isEmpty()) {
            message.put("errorCreate", "房型不存在");
            roomTypeDTO = RoomTypeDTO.builder().message(message).build();
            return ResponseEntity.badRequest().body(roomTypeDTO);
        }
        message.put("success", "更新成功");
        roomTypeDTO = RoomTypeDTO.builder().message(message).build();
        roomTypeAvailabilityService.initializeRedis();
        return ResponseEntity.ok(roomTypeDTO);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<Map<String, String>> deleteRoomType(@PathVariable String name) {
        Map<String, String> message = new HashMap<>();
        if (!roomTypeService.deleteRoomTypeByName(name)) {
            message.put("errorDelete", "房型名稱不存在");
            return ResponseEntity.badRequest().body(message);
        }
        message.put("success", "刪除成功");
        roomTypeAvailabilityService.initializeMSSQL(LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31));
        roomTypeAvailabilityService.initializeRedis();
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/select-all")
    public ResponseEntity<List<RoomType>> selectRoomType() {
        return ResponseEntity.ok().body(roomTypeService.findAll());
    }

    @GetMapping("/select/{roomTypeId}")
    public ResponseEntity<RoomTypeDTO> getMethodName(@PathVariable Integer roomTypeId) {
        Map<String, String> message = new HashMap<>();
        if (roomTypeService.findById(roomTypeId) == null) {
            message.put("error", "ID為 " + roomTypeId + " 的房型不存在");
            RoomTypeDTO roomTypeDTO = RoomTypeDTO.builder().roomType(null).message(message).build();
            return ResponseEntity.badRequest().body(roomTypeDTO);
        }
        message.put("success", "找到房型");
        RoomTypeDTO roomTypeDTO = RoomTypeDTO.builder().roomType(roomTypeService.findById(roomTypeId)).message(message)
                .build();
        return ResponseEntity.ok().body(roomTypeDTO);
    }

}