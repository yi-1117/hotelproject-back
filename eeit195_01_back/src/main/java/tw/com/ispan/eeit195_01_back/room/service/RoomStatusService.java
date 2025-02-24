package tw.com.ispan.eeit195_01_back.room.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.Room;
import tw.com.ispan.eeit195_01_back.room.repository.RoomRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomStatusRepository;

@Service
@Transactional
public class RoomStatusService {

    private final RoomStatusRepository roomStatusRepository;
    private final RoomRepository roomRepository;

    public RoomStatusService(RoomStatusRepository roomStatusRepository,
            RoomRepository roomRepository) {
        this.roomStatusRepository = roomStatusRepository;
        this.roomRepository = roomRepository;
    }

    // 更新單一房態 (checkin,checkout)
    public Map<Object, Object> updateOneRoom(String roomId, Integer roomStatus, Integer orderId) {
        roomStatusRepository.updateRoomStatus(roomId, roomStatus, orderId);
        return roomStatusRepository.getRoomStatus(roomId);
    }

    // 更新複數房態
    public List<Map<Object, Object>> updateMultipleRooms(List<String> roomIds, Integer roomStatus, Integer orderId) {
        List<Map<Object, Object>> newStatus = new ArrayList<>();
        for (String roomId : roomIds) {
            roomStatusRepository.updateRoomStatus(roomId, roomStatus, orderId);
            newStatus.add(roomStatusRepository.getRoomStatus(roomId));
        }
        return newStatus;
    }

    // 獲取房態
    public Map<Object, Object> getStatus(String roomId) {
        return roomStatusRepository.getRoomStatus(roomId);
    }

    // 獲取所有房態
    public List<Map<Object, Object>> getAllStatus() {
        List<Map<Object, Object>> allStatus = new ArrayList<>();
        List<Room> allRooms = roomRepository.findAll();
        for (Room room : allRooms) {
            allStatus.add(roomStatusRepository.getRoomStatus(room.getRoomId().toString()));
        }
        return allStatus;
    }
}
