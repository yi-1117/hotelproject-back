package tw.com.ispan.eeit195_01_back.room.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoomTypeAvailabilityInit {
    private final RoomTypeAvailabilityService roomTypeAvailabilityService;

    public RoomTypeAvailabilityInit(RoomTypeAvailabilityService roomTypeAvailabilityService) {
        this.roomTypeAvailabilityService = roomTypeAvailabilityService;
    }

    @PostConstruct
    public void init() {
        System.err.println("依照現有房型開始匯入 2025 年空房資料");
        roomTypeAvailabilityService.initializeMSSQL(LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31));
        roomTypeAvailabilityService.initializeRedis();
    }
}
