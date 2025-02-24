package tw.com.ispan.eeit195_01_back.room.dao;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RoomRelatedDAO {
    public static Map<String, Object> parseJsonToMap(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }
}