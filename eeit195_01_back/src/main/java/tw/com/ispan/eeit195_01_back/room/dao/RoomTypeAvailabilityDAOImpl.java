package tw.com.ispan.eeit195_01_back.room.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;

public class RoomTypeAvailabilityDAOImpl implements RoomTypeAvailabilityDAO {
    // @PersistenceContext
    // private EntityManager entityManager;

    private final RedisTemplate<String, Object> redisTemplate;

    public RoomTypeAvailabilityDAOImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Integer> getCount(String roomTypeName, LocalDate date) {
        String key = date + roomTypeName;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Integer) {
            return Optional.of((Integer) value);
        } else
            return Optional.empty();
    }

    @Override
    public Optional<Integer> getCount(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Integer) {
            return Optional.of((Integer) value);
        } else
            return Optional.empty();
    }

    // @Override
    // public String getHashKey(LocalDate date, String roomTypeName) {
    // return Hashing.sha256().hashString(date.toString() + roomTypeName,
    // StandardCharsets.UTF_8).toString();
    // }

    // @Override
    // public Optional<Integer> getCount(String roomTypeName, LocalDate date) {
    // CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    // CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
    // Root<RoomTypeAvailability> root = query.from(RoomTypeAvailability.class);

    // Join<RoomTypeAvailability, RoomType> roomTypeJoin = root.join("roomType");

    // Predicate roomTypeNamePredicate = cb.equal(roomTypeJoin.get("roomTypeName"),
    // roomTypeName);
    // Predicate datePredicate = cb.equal(root.get("date"), date);

    // query.select(root.get("availableCount"))
    // .where(cb.and(roomTypeNamePredicate, datePredicate));

    // try {
    // Integer availableCount = entityManager.createQuery(query).getSingleResult();
    // return Optional.ofNullable(availableCount);
    // } catch (Exception e) {
    // return Optional.empty();
    // }
    // }
}
