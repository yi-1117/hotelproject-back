package tw.com.ispan.eeit195_01_back.points.bean;


import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "levels")
public class LevelBean {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="level_id")
    private Integer levelId;
    @Column(name="level_name",nullable = false,columnDefinition = "nvarchar(50)") //NOT NULL
    private String levelName;

    @Column(name="level_description",columnDefinition = "nvarchar(200)") 
    private String description;

    @Column(name="min_points",columnDefinition = "int default 0")
    private Integer minPoints;
    @Column(name="max_points",columnDefinition = "int default 0")
    private Integer maxPoints;
    

    @OneToMany(mappedBy = "levels")
    private List<MemberLevelsBean> memberLevels;

    // constructor
    public LevelBean(String levelName, String description) {
        this.levelName = levelName;
        this.description = description;
    }

    //toString method
    @Override
    public String toString() {
        return "LevelBean [levelId=" + levelId + ", levelName=" + levelName + ", discription=" + description
                + ", minPoints=" + minPoints + ", maxPoints=" + maxPoints + ", memberLevels=" + memberLevels + "]";
    }

    //getter & setter methods
    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }


    public String getLevelName() {
        return levelName;
    }


    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }


    public Integer getMinPoints() {
        return minPoints;
    }


    public void setMinPoints(Integer minPoints) {
        this.minPoints = minPoints;
    }


    public Integer getMaxPoints() {
        return maxPoints;
    }


    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }


    public List<MemberLevelsBean> getMemberLevels() {
        return memberLevels;
    }


    public void setMemberLevels(List<MemberLevelsBean> memberLevels) {
        this.memberLevels = memberLevels;
    }

    public String getDiscription() {
        return description;
    }

    public void setDiscription(String discription) {
        this.description = discription;
    }

    
}
