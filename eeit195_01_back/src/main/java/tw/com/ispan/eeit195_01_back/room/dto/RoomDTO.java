package tw.com.ispan.eeit195_01_back.room.dto;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RoomDTO {


    @NotNull(message = "必須指定起始房號")
    private Integer roomNumberStart;
    @NotNull(message = "必須指定房間樓層")
    private Integer roomFloor;
    @NotNull(message = "必須指定建築")
    private Integer buildingId;
    @NotNull(message = "必須指定房型")
    private Integer roomTypeId;
    private Map<String, String> message;
}
