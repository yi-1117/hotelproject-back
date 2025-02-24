package tw.com.ispan.eeit195_01_back.room.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;

@SpringBootTest
@Transactional
public class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private RoomType roomType;

    @BeforeEach
    void setUp() {
        // 在每個測試前創建一個新的 RoomType 物件
        roomType = RoomType.builder()
                .roomTypeName("Deluxe Room")
                .roomTypeMaxCount(5)
                .bedType("King Size")
                .area(45.0)
                .bathroomType("En Suite")
                .roomTypeDescription("Spacious deluxe room with ocean view.")
                .isHandicap(false)
                .adultCapacity(2)
                .childrenCapacity(2)
                .maxCapacity(4)
                .unitPrice(150.0)
                .additionalPricePerPerson(20.0)
                .overTimeRatio(1.5)
                .build();
    }

    @Test
    void testCreateRoomType() {
        // 儲存 RoomType
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        assertNotNull(savedRoomType.getRoomTypeId());
        assertEquals("Deluxe Room", savedRoomType.getRoomTypeName());
    }

    @Test
    void testFindRoomTypeById() {
        // 儲存 RoomType
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // 透過 ID 查詢 RoomType
        RoomType foundRoomType = roomTypeRepository.findById(savedRoomType.getRoomTypeId()).orElse(null);

        assertNotNull(foundRoomType);
        assertEquals(savedRoomType.getRoomTypeId(), foundRoomType.getRoomTypeId());
        assertEquals(savedRoomType.getRoomTypeName(), foundRoomType.getRoomTypeName());
    }

    @Test
    void testUpdateRoomType() {
        // 儲存 RoomType
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // 更新 RoomType 的名稱
        savedRoomType.setRoomTypeName("Updated Deluxe Room");
        RoomType updatedRoomType = roomTypeRepository.save(savedRoomType);

        assertEquals("Updated Deluxe Room", updatedRoomType.getRoomTypeName());
    }

    @Test
    void testDeleteRoomType() {
        // 儲存 RoomType
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        // 刪除 RoomType
        roomTypeRepository.delete(savedRoomType);

        // 驗證 RoomType 是否已被刪除
        RoomType foundRoomType = roomTypeRepository.findById(savedRoomType.getRoomTypeId()).orElse(null);
        assertNull(foundRoomType);
    }
}
