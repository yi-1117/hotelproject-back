package tw.com.ispan.eeit195_01_back.room.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tw.com.ispan.eeit195_01_back.room.bean.RoomOrder;

public class RoomOrderDAOImpl implements RoomOrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<RoomOrder> findByConditions(Map<String, Object> conditions) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoomOrder> cq = cb.createQuery(RoomOrder.class);
        Root<RoomOrder> root = cq.from(RoomOrder.class);

        // 動態構建 Predicate
        List<Predicate> predicates = new ArrayList<>();

        // 判斷條件是否包含 memberId
        if (conditions.containsKey("memberId")) {
            predicates.add(cb.equal(root.get("member").get("memberId"), conditions.get("memberId")));
        }

        // 判斷條件是否包含 startingTime 的上下界
        if (conditions.containsKey("startingTime")) {
            Map<String, Object> timeRange = (Map<String, Object>) conditions.get("startingTime");
            if (timeRange.containsKey("min")) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startingTime"), (LocalDateTime) timeRange.get("min")));
            }
            if (timeRange.containsKey("max")) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startingTime"), (LocalDateTime) timeRange.get("max")));
            }
        }

        // 判斷條件是否包含 leavingTime 的上下界
        if (conditions.containsKey("leavingTime")) {
            Map<String, Object> timeRange = (Map<String, Object>) conditions.get("leavingTime");
            if (timeRange.containsKey("min")) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("leavingTime"), (LocalDateTime) timeRange.get("min")));
            }
            if (timeRange.containsKey("max")) {
                predicates.add(cb.lessThanOrEqualTo(root.get("leavingTime"), (LocalDateTime) timeRange.get("max")));
            }
        }

        // 如果沒有條件，回傳所有結果
        if (predicates.isEmpty()) {
            return entityManager.createQuery(cq).getResultList();
        }

        // 合併所有 Predicate 為一個，並設定查詢條件
        Predicate combinedPredicate = cb.and(predicates.toArray(new Predicate[0]));
        cq.where(combinedPredicate);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<RoomOrder> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoomOrder> cq = cb.createQuery(RoomOrder.class);
        Root<RoomOrder> root = cq.from(RoomOrder.class);
        cq.select(root);
        return entityManager.createQuery(cq).getResultList();
    }

}
