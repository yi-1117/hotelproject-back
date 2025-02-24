package tw.com.ispan.eeit195_01_back.room.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;

@SpringBootTest
@Transactional
public class RoomTypeOrderRepositoryTest {

    @Autowired
    private RoomTypeOrderRepository roomTypeOrderRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomOrderRepository roomOrderRepository;

    private RoomTypeOrder roomTypeOrder;
    private RoomType roomType;
    private RoomOrder roomOrder;

    @BeforeEach
    void setUp() {
        // 創建 RoomType 物件
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

        // 創建 RoomOrder 物件
        roomOrder = RoomOrder.builder()
                .memberId(1)
                .roomOfferId(1)
                .orderStatus("Booked")
                .residentCount(2)
                .totalPayment(200.0)
                .orderTime(LocalDateTime.now())
                .startingTime(LocalDateTime.now().plusDays(1))
                .leavingTime(LocalDateTime.now().plusDays(3))
                .checkinTime(LocalDateTime.now().plusDays(1))
                .checkoutTime(LocalDateTime.now().plusDays(3))
                .isOverTime(false)
                .isRoomService(false)
                .build();

        roomOrder = roomOrderRepository.save(roomOrder); // 儲存 RoomOrder

        // 創建 RoomTypeOrder 物件
        roomTypeOrder = RoomTypeOrder.builder()
                .roomCount(2)
                .roomTypeBean(roomType)
                .roomOrder(roomOrder)
                .build();

        roomTypeOrder = roomTypeOrderRepository.save(roomTypeOrder); // 儲存 RoomTypeOrder
    }

    @Test
    void testCreateRoomTypeOrder() {
        // 儲存 RoomTypeOrder
        RoomTypeOrder savedRoomTypeOrder = roomTypeOrderRepository.save(roomTypeOrder);

        assertNotNull(savedRoomTypeOrder.getRoomListId());
        assertEquals(2, savedRoomTypeOrder.getRoomCount());
        assertEquals("Standard Room", savedRoomTypeOrder.getRoomTypeBean().getRoomTypeName());
        assertEquals("Booked", savedRoomTypeOrder.getRoomOrder().getOrderStatus());
    }

    @Test
    void testFindRoomTypeOrderById() {
        // 儲存 RoomTypeOrder
        RoomTypeOrder savedRoomTypeOrder = roomTypeOrderRepository.save(roomTypeOrder);

        // 透過 ID 查詢 RoomTypeOrder
        RoomTypeOrder foundRoomTypeOrder = roomTypeOrderRepository.findById(savedRoomTypeOrder.getRoomListId())
                .orElse(null);

        assertNotNull(foundRoomTypeOrder);
        assertEquals(savedRoomTypeOrder.getRoomListId(), foundRoomTypeOrder.getRoomListId());
        assertEquals(savedRoomTypeOrder.getRoomCount(), foundRoomTypeOrder.getRoomCount());
    }

    @Test
    void testUpdateRoomTypeOrder() {
        // 儲存 RoomTypeOrder
        RoomTypeOrder savedRoomTypeOrder = roomTypeOrderRepository.save(roomTypeOrder);

        // 更新 RoomTypeOrder 的房間數量
        savedRoomTypeOrder.setRoomCount(3);
        RoomTypeOrder updatedRoomTypeOrder = roomTypeOrderRepository.save(savedRoomTypeOrder);

        assertEquals(3, updatedRoomTypeOrder.getRoomCount());
    }

    @Test
    void testDeleteRoomTypeOrder() {
        // 儲存 RoomTypeOrder
        RoomTypeOrder savedRoomTypeOrder = roomTypeOrderRepository.save(roomTypeOrder);

        // 刪除 RoomTypeOrder
        roomTypeOrderRepository.delete(savedRoomTypeOrder);

        // 驗證 RoomTypeOrder 是否已被刪除
        RoomTypeOrder foundRoomTypeOrder = roomTypeOrderRepository.findById(savedRoomTypeOrder.getRoomListId())
                .orElse(null);
        assertNull(foundRoomTypeOrder);
    }
}
