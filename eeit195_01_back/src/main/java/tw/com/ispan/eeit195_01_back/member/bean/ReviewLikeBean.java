package tw.com.ispan.eeit195_01_back.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "review_like", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "review_id", "member_id" }) // 確保每個會員只能對一則評論按讚一次
})
public class ReviewLikeBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeId; // 按讚記錄 ID（主鍵）

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    
    private ReviewBean review; // 關聯到 `ReviewBean`

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    
    private MemberBean member; // 哪個會員按讚

}
