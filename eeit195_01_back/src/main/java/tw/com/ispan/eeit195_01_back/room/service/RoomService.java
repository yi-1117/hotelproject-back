package tw.com.ispan.eeit195_01_back.room.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.Room;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.dto.RoomDTO;
import tw.com.ispan.eeit195_01_back.room.repository.RoomRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public RoomDTO createRooms(RoomDTO roomDTO, Integer roomCount) {
        Map<String, String> message = roomDTO.getMessage();
        Optional<RoomType> optional = roomTypeRepository.findById(roomDTO.getRoomTypeId());
        if (optional.isEmpty()) {
            message.put("errorRoomType", "指定房型不存在");
            return RoomDTO.builder().message(message).build();
        }
        RoomType roomType = optional.get();
        for (int i = 0; i < roomCount; i++) {
            Integer currentRoomNumber = roomDTO.getRoomNumberStart() + i;
            if (roomRepository.existsByRoomNumber(currentRoomNumber)) {
                message.put("errorRoomNumber", "房號已存在");
                throw new IllegalArgumentException("房號 " + currentRoomNumber + " 已存在，將回滾操作");
            }
            Room room = Room.builder()
                    .buildingId(roomDTO.getBuildingId())
                    .roomFloor(roomDTO.getRoomFloor())
                    .roomNumber(currentRoomNumber)
                    .roomType(roomType)
                    .build();
            roomRepository.save(room);
        }
        message.put("success", "已添加" + roomCount + "個房間");
        return RoomDTO.builder().message(message).build();
    }

    public RoomDTO updateRooms(RoomDTO roomDTO, Integer roomCount) {
        Map<String, String> message = roomDTO.getMessage();
        Optional<RoomType> optional = roomTypeRepository.findById(roomDTO.getRoomTypeId());
        if (optional.isEmpty()) {
            message.put("errorRoomType", "指定房型不存在");
            return RoomDTO.builder().message(message).build();
        }
        RoomType roomType = optional.get();
        for (int i = 0; i < roomCount; i++) {
            Integer currentRoomNumber = roomDTO.getRoomNumberStart() + i;
            if (!roomRepository.existsByRoomNumber(currentRoomNumber)) {
                message.put("errorRoomNumber", "房號 " + currentRoomNumber + " 不存在");
                throw new IllegalArgumentException("房號 " + currentRoomNumber + " 不存在，將回滾操作");
            }
            Room room = Room.builder()
                    .buildingId(roomDTO.getBuildingId())
                    .roomFloor(roomDTO.getRoomFloor())
                    .roomNumber(currentRoomNumber)
                    .roomType(roomType)
                    .build();
            roomRepository.save(room);
        }
        message.put("success", "已修改" + roomCount + "個房間");
        return RoomDTO.builder().message(message).build();
    }

    public RoomDTO deleteRooms(Integer roomNumberStart, Integer roomCount) {
        Map<String, String> message = new LinkedHashMap<>();
        for (int i = roomNumberStart; i < roomNumberStart + roomCount; i++) {
            if (!roomRepository.existsByRoomNumber(i)) {
                message.put("errorRoomNumber", i + "號房不存在");
                throw new IllegalArgumentException("查無房號，將回滾操作");
            }
            roomRepository.deleteByRoomNumber(i);
        }
        message.put("success",
                "已刪除從" + roomNumberStart + "到" + (roomNumberStart + roomCount - 1) + "共" + roomCount + "個房間");
        return RoomDTO.builder().message(message).build();
    }

    public List<Room> selectWithRange(Integer roomNumberStart, Integer roomNumberEnd) {
        List<Room> rooms = new ArrayList<>();
        for (int i = roomNumberStart; i <= roomNumberEnd; i++) {
            rooms.add(roomRepository.findByRoomNumber(i));
        }
        return rooms;
    }

    // public Optional<Room> createRoom(Room room) {
    // if (room == null) {
    // return Optional.empty();
    // }
    // return Optional.of(roomRepository.save(room));
    // }

    // public Optional<Room> updateRoom(Room room) {
    // if (room == null || room.getRoomId() == null ||
    // !roomRepository.existsById(room.getRoomId())) {
    // return Optional.empty();
    // }
    // return Optional.of(roomRepository.save(room));
    // }

    // public boolean deleteRoomById(Integer id) {
    // if (id == null || !roomRepository.existsById(id)) {
    // return false;
    // }
    // roomRepository.deleteById(id);
    // return true;
    // }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }
}
