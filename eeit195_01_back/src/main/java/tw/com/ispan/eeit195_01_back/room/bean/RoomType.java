package tw.com.ispan.eeit195_01_back.room.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_type")
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomTypeId;
    private String roomTypeName;
    private String bedType;
    private Double area;
    private String bathroomType;
    private String roomTypeDescription;
    private Boolean isHandicap;
    private Integer adultCapacity;
    private Integer childrenCapacity;
    private Integer maxCapacity;
    private Double unitPrice;
    private Double additionalPricePerPerson;
    private Double overTimeRatio;
    @Column(name = "max_count")
    private Integer roomTypeMaxCount;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Room> rooms;

    @OneToMany(mappedBy = "roomType", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<RoomTypeImage> roomTypeImages;

    // @ToString.Exclude
    // @OneToMany(mappedBy = "roomType", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<RoomTypeAvailability> availabilities;

}
