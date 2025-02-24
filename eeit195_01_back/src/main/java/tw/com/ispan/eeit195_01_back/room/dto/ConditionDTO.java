package tw.com.ispan.eeit195_01_back.room.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionDTO {
    private Map<String, Object> additionalConditions;
}
