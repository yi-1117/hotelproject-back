package tw.com.ispan.eeit195_01_back.room.repository;

import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public class RoomStatusRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public RoomStatusRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 使用0~31的數值管理房況(對應 00000 到 11111)
    public void updateRoomStatus(String roomId, Integer roomStatus, Integer orderId) {
        try {
            // 取得當前的 roomStatus
            Object oldRoomStatusObj = redisTemplate.opsForHash().get(roomId, "roomStatus");

            // 只更新 roomStatus
            if (roomStatus != null) {
                if (oldRoomStatusObj == null) {
                    // 初始狀態 (如果 roomStatus 不存在，設為 31)
                    redisTemplate.opsForHash().put(roomId, "roomStatus", 31);
                } else {
                    // 更新狀態 (使用 XOR 轉換 oldRoomStatus 為 int 型態)
                    int oldRoomStatus = (int) oldRoomStatusObj;
                    redisTemplate.opsForHash().put(roomId, "roomStatus", oldRoomStatus ^ roomStatus); // XOR 運算
                }
            }

            // 只更新 orderId
            if (orderId != null) {
                redisTemplate.opsForHash().put(roomId, "orderId", orderId); // 更新 orderId (防止為 null)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Object, Object> getRoomStatus(String roomId) {
        return redisTemplate.opsForHash().entries(roomId);
    }

}
