package tw.com.ispan.eeit195_01_back.room.dao;

import java.util.Optional;

import tw.com.ispan.eeit195_01_back.room.bean.RoomType;

public interface RoomTypeDAO {
    Optional<RoomType> findByRoomTypeName(String name);
}
