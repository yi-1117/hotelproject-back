package tw.com.ispan.eeit195_01_back.room.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.Room;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;

@SpringBootTest
@Transactional
public class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Room room;
    private RoomType roomType;

    @BeforeEach
    void setUp() {
        // 在每個測試前創建一個新的 RoomType 和 Room 物件
        roomType = RoomType.builder()
                .roomTypeName("Standard Room")
                .roomTypeMaxCount(5)
                .bedType("Queen Size")
                .area(35.0)
                .bathroomType("Shared")
                .roomTypeDescription("Comfortable standard room.")
                .isHandicap(false)
                .adultCapacity(2)
                .childrenCapacity(2)
                .maxCapacity(4)
                .unitPrice(100.0)
                .additionalPricePerPerson(15.0)
                .overTimeRatio(1.2)
                .build();

        roomType = roomTypeRepository.save(roomType); // 儲存 RoomType

        room = Room.builder()
                .roomNumber(101)
                .roomFloor(1)
                .buildingId(1)
                .roomTypeBean(roomType)
                .build();
    }

    @Test
    void testCreateRoom() {
        // 儲存 Room
        Room savedRoom = roomRepository.save(room);
        assertNotNull(savedRoom.getRoomId());
        assertEquals(101, savedRoom.getRoomNumber());
        assertEquals(1, savedRoom.getRoomFloor());
    }

    @Test
    void testFindRoomById() {
        // 儲存 Room
        Room savedRoom = roomRepository.save(room);

        // 透過 ID 查詢 Room
        Room foundRoom = roomRepository.findById(savedRoom.getRoomId()).orElse(null);

        assertNotNull(foundRoom);
        assertEquals(savedRoom.getRoomId(), foundRoom.getRoomId());
        assertEquals(savedRoom.getRoomNumber(), foundRoom.getRoomNumber());
    }

    @Test
    void testUpdateRoom() {
        // 儲存 Room
        Room savedRoom = roomRepository.save(room);

        // 更新 Room 的房號
        savedRoom.setRoomNumber(102);
        Room updatedRoom = roomRepository.save(savedRoom);

        assertEquals(102, updatedRoom.getRoomNumber());
    }

    @Test
    void testDeleteRoom() {
        // 儲存 Room
        Room savedRoom = roomRepository.save(room);

        // 刪除 Room
        roomRepository.delete(savedRoom);

        // 驗證 Room 是否已被刪除
        Room foundRoom = roomRepository.findById(savedRoom.getRoomId()).orElse(null);
        assertNull(foundRoom);
    }
}
