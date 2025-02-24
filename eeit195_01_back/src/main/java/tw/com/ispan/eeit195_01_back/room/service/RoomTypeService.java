package tw.com.ispan.eeit195_01_back.room.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeService(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    public Optional<RoomType> createRoomType(RoomType roomType) {
        if (roomType == null || roomTypeRepository.existsByRoomTypeName(roomType.getRoomTypeName())) {
            return Optional.empty();
        }
        return Optional.of(roomTypeRepository.save(roomType));
    }

    public Optional<RoomType> updateRoomType(RoomType roomType) {
        if (roomType == null || roomType.getRoomTypeName() == null
                || roomTypeRepository.findByRoomTypeName(roomType.getRoomTypeName()).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(roomTypeRepository.save(roomType));
    }

    public boolean deleteRoomTypeByName(String name) {
        if (name == null || roomTypeRepository.findByRoomTypeName(name).isEmpty()) {
            return false;
        }
        roomTypeRepository.delete(roomTypeRepository.findByRoomTypeName(name).get());
        return true;
    }

    public RoomType findById(Integer roomTypeId) {
        if (roomTypeRepository.existsById(roomTypeId)) {
            return roomTypeRepository.findById(roomTypeId).get();
        }
        return null;
    }

    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

}
