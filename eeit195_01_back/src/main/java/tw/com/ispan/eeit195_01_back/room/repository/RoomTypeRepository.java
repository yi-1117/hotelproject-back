package tw.com.ispan.eeit195_01_back.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.dao.RoomTypeDAO;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer>, RoomTypeDAO {
    boolean existsByRoomTypeName(String roomTypeName);
}
