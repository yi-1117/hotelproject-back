package tw.com.ispan.eeit195_01_back.room.dao;

import java.util.Optional;

import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;

public interface RoomTypeOrderDAO {

    public Optional<RoomTypeOrder> findRoomTypeOrderByNameAndCount(String name, Integer count);
}