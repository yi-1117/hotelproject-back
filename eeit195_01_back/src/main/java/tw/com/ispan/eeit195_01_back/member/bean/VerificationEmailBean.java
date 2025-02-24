package tw.com.ispan.eeit195_01_back.member.bean;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "verification_email")
@Data
public class VerificationEmailBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Integer verificationId;

    // @Column(nullable = false)
    // private String email; // 確保這個欄位對應資料庫列

    @Column(name = "verification_code")
    private String verificationCode; // 驗證碼

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate; // 驗證碼的過期時間

    @Column(name = "sent_time", nullable = false)
    private LocalDateTime sentTime; // 驗證碼發送時間

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 外鍵關聯到 Member
    private MemberBean member; // 單向關聯，表示每封驗證郵件屬於一個會員

    // 設置發送時間為當前時間
    @PrePersist
    public void prePersist() {
        this.sentTime = LocalDateTime.now();

        // 設定過期時間為發送時間加上 24 小時
        this.expirationDate = this.sentTime.plusHours(24);
    }
}
