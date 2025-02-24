package tw.com.ispan.eeit195_01_back.room.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.room.service.RoomTypeAvailabilityService;

@RestController
@RequestMapping("/api/room-availability")
@CrossOrigin
public class RoomTypeAvailabilityController {

    private final RoomTypeAvailabilityService roomTypeAvailabilityService;

    public RoomTypeAvailabilityController(RoomTypeAvailabilityService roomTypeAvailabilityService) {
        this.roomTypeAvailabilityService = roomTypeAvailabilityService;
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> postMethodName(@RequestBody List<String> keys) {
        Map<String, Object> roomCounts = roomTypeAvailabilityService.getRoomCount(keys);
        return ResponseEntity.ok(roomCounts);
    }

}
