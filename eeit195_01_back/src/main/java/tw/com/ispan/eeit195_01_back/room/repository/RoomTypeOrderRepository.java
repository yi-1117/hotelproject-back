package tw.com.ispan.eeit195_01_back.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;
import tw.com.ispan.eeit195_01_back.room.dao.RoomTypeOrderDAO;

public interface RoomTypeOrderRepository extends JpaRepository<RoomTypeOrder, Integer>, RoomTypeOrderDAO {
}
