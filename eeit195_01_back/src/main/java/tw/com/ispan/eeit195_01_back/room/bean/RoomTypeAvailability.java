package tw.com.ispan.eeit195_01_back.room.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_availibility")

public class RoomTypeAvailability {
    @Id
    private String roomTypeAvailabilityId;
    private Integer availableCount;
}
