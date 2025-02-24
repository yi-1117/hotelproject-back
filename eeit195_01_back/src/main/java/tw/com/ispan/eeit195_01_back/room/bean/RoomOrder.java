package tw.com.ispan.eeit195_01_back.room.bean;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_order")
public class RoomOrder {

    @Id
    private String roomOrderId;
    private Integer roomOfferId;
    private String orderStatus;
    private Integer residentCount;
    private Double totalPayment;
    @Column(name = "order_time")
    private LocalDateTime orderTime;
    @Column(name = "starting_time")
    private LocalDateTime startingTime;
    @Column(name = "leaving_time")
    private LocalDateTime leavingTime;
    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;
    @Column(name = "checkout_time")
    private LocalDateTime checkoutTime;
    @Column(name = "used_points")
    private Integer usedPoints;
    @Column(name = "total_payment_before_using_points")
    private Double totalPaymentBeforeUsingPoints;
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "fk_room_order_id")
    private Set<RoomTypeOrder> roomTypeOrders;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
    private MemberBean member;
}
