package tw.com.ispan.eeit195_01_back.room.dao;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;

public class RoomTypeDAOImpl implements RoomTypeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RoomType> findByRoomTypeName(String roomTypeName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoomType> cq = cb.createQuery(RoomType.class);
        Root<RoomType> root = cq.from(RoomType.class);
        Predicate p = cb.equal(root.get("roomTypeName"), roomTypeName);
        cq.where(p);
        if (entityManager.createQuery(cq).getResultList().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entityManager.createQuery(cq).getResultList().get(0));
    }

}
