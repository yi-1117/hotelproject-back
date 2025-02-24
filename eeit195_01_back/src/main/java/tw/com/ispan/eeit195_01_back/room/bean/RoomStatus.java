package tw.com.ispan.eeit195_01_back.room.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "room_status")

public class RoomStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomStatusId;
    @Builder.Default
    private Boolean isClear = true;
    @Builder.Default
    private Boolean isClean = true;
    @Builder.Default
    private Boolean isFunctional = true;
    @Builder.Default
    private Boolean isDisturbable = true;
    @OneToOne
    @JoinColumn(name = "fk_room_id")
    private Room roomBean;

}
