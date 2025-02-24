package tw.com.ispan.eeit195_01_back.room.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeAvailability;

@SpringBootTest
@Transactional
public class RoomTypeAvailabilityRepositoryTest {

    @Autowired
    private RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private RoomTypeAvailability roomTypeAvailability;
    private RoomType roomType;

    @BeforeEach
    void setUp() {
        // 在每個測試前創建一個新的 RoomType 物件
        roomType = RoomType.builder()
                .roomTypeName("Deluxe Room")
                .roomTypeMaxCount(10)
                .bedType("King Size")
                .area(50.0)
                .bathroomType("En Suite")
                .roomTypeDescription("Luxurious room with ocean view.")
                .isHandicap(false)
                .adultCapacity(2)
                .childrenCapacity(2)
                .maxCapacity(4)
                .unitPrice(200.0)
                .additionalPricePerPerson(30.0)
                .overTimeRatio(1.5)
                .build();
        roomType = roomTypeRepository.save(roomType); // 儲存 RoomType

        // 在每個測試前創建一個新的 RoomTypeAvailability 物件
        roomTypeAvailability = RoomTypeAvailability.builder()
                .date(LocalDate.now())
                .roomTypeAvailableCount(8)
                .roomTypeBean(roomType) // 將 RoomType 關聯進來
                .build();
    }

    @Test
    void testCreateRoomTypeAvailability() {
        // 儲存 RoomTypeAvailability
        RoomTypeAvailability savedRoomTypeAvailability = roomTypeAvailabilityRepository.save(roomTypeAvailability);
        assertNotNull(savedRoomTypeAvailability.getRoomTypeAvailabilityId());
        assertEquals(8, savedRoomTypeAvailability.getRoomTypeAvailableCount());
    }

    @Test
    void testFindRoomTypeAvailabilityById() {
        // 儲存 RoomTypeAvailability
        RoomTypeAvailability savedRoomTypeAvailability = roomTypeAvailabilityRepository.save(roomTypeAvailability);

        // 透過 ID 查詢 RoomTypeAvailability
        RoomTypeAvailability foundRoomTypeAvailability = roomTypeAvailabilityRepository
                .findById(savedRoomTypeAvailability.getRoomTypeAvailabilityId()).orElse(null);

        assertNotNull(foundRoomTypeAvailability);
        assertEquals(savedRoomTypeAvailability.getRoomTypeAvailabilityId(),
                foundRoomTypeAvailability.getRoomTypeAvailabilityId());
        assertEquals(savedRoomTypeAvailability.getRoomTypeAvailableCount(),
                foundRoomTypeAvailability.getRoomTypeAvailableCount());
    }

    @Test
    void testUpdateRoomTypeAvailability() {
        // 儲存 RoomTypeAvailability
        RoomTypeAvailability savedRoomTypeAvailability = roomTypeAvailabilityRepository.save(roomTypeAvailability);

        // 更新 RoomTypeAvailability 的可用房間數
        savedRoomTypeAvailability.setRoomTypeAvailableCount(10);
        RoomTypeAvailability updatedRoomTypeAvailability = roomTypeAvailabilityRepository
                .save(savedRoomTypeAvailability);

        assertEquals(10, updatedRoomTypeAvailability.getRoomTypeAvailableCount());
    }

    @Test
    void testDeleteRoomTypeAvailability() {
        // 儲存 RoomTypeAvailability
        RoomTypeAvailability savedRoomTypeAvailability = roomTypeAvailabilityRepository.save(roomTypeAvailability);

        // 刪除 RoomTypeAvailability
        roomTypeAvailabilityRepository.delete(savedRoomTypeAvailability);

        // 驗證 RoomTypeAvailability 是否已被刪除
        RoomTypeAvailability foundRoomTypeAvailability = roomTypeAvailabilityRepository
                .findById(savedRoomTypeAvailability.getRoomTypeAvailabilityId()).orElse(null);
        assertNull(foundRoomTypeAvailability);
    }
}
