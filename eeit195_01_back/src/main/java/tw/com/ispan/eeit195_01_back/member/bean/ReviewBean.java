package tw.com.ispan.eeit195_01_back.member.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "review")
public class ReviewBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId; // 評論 ID（主鍵）

    @ManyToOne(cascade = CascadeType.MERGE) // 確保合併時也更新
    @JsonBackReference // 避免無窮遞迴
    @JoinColumn(name = "member_id", nullable = false)
    private MemberBean member; // 會員關聯（發表評論的會員）

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // 避免無窮遞迴
    private List<ReviewLikeBean> likes = new ArrayList<>(); // 存放所有按讚的會員

    // 關聯到留言
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // 忽略回傳的 review 屬性
    private List<ReviewCommentBean> reviewComments;

    @Column(nullable = false)
    private int rating; // 星等評分（1~5）

    @Column(nullable = false, length = 1000)
    private String comment; // 評論內容

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 發表時間

    @Column
    private LocalDateTime updatedAt; // 編輯時間

    

}