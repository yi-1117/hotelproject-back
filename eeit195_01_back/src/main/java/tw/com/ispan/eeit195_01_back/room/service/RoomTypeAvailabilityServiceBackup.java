// package tw.com.ispan.eeit195_01_back.room.service;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;

// import jakarta.transaction.Transactional;
// import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeAvailability;
// import
// tw.com.ispan.eeit195_01_back.room.repository.RoomTypeAvailabilityRepository;

// @Service
// @Transactional
// public class RoomTypeAvailabilityServiceBackup {

// private final RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;

// public RoomTypeAvailabilityServiceBackup(RoomTypeAvailabilityRepository
// roomTypeAvailabilityRepository) {
// this.roomTypeAvailabilityRepository = roomTypeAvailabilityRepository;
// }

// public Optional<RoomTypeAvailability>
// createRoomTypeAvailability(RoomTypeAvailability roomTypeAvailability) {
// if (roomTypeAvailability == null) {
// return Optional.empty();
// }
// return
// Optional.of(roomTypeAvailabilityRepository.save(roomTypeAvailability));
// }

// public Optional<RoomTypeAvailability>
// updateRoomTypeAvailability(RoomTypeAvailability roomTypeAvailability) {
// if (roomTypeAvailability == null ||
// roomTypeAvailability.getRoomTypeAvailabilityId() == null ||
// !roomTypeAvailabilityRepository.existsById(roomTypeAvailability.getRoomTypeAvailabilityId()))
// {
// return Optional.empty();
// }
// return
// Optional.of(roomTypeAvailabilityRepository.save(roomTypeAvailability));
// }

// public boolean deleteRoomTypeAvailabilityById(Integer id) {
// if (id == null || !roomTypeAvailabilityRepository.existsById(id)) {
// return false;
// }
// roomTypeAvailabilityRepository.deleteById(id);
// return true;
// }

// public List<RoomTypeAvailability> findAll() {
// return roomTypeAvailabilityRepository.findAll();
// }
// }
