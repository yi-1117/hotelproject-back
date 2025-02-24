package tw.com.ispan.eeit195_01_back.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeAvailability;
import tw.com.ispan.eeit195_01_back.room.dao.RoomTypeAvailabilityDAO;

public interface RoomTypeAvailabilityRepository
                extends JpaRepository<RoomTypeAvailability, String>, RoomTypeAvailabilityDAO {

}
