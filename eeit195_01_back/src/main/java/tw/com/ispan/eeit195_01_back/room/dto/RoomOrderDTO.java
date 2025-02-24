package tw.com.ispan.eeit195_01_back.room.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class RoomOrderDTO {

    @NotNull(message = "請輸入會員Id")
    private Integer memberId;
    private String roomOrderId;
    @NotNull(message = "請輸入房客數量")
    private Integer residentCount;
    // 使用點數折抵後的總價
    private Double totalPayment;
    // 欲使用的點數
    private Integer usedPoints;
    // 點數折抵前的總價
    private Double totalPaymentBeforeUsedPoints;
    @NotNull(message = "請輸入入住日期")
    private LocalDate startingDate;
    @NotNull(message = "請輸入退房日期")
    private LocalDate leavingDate;
    // 用戶選擇的房型和對應的數量
    private Set<RoomTypeOrderDTO> roomTypeOrderDTOs;
    private Map<String, String> message;

}
