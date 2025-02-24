package tw.com.ispan.eeit195_01_back.room.dao;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeOrder;

public class RoomTypeOrderDAOImpl implements RoomTypeOrderDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RoomTypeOrder> findRoomTypeOrderByNameAndCount(String name, Integer count) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoomTypeOrder> cq = cb.createQuery(RoomTypeOrder.class);
        Root<RoomTypeOrder> root = cq.from(RoomTypeOrder.class);
        Join<RoomTypeOrder, RoomType> roomTypeJoin = root.join("roomType", JoinType.INNER);
        Predicate p1 = cb.equal(roomTypeJoin.get("roomTypeName"), name);
        Predicate p2 = cb.equal(root.get("roomCount"), count);
        cq.where(cb.and(p1, p2));
        if (entityManager.createQuery(cq).getResultList().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entityManager.createQuery(cq).getResultList().get(0));
    }

}
