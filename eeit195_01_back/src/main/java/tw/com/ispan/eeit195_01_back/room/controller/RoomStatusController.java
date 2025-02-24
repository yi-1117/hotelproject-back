package tw.com.ispan.eeit195_01_back.room.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import tw.com.ispan.eeit195_01_back.room.bean.Room;
import tw.com.ispan.eeit195_01_back.room.service.RoomService;
import tw.com.ispan.eeit195_01_back.room.service.RoomStatusService;

@RestController
@RequestMapping("/api/room-status")
@CrossOrigin
public class RoomStatusController {
    private final RoomStatusService roomStatusService;
    private final RoomService roomService;

    public RoomStatusController(RoomStatusService roomStatusService,
            RoomService roomService) {
        this.roomStatusService = roomStatusService;
        this.roomService = roomService;
    }

    // 初始化 Redis 房態
    @PostConstruct
    public void roomStatusInit() {
        System.err.println("開始初始化房況...");
        List<Room> rooms = roomService.findAll();
        List<String> ids = new ArrayList<>();
        for (Room room : rooms) {
            ids.add(room.getRoomId().toString());
        }
        roomStatusService.updateMultipleRooms(ids, 0, 0);
        System.err.println("初始化房況完成，共匯入" + rooms.size() + "筆房況資料");
    }

    // 獲得複數指定房間的房況
    @GetMapping("/get")
    public ResponseEntity<Map<String, Map<Object, Object>>> getRoomStatus(@RequestBody List<String> roomIds) {
        Map<String, Map<Object, Object>> statusList = new HashMap<>();
        if (roomIds == null || roomIds.size() == 0) {
            List<Room> allRooms = roomService.findAll();
            for (Room room : allRooms) {
                statusList.put(room.getRoomId().toString(), roomStatusService.getStatus(room.getRoomId().toString()));
            }
        } else {
            for (String roomId : roomIds) {
                statusList.put(roomId, roomStatusService.getStatus(roomId));
            }
        }
        return ResponseEntity.ok().body(statusList);
    }

    // 修改複數指定房間的房況
    @PutMapping("/update-room-status/{roomStatus}")
    public ResponseEntity<Map<String, Map<Object, Object>>> updateRoomStatus(@RequestBody List<String> roomIds,
            @PathVariable Integer roomStatus) {
        Map<String, Map<Object, Object>> statusList = new HashMap<>();
        for (String roomId : roomIds) {
            statusList.put(roomId, roomStatusService.updateOneRoom(roomId, roomStatus, null));
        }
        return ResponseEntity.ok().body(statusList);
    }

    // 修改複數指定房間的訂單
    @PutMapping("/update-orderId/{orderId}")
    public ResponseEntity<Map<String, Map<Object, Object>>> updateOrderId(@RequestBody List<String> roomIds,
            @PathVariable Integer orderId) {
        Map<String, Map<Object, Object>> statusList = new HashMap<>();
        for (String roomId : roomIds) {
            statusList.put(roomId, roomStatusService.updateOneRoom(roomId, null, orderId));
        }
        return ResponseEntity.ok().body(statusList);
    }

}
