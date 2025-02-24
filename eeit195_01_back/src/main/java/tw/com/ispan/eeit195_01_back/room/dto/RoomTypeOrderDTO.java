package tw.com.ispan.eeit195_01_back.room.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomTypeOrderDTO {

    private Integer roomTypeId;
    private String roomTypeName;
    private Integer roomCount;

    @Override
    public String toString() {
        return roomTypeName + ":" + roomCount + "間";
    }
}
