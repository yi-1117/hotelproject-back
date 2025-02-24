package tw.com.ispan.eeit195_01_back.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.room.bean.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsByRoomNumber(Integer roomNumber);

    void deleteByRoomNumber(Integer roomNumber);

    Room findByRoomNumber(Integer roomNumber);
}
