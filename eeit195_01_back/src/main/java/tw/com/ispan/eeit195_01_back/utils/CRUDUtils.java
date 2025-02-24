package tw.com.ispan.eeit195_01_back.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CRUDUtils {

    public static Map<String, Object> parseJsonToMap(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        try {
            map = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // @SuppressWarnings({ "rawtypes", "unchecked" })
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <T> List<Predicate> buildPredicatesFromMap(
            Map<String, Object> conditions,
            Root<T> root,
            CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        // 空檢查
        if (conditions == null || conditions.isEmpty()) {
            return predicates;
        }
        // 將傳入的 Map 分拆為 key-value 的 entry，逐個分析。
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 若傳入的是嵌套的 json
            if (value instanceof Map<?, ?> subMap) {
                // 把 "min":下界 和 "max":上界 的條件加進去
                Object minValue = subMap.get("min");
                Object maxValue = subMap.get("max");
                if (minValue != null && maxValue != null) {
                    if (minValue != null && minValue instanceof Comparable
                            && maxValue != null && maxValue instanceof Comparable) {
                        predicates.add(cb.greaterThanOrEqualTo(root.get(key), (Comparable) minValue));
                        predicates.add(cb.lessThanOrEqualTo(root.get(key), (Comparable) maxValue));
                    }
                }
            }
            // 字串的後綴模糊查詢，前綴大幅降低 SQL 效能 (無法使用 index) 故不採用
            else {
                predicates.add(cb.like(root.get(key), value.toString() + "%"));
            }
        }
        return predicates;
    }
}
