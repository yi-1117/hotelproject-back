package tw.com.ispan.eeit195_01_back.room.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeAvailability;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;
import tw.com.ispan.eeit195_01_back.room.dto.RoomOrderDTO;
import tw.com.ispan.eeit195_01_back.room.dto.RoomTypeOrderDTO;
import tw.com.ispan.eeit195_01_back.room.repository.RoomOrderRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeAvailabilityRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeOrderRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeRepository;

@Service
public class RoomTypeAvailabilityService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RoomTypeAvailabilityRepository roomTypeAvailabilityRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomOrderRepository roomOrderRepository;
    private final RoomTypeOrderRepository roomTypeOrderRepository;

    public RoomTypeAvailabilityService(RedisTemplate<String, Object> redisTemplate,
            RoomTypeAvailabilityRepository roomTypeAvailabilityRepository,
            RoomTypeRepository roomTypeRepository,
            RoomOrderRepository roomOrderRepository,
            RoomTypeOrderRepository roomTypeOrderRepository) {
        this.redisTemplate = redisTemplate;
        this.roomTypeAvailabilityRepository = roomTypeAvailabilityRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomOrderRepository = roomOrderRepository;
        this.roomTypeOrderRepository = roomTypeOrderRepository;
    }

    public Map<String, Object> getRoomCount(List<String> keys) {
        // 用 controller 傳進來的 keys 從 redisTemplate 找出相對應的 values 然後連 keys 一起回傳

        Map<String, Object> roomCounts = new LinkedHashMap<>();
        for (String key : keys) {
            Optional<Integer> value = roomTypeAvailabilityRepository.getCount(key);
            if (value.isPresent()) {
                // 如果 key 有對應的空房數則放入 roomCounts
                roomCounts.put(key, value.get());
            }
            // 如果 key 沒有對應的空房數則放入 0
            else {
                roomCounts.put(key, 0);
            }
        }
        return roomCounts;
    }

    public void updateRoomAvailabilityAfterPayment(RoomOrderDTO roomOrderDTO) {
        for (RoomTypeOrderDTO roomTypeOrderDTO : roomOrderDTO.getRoomTypeOrderDTOs()) {
            String roomTypeName = roomTypeOrderDTO.getRoomTypeName();
            int roomCount = roomTypeOrderDTO.getRoomCount();

            LocalDate date = roomOrderDTO.getStartingDate();
            while (!date.isAfter(roomOrderDTO.getLeavingDate().minusDays(1))) {
                String key = date + ":" + roomTypeName;
                Integer availableCount = (Integer) redisTemplate.opsForValue().get(key);

                if (availableCount == null || availableCount < roomCount) {
                    throw new IllegalStateException("房間數量不足，無法完成訂單");
                }

                // 更新 Redis
                redisTemplate.opsForValue().set(key, availableCount - roomCount);

                // 更新 MSSQL
                Optional<RoomTypeAvailability> roomTypeAvailabilityOptional = roomTypeAvailabilityRepository
                        .findById(key);
                roomTypeAvailabilityOptional.ifPresent(roomTypeAvailability -> {
                    roomTypeAvailability.setAvailableCount(availableCount - roomCount);
                    roomTypeAvailabilityRepository.save(roomTypeAvailability);
                });

                date = date.plusDays(1);
            }
        }
    }

    public void updateRoomAvailabilityBeforeDeletion(String orderId) {
        // 根據訂單 ID 查詢訂單
        Optional<RoomOrder> optional = roomOrderRepository.findById(orderId);
        if (optional.isEmpty()) {
            System.out.println(orderId + " 訂單不存在");
        }
        RoomOrder roomOrder = optional.get();
        // 用外鍵(訂單編號)尋找房型及數量的組合(Set<RoomTypeOrder>)
        Set<RoomTypeOrder> roomTypeOrders = roomOrder.getRoomTypeOrders();

        // 遍歷訂單內入住日期到離開日期之間的每一天
        LocalDate startDate = roomOrder.getStartingTime().toLocalDate();
        LocalDate endDate = roomOrder.getLeavingTime().toLocalDate();

        while (!startDate.isAfter(endDate.minusDays(1))) {
            for (RoomTypeOrder roomTypeOrder : roomTypeOrders) {
                String roomTypeName = roomTypeOrder.getRoomType().getRoomTypeName();
                int roomCount = roomTypeOrder.getRoomCount();

                String key = startDate + ":" + roomTypeName;

                // 讀取 Redis 中當日的可用房間數量
                Integer currentAvailableCount = (Integer) redisTemplate.opsForValue().get(key);

                // 如果 Redis 中找不到數量，初始化為 0
                int newAvailableCount = (currentAvailableCount == null ? 0 : currentAvailableCount) + roomCount;

                // 在 Redis 中將空房數量加回
                redisTemplate.opsForValue().set(key, newAvailableCount);

                // 更新資料庫中的房型可用數量
                roomTypeAvailabilityRepository.findById(key).ifPresent(roomTypeAvailability -> {
                    roomTypeAvailability.setAvailableCount(newAvailableCount);
                    roomTypeAvailabilityRepository.save(roomTypeAvailability);
                });
            }
            startDate = startDate.plusDays(1);
        }
    }

    public void initializeRedis() {
        // MSSQL 傳資料給 Redis
        // redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        System.err.println("將 MSSQL 的資料快取至 Redis");
        List<RoomTypeAvailability> sqlDatas = roomTypeAvailabilityRepository.findAll();
        int num = 0;
        for (RoomTypeAvailability data : sqlDatas) {
            redisTemplate.opsForValue().set(data.getRoomTypeAvailabilityId(), data.getAvailableCount());
            num++;
        }
        System.err.println(
                "快取完成，共" + redisTemplate.keys("*").size() + "筆資料，迴圈執行" + num + "次，資料庫有" + sqlDatas.size() + "筆資料。");
    }

    public void initializeMSSQL(LocalDate firstDay, LocalDate lastDay) {
        // 把 2025 年的空房準備好
        roomTypeAvailabilityRepository.deleteAll();
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        for (RoomType roomType : roomTypes) {
            LocalDate day = firstDay;
            while (!day.isAfter(lastDay)) {
                // 使用日期和房型名稱拼接成 key
                String key = day + ":" + roomType.getRoomTypeName();
                RoomTypeAvailability roomTypeAvailability = RoomTypeAvailability.builder()
                        .roomTypeAvailabilityId(key)
                        .availableCount(roomType.getRoomTypeMaxCount())
                        .build();
                roomTypeAvailabilityRepository.save(roomTypeAvailability);
                day = day.plusDays(1);
            }
        }
        System.err.println("匯入完成");
    }

}
