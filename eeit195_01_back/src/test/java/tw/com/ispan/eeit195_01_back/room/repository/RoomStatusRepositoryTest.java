// package tw.com.ispan.eeit195_01_back.room.repository;

// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;

// import jakarta.transaction.Transactional;
// import tw.com.ispan.eeit195_01_back.room.bean.Room;
// import tw.com.ispan.eeit195_01_back.room.bean.RoomStatus;

// @SpringBootTest
// @Transactional
// public class RoomStatusRepositoryTest {

// @Autowired
// private RoomStatusRepository roomStatusRepository;

// @Autowired
// private RoomRepository roomRepository;

// private RoomStatus roomStatus;
// private Room room;

// @BeforeEach
// void setUp() {
// // 在每個測試前創建一個新的 Room 物件
// room = Room.builder()
// .roomNumber(101)
// .roomFloor(1)
// .buildingId(1)
// .build();
// room = roomRepository.save(room); // 儲存 Room

// // 在每個測試前創建一個新的 RoomStatus 物件
// roomStatus = RoomStatus.builder()
// .isClear(true)
// .isClean(true)
// .isFunctional(true)
// .isDisturbable(false)
// .roomBean(room) // 將 Room 關聯進來
// .build();
// }

// @Test
// void testCreateRoomStatus() {
// // 儲存 RoomStatus
// RoomStatus savedRoomStatus = roomStatusRepository.save(roomStatus);
// assertNotNull(savedRoomStatus.getRoomStatusId());
// assertEquals(true, savedRoomStatus.getIsClear());
// }

// @Test
// void testFindRoomStatusById() {
// // 儲存 RoomStatus
// RoomStatus savedRoomStatus = roomStatusRepository.save(roomStatus);

// // 透過 ID 查詢 RoomStatus
// RoomStatus foundRoomStatus =
// roomStatusRepository.findById(savedRoomStatus.getRoomStatusId()).orElse(null);

// assertNotNull(foundRoomStatus);
// assertEquals(savedRoomStatus.getRoomStatusId(),
// foundRoomStatus.getRoomStatusId());
// assertEquals(savedRoomStatus.getIsClear(), foundRoomStatus.getIsClear());
// }

// @Test
// void testUpdateRoomStatus() {
// // 儲存 RoomStatus
// RoomStatus savedRoomStatus = roomStatusRepository.save(roomStatus);

// // 更新 RoomStatus 的清潔狀態
// savedRoomStatus.setIsClean(false);
// RoomStatus updatedRoomStatus = roomStatusRepository.save(savedRoomStatus);

// assertEquals(false, updatedRoomStatus.getIsClean());
// }

// @Test
// void testDeleteRoomStatus() {
// // 儲存 RoomStatus
// RoomStatus savedRoomStatus = roomStatusRepository.save(roomStatus);

// // 刪除 RoomStatus
// roomStatusRepository.delete(savedRoomStatus);

// // 驗證 RoomStatus 是否已被刪除
// RoomStatus foundRoomStatus =
// roomStatusRepository.findById(savedRoomStatus.getRoomStatusId()).orElse(null);
// assertNull(foundRoomStatus);
// }
// }
