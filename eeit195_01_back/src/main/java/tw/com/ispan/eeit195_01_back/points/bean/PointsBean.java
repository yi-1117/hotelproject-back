package tw.com.ispan.eeit195_01_back.points.bean;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

@Entity
@Table(name = "points")
public class PointsBean { // 會員點數

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成策略
    @Column(name = "points_id")
    private Integer PointsId;

    @Column(name = "current_points")
    private Integer currentPoints;

    @Column(name = "update_time", columnDefinition = "DATETIME DEFAULT GETDATE()")
    private LocalDateTime upDateTime;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    @JoinColumn(name = "FK_MemberPoints_id", foreignKey = @ForeignKey(name = "fkc_member_memberpoints_BI"))
    private MemberBean member;

    public Integer getPointsId() {
        return PointsId;
    }

    public void setPointsId(Integer pointsId) {
        PointsId = pointsId;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public LocalDateTime getUpDateTime() {
        return upDateTime;
    }

    public void setUpDateTime(LocalDateTime upDateTime) {
        this.upDateTime = upDateTime;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return "PointsBean [PointsId=" + PointsId + ", currentPoints=" + currentPoints + ", upDateTime=" + upDateTime
                + ", ]";
    }

    public PointsBean() {
        
    }
}
