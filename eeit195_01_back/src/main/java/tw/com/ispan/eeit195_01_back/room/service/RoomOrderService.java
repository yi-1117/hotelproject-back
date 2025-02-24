package tw.com.ispan.eeit195_01_back.room.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.repository.MemberDetailsRepository;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;
import tw.com.ispan.eeit195_01_back.room.dto.ConditionDTO;
import tw.com.ispan.eeit195_01_back.room.dto.RoomOrderDTO;
import tw.com.ispan.eeit195_01_back.room.dto.RoomTypeOrderDTO;
import tw.com.ispan.eeit195_01_back.room.repository.RoomOrderRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeOrderRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomOrderService {
    @PersistenceContext
    private EntityManager entityManager;

    private final RoomTypeRepository roomTypeRepository;
    private final RoomOrderRepository roomOrderRepository;
    private final RoomTypeOrderRepository roomTypeOrderRepository;
    private final MemberRepository memberRepository;
    private final RoomTypeOrderService roomTypeOrderService;
    private final RedisTemplate<String, Object> redisTemplate;

    public RoomOrderService(RoomTypeRepository roomTypeRepository,
            RoomOrderRepository roomOrderRepository,
            RoomTypeOrderRepository roomTypeOrderRepository,
            MemberRepository memberRepository,
            MemberDetailsRepository memberDetailsRepository,
            RoomTypeOrderService roomTypeOrderService,
            RedisTemplate<String, Object> redisTemplate) {
        this.roomOrderRepository = roomOrderRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomTypeOrderRepository = roomTypeOrderRepository;
        this.roomTypeOrderService = roomTypeOrderService;
        this.redisTemplate = redisTemplate;
        this.memberRepository = memberRepository;
    }

    public Optional<RoomOrder> createRoomOrder(RoomOrder roomOrder) {
        if (roomOrder == null) {
            return Optional.empty();
        }
        return Optional.of(roomOrderRepository.save(roomOrder));
    }

    public Optional<RoomOrder> updateRoomOrder(RoomOrder roomOrder) {
        if (roomOrder == null || roomOrder.getRoomOrderId() == null
                || !roomOrderRepository.existsById(roomOrder.getRoomOrderId())) {
            return Optional.empty();
        }
        return Optional.of(roomOrderRepository.save(roomOrder));
    }

    public void deleteRoomOrderById(String id) {
        if (id != null && roomOrderRepository.existsById(id)) {
            roomOrderRepository.deleteById(id);
        }
    }

    public List<RoomOrder> findAll() {
        return roomOrderRepository.findAll();
    }

    // 依據條件動態查詢
    public List<RoomOrder> queryRoomOrders(ConditionDTO conditionDTO) {
        // 檢查條件是否為空
        if (conditionDTO.getAdditionalConditions() == null || conditionDTO.getAdditionalConditions().isEmpty()) {
            return roomOrderRepository.findAll(); // 如果沒有條件，回傳所有訂單
        }

        // 根據條件建立 Predicate 並查詢
        return roomOrderRepository.findByConditions(conditionDTO.getAdditionalConditions());
    }

    // 將 DTO 轉換為 entity
    public RoomOrder convertDTO(RoomOrderDTO roomOrderDTO) {
        Set<RoomTypeOrderDTO> roomTypeOrderDTOs = roomOrderDTO.getRoomTypeOrderDTOs();
        Set<RoomTypeOrder> roomTypeOrders = new HashSet<>();
        for (RoomTypeOrderDTO dto : roomTypeOrderDTOs) {
            System.out.println("dtoId" + dto.getRoomTypeId());
            Optional<RoomType> optional = roomTypeRepository.findById(dto.getRoomTypeId());
            if (optional.isEmpty()) {
                return null;
            }
            RoomTypeOrder roomTypeOrder = roomTypeOrderService.convertDTO(dto);
            roomTypeOrderRepository.save(roomTypeOrder);
            roomTypeOrders.add(roomTypeOrder);
        }
        System.out.println("build");
        RoomOrder roomOrder = RoomOrder.builder()
                .member(memberRepository.findById(roomOrderDTO.getMemberId()).get())
                .roomOrderId(roomOrderDTO.getRoomOrderId())
                .roomTypeOrders(roomTypeOrders)
                .checkinTime(null)
                .checkoutTime(null)
                .orderStatus(null)
                .orderTime(LocalDateTime.now())
                .residentCount(roomOrderDTO.getResidentCount())
                .startingTime(roomOrderDTO.getStartingDate().atTime(14, 0))
                .leavingTime(roomOrderDTO.getLeavingDate().atTime(10, 0))
                .build();
        return roomOrder;
    }

    // 接收 RoomOrderDTO 並調取資料庫資料驗證其內容合法性
    public RoomOrderDTO previewOrder(RoomOrderDTO roomOrderDTO) {

        System.err.println("price" + roomOrderDTO.getTotalPaymentBeforeUsedPoints());
        Map<String, String> message = new HashMap<>();
        int totalCapacity = 0; // 總房間容量計算
        Set<RoomTypeOrderDTO> roomTypeOrderDTOs = roomOrderDTO.getRoomTypeOrderDTOs();

        // 驗證房型是否存在並計算容量
        for (RoomTypeOrderDTO rtoDTO : roomTypeOrderDTOs) {
            Integer roomTypeId = rtoDTO.getRoomTypeId();

            // 查詢房型是否存在
            RoomType roomType = roomTypeRepository.findById(roomTypeId)
                    .orElse(null);

            if (roomType == null) {
                message.put("errorRoomType", "房型不存在");
                return buildErrorResponse(roomOrderDTO, message);
            }

            // 計算房間總容量
            totalCapacity += roomType.getMaxCapacity() * rtoDTO.getRoomCount();
        }

        // 驗證房間容量是否足夠
        if (roomOrderDTO.getResidentCount() > totalCapacity) {
            message.put("errorResident", "房客人數 (" + roomOrderDTO.getResidentCount() +
                    ") 超過總房間容量 (" + totalCapacity + ")");
        }

        // 驗證入住期間是否有空房
        LocalDate day = roomOrderDTO.getStartingDate();
        LocalDate dayOut = roomOrderDTO.getLeavingDate();

        while (!day.isAfter(dayOut)) {
            for (RoomTypeOrderDTO rtoDTO : roomTypeOrderDTOs) {
                RoomType roomType = roomTypeRepository.findById(rtoDTO.getRoomTypeId()).get();
                int requestedRoomCount = rtoDTO.getRoomCount();

                // 查詢特定日期的空房數量
                Integer availableRoomCount = (Integer) redisTemplate.opsForValue()
                        .get(day + ":" + roomType.getRoomTypeName());

                // 如果空房不足或無法取得數據，記錄錯誤並跳出
                if (availableRoomCount == null || availableRoomCount < requestedRoomCount) {
                    message.put("errorRoomCount", day + " 的 " + roomType.getRoomTypeName() +
                            " 空房數量不足，需求: " + requestedRoomCount +
                            "，可用: " + availableRoomCount);
                    return buildErrorResponse(roomOrderDTO, message);
                }
            }
            day = day.plusDays(1);
        }

        // 建立回應，包含驗證結果
        message.put("success", "訂單內容和資料庫無衝突");

        return RoomOrderDTO.builder()
                .roomOrderId(null)
                .memberId(roomOrderDTO.getMemberId())
                .residentCount(roomOrderDTO.getResidentCount())
                .totalPayment(roomOrderDTO.getTotalPayment())
                .startingDate(roomOrderDTO.getStartingDate())
                .leavingDate(roomOrderDTO.getLeavingDate())
                .roomTypeOrderDTOs(roomOrderDTO.getRoomTypeOrderDTOs())
                .message(message)
                .build();
    }

    // 封裝錯誤回應的輔助方法
    private RoomOrderDTO buildErrorResponse(RoomOrderDTO roomOrderDTO, Map<String, String> message) {
        return RoomOrderDTO.builder()
                .message(message)
                .build();
    }

    // 付款完成後把訂單新增到資料庫
    public void confirmOrder(RoomOrderDTO roomOrderDTO) {
        Set<RoomTypeOrder> roomTypeOrders = new HashSet<>();

        for (RoomTypeOrderDTO roomTypeOrderDTO : roomOrderDTO.getRoomTypeOrderDTOs()) {
            Integer roomTypeId = roomTypeOrderDTO.getRoomTypeId();
            Integer roomCount = roomTypeOrderDTO.getRoomCount();

            // 確保房型房數的組合存在，不重複利用已存在的 RoomTypeOrder
            Optional<RoomType> roomType = roomTypeRepository.findById(roomTypeId);
            if (roomType.isPresent()) {
                // 只設置 roomType 和 roomCount，讓 roomTypeOrderId 自動生成
                RoomTypeOrder newRoomTypeOrder = RoomTypeOrder.builder()
                        .roomType(roomType.get()) // 使用找到的房型
                        .roomCount(roomCount)
                        .build();

                roomTypeOrderRepository.save(newRoomTypeOrder);
                roomTypeOrders.add(newRoomTypeOrder);
            }
        }

        // 確保 member 存在
        Optional<MemberBean> member = memberRepository.findById(roomOrderDTO.getMemberId());

        RoomOrder roomOrder = RoomOrder.builder()
                .roomOrderId(roomOrderDTO.getRoomOrderId())
                .residentCount(roomOrderDTO.getResidentCount())
                .totalPayment(roomOrderDTO.getTotalPayment())
                .orderStatus("Ready")
                .orderTime(LocalDateTime.now())
                .startingTime(roomOrderDTO.getStartingDate().atTime(15, 0))
                .leavingTime(roomOrderDTO.getLeavingDate().atTime(10, 0))
                .roomTypeOrders(roomTypeOrders)
                .member(member.orElse(null)) // 若 member 不存在則設為 null
                .usedPoints(roomOrderDTO.getUsedPoints())
                .totalPaymentBeforeUsingPoints(roomOrderDTO.getTotalPaymentBeforeUsedPoints())
                .build();

        roomOrderRepository.save(roomOrder);
    }

    public RoomOrderDTO updateOrder(RoomOrderDTO roomOrderDTO) {
        Map<String, String> message = new LinkedHashMap<>();
        String orderId = roomOrderDTO.getRoomOrderId();

        // 驗證訂單是否存在
        Optional<RoomOrder> optionalRoomOrder = roomOrderRepository.findById(orderId);
        if (optionalRoomOrder.isEmpty()) {
            message.put("errorOrderId", "訂單號碼不存在");
            // return RoomOrderDTO.builder().message(message).build();
            return buildErrorResponse(roomOrderDTO, message);
        }

        // 獲取原始訂單
        RoomOrder existingOrder = optionalRoomOrder.get();

        // 更新訂單的基本資訊
        existingOrder.setResidentCount(roomOrderDTO.getResidentCount());
        existingOrder.setTotalPayment(roomOrderDTO.getTotalPayment());
        existingOrder.setOrderStatus("updated");
        existingOrder.setOrderTime(LocalDateTime.now());
        existingOrder.setStartingTime(roomOrderDTO.getStartingDate().atTime(14, 0));
        existingOrder.setLeavingTime(roomOrderDTO.getLeavingDate().atTime(10, 0));
        existingOrder.setCheckinTime(null);
        existingOrder.setCheckoutTime(null);

        // // 驗證會員是否存在並更新
        // Optional<MemberBean> optionalMember =
        // memberRepository.findById(roomOrderDTO.getMemberId());
        // if (optionalMember.isEmpty()) {
        // message.put("errorMemberId", "會員 ID 不存在");
        // return RoomOrderDTO.builder().message(message).build();
        // }
        // existingOrder.setMember(optionalMember.get());

        // 更新房型訂單
        Set<RoomTypeOrderDTO> roomTypeOrderDTOs = roomOrderDTO.getRoomTypeOrderDTOs();
        Set<RoomTypeOrder> updatedRoomTypeOrders = new HashSet<>();

        for (RoomTypeOrderDTO dto : roomTypeOrderDTOs) {
            Optional<RoomType> optionalRoomType = roomTypeRepository.findById(dto.getRoomTypeId());
            if (optionalRoomType.isEmpty()) {
                message.put("errorRoomType", "房型 ID: " + dto.getRoomTypeId() + " 不存在");
                return RoomOrderDTO.builder().message(message).build();
            }

            RoomType roomType = optionalRoomType.get();
            RoomTypeOrder roomTypeOrder = RoomTypeOrder.builder()
                    .roomType(roomType)
                    .roomCount(dto.getRoomCount())
                    .build();

            updatedRoomTypeOrders.add(roomTypeOrder);
        }

        // 設置新的房型訂單關聯
        existingOrder.setRoomTypeOrders(updatedRoomTypeOrders);

        // 保存更新後的訂單
        roomOrderRepository.save(existingOrder);

        message.put("success", "修改成功");
        return RoomOrderDTO.builder().message(message).build();
    }

    public Map<String, String> deleteOrder(String orderId) {
        Map<String, String> message = new LinkedHashMap<>();
        if (!roomOrderRepository.existsById(orderId)) {
            message.put("errorOrderId", "Id不存在");
            return message;
        }
        roomOrderRepository.deleteById(orderId);
        message.put("success", "刪除成功");
        return message;
    }

    public void save(RoomOrder roomOrder) {
        roomOrderRepository.save(roomOrder);
    }

    public List<RoomOrderDTO> findByMemberId(Integer memberId) {
        Optional<MemberBean> optional = memberRepository.findById(memberId);
        if (optional.isEmpty()) {
            return Collections.emptyList();
        }
        List<RoomOrder> roomOrders = roomOrderRepository.findByMember(optional.get());

        // 轉換為 DTO
        return roomOrders.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public RoomOrderDTO convertToDTO(RoomOrder roomOrder) {
        // 轉換 RoomTypeOrder 到 RoomTypeOrderDTO
        Set<RoomTypeOrderDTO> roomTypeOrderDTOs = new HashSet<>();
        for (RoomTypeOrder roomTypeOrder : roomOrder.getRoomTypeOrders()) {
            RoomTypeOrderDTO roomTypeOrderDTO = RoomTypeOrderDTO.builder()
                    .roomTypeId(roomTypeOrder.getRoomType().getRoomTypeId()) // 取得 roomTypeId
                    .roomTypeName(roomTypeOrder.getRoomType().getRoomTypeName()) // 取得 roomTypeName
                    .roomCount(roomTypeOrder.getRoomCount()) // 取得 roomCount
                    .build();
            roomTypeOrderDTOs.add(roomTypeOrderDTO);
        }

        // 建立 RoomOrderDTO
        RoomOrderDTO roomOrderDTO = RoomOrderDTO.builder()
                .roomOrderId(roomOrder.getRoomOrderId())
                .memberId(roomOrder.getMember().getMemberId()) // 取得 memberId
                .residentCount(roomOrder.getResidentCount()) // 取得 residentCount
                .totalPayment(roomOrder.getTotalPayment()) // 取得 totalPayment
                .usedPoints(roomOrder.getUsedPoints()) // 取得 usedPoints
                .totalPaymentBeforeUsedPoints(roomOrder.getTotalPaymentBeforeUsingPoints()) // 取得
                                                                                            // totalPaymentBeforeUsingPoints
                .startingDate(roomOrder.getStartingTime().toLocalDate()) // 取得入住日期
                .leavingDate(roomOrder.getLeavingTime().toLocalDate()) // 取得退房日期
                .roomTypeOrderDTOs(roomTypeOrderDTOs) // 設置房型訂單DTO
                .message(null) // 如果需要，可以根據需求設置 message
                .build();

        return roomOrderDTO;
    }

    public List<RoomOrder> queryOrderByConditions(Map<String, Object> conditions) {
        return roomOrderRepository.findByConditions(conditions);
    }

}
