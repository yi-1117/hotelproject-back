package tw.com.ispan.eeit195_01_back.room.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "room_type_order")

public class RoomTypeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeOrderId;
    private Integer roomCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_room_type_id", nullable = false)
    private RoomType roomType;
}
