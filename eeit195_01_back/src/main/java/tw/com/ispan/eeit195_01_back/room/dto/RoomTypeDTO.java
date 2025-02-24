package tw.com.ispan.eeit195_01_back.room.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class RoomTypeDTO {

    private RoomType roomType;
    private Map<String, String> message;
}
