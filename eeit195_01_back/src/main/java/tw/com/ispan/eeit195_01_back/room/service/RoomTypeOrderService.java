package tw.com.ispan.eeit195_01_back.room.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;
import tw.com.ispan.eeit195_01_back.room.dto.RoomTypeOrderDTO;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeOrderRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomTypeOrderService {

    private final RoomTypeOrderRepository roomTypeOrderRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeOrderService(RoomTypeOrderRepository roomTypeOrderRepository,
            RoomTypeRepository roomTypeRepository) {
        this.roomTypeOrderRepository = roomTypeOrderRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public Optional<RoomTypeOrder> createRoomTypeOrder(RoomTypeOrder roomTypeOrder) {
        if (roomTypeOrder == null) {
            return Optional.empty();
        }
        return Optional.of(roomTypeOrderRepository.save(roomTypeOrder));
    }

    public Optional<RoomTypeOrder> updateRoomTypeOrder(RoomTypeOrder roomTypeOrder) {
        if (roomTypeOrder == null) {
            return Optional.empty();
        }
        return Optional.of(roomTypeOrderRepository.save(roomTypeOrder));
    }

    public boolean deleteRoomTypeOrderById(Integer id) {
        if (id == null || !roomTypeOrderRepository.existsById(id)) {
            return false;
        }
        roomTypeOrderRepository.deleteById(id);
        return true;
    }

    public List<RoomTypeOrder> findAll() {
        return roomTypeOrderRepository.findAll();
    }

    public RoomTypeOrder convertDTO(RoomTypeOrderDTO roomTypeOrderDTO) {
        Optional<RoomType> optional = roomTypeRepository.findById(roomTypeOrderDTO.getRoomTypeId());
        if (optional.isEmpty()) {
            return null;
        }
        RoomType roomType = optional.get();
        Integer roomCount = roomTypeOrderDTO.getRoomCount();
        RoomTypeOrder roomTypeOrder = RoomTypeOrder.builder()
                .roomType(roomType)
                .roomCount(roomCount)
                .build();
        return roomTypeOrder;
    }

}
