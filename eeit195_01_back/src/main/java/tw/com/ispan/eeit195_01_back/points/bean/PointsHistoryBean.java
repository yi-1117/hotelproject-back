package tw.com.ispan.eeit195_01_back.points.bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

@Entity
@Table(name = "member_points_history")
public class PointsHistoryBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer memberPointsHistoryID;

    @Column(name = "change_ponts")
    private Integer changePoints;
    @Column(name = "change_discription")
    private String changeDiscription;
    @Column(name = "member_points_history_time")
    private LocalDateTime createdTime;
    // 關聯
    @ManyToOne
    @JoinColumn(name = "FK_MemberPointsHistorys_id", foreignKey = @ForeignKey(name = "fkc_member_memberpointshistory_BI"))
    private MemberBean member;

    // toString method
    @Override
    public String toString() {
        return "ＭemberPointsHistoryBean [memberPointsHistoryID=" + memberPointsHistoryID + ", changePoints="
                + changePoints + ", changeDiscription=" + changeDiscription + ", member=" + member + "]";
    }

    // getter & setter methods
    public Integer getMemberPointsHistoryID() {
        return memberPointsHistoryID;
    }

    public void setMemberPointsHistoryID(Integer memberPointsHistoryID) {
        this.memberPointsHistoryID = memberPointsHistoryID;
    }

    public Integer getChangePoints() {
        return changePoints;
    }

    public void setChangePoints(Integer changePoints) {
        this.changePoints = changePoints;
    }

    public String getChangeDiscription() {
        return changeDiscription;
    }

    public void setChangeDiscription(String changeDiscription) {
        this.changeDiscription = changeDiscription;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

}
