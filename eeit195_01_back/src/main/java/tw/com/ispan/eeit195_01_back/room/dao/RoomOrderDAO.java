package tw.com.ispan.eeit195_01_back.room.dao;

import java.util.List;
import java.util.Map;

import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;

public interface RoomOrderDAO {
    public List<RoomOrder> findByConditions(Map<String, Object> conditions);
    public List<RoomOrder> findAll();
}
