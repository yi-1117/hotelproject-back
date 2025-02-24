package tw.com.ispan.eeit195_01_back.points.bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

@Entity
@Table(name="member_levels")
public class MemberLevelsBean { //為了創建另外多的屬性而現造的bean，不使用inversive columns
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_levels_id")
    private Integer memberLevelsId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberBean member;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private LevelBean levels;

    @Column(name="updated_time", columnDefinition = "DATETIME DEFAULT GETDATE()")//預設為現在時間
    private LocalDateTime upDateTime;

    //toString method
    @Override
    public String toString() {
        return "MemberLevelsBean [memberLevelsId=" + memberLevelsId + ", member=" + member + ", levels=" + levels
                + ", upDateTime=" + upDateTime + "]";
    }

    //getter & setter methods

    public Integer getMemberLevelsId() {
        return memberLevelsId;
    }

    public void setMemberLevelsId(Integer memberLevelsId) {
        this.memberLevelsId = memberLevelsId;
    }

    public MemberBean getMember() {
        return member;
    }

    public void setMember(MemberBean member) {
        this.member = member;
    }

    public LevelBean getLevels() {
        return levels;
    }

    public void setLevels(LevelBean levels) {
        this.levels = levels;
    }

    public LocalDateTime getUpDateTime() {
        return upDateTime;
    }

    public void setUpDateTime(LocalDateTime upDateTime) {
        this.upDateTime = upDateTime;
    }

}
