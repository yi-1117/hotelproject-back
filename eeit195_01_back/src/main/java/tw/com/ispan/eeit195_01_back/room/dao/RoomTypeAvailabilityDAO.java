package tw.com.ispan.eeit195_01_back.room.dao;

import java.time.LocalDate;
import java.util.Optional;

public interface RoomTypeAvailabilityDAO {
    // public String getHashKey(LocalDate date, String roomTypeName);

    public Optional<Integer> getCount(String roomTypeName, LocalDate date);

    public Optional<Integer> getCount(String key);
}
