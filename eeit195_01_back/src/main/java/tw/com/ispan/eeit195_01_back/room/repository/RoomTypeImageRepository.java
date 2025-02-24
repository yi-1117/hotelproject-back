package tw.com.ispan.eeit195_01_back.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeImage;

@Repository
@Transactional
public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, String> {

}
