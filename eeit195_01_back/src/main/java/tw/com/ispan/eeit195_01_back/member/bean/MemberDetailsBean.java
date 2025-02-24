package tw.com.ispan.eeit195_01_back.member.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "member_details")
@Data // Lombok 自動生成 getter, setter, toString 等方法
public class MemberDetailsBean {

    @Id
    @Column(name = "member_id")
    private Integer memberId; // 與 MemberBean 的主鍵對應

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    @MapsId
    @JoinColumn(name = "member_id", referencedColumnName = "member_id", insertable = false, updatable = false)
    @JsonIgnore
    private MemberBean memberBean; // 與 MemberBean 的關聯

    public MemberBean getMemberBean() {
        return memberBean;
    }

    public void setMemberBean(MemberBean memberBean) {
        this.memberBean = memberBean;
    }

    @Column(name = "full_name", length = 100)
    private String fullName; // 全名

    @Column(name = "gender", length = 10)
    private String gender; // 性別

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; // 生日

    @Column(name = "phone_number", length = 15)
    private String phoneNumber; // 電話號碼

    @Column(name = "address", length = 255)
    private String address; // 地址

    @Column(name = "join_date", updatable = false) // 不允許更新
    private LocalDateTime joinDate; // 加入日期

    @Column(name = "updated_at", updatable = true)
    private LocalDateTime updatedAt; // 最後更新時間

    @PrePersist
    protected void onCreate() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now(); // 新增時自動設定
        }
    }

    @Column(name = "nationality", length = 50)
    private String nationality; // 會員國籍

    @Column(name = "preferred_language", length = 50)
    private String preferredLanguage; // 偏好語言

    @Column(name = "newsletter_subscription")
    private Boolean newsletterSubscription; // 是否訂閱電子報

    @Column(name = "social_media_account", length = 255)
    private String socialMediaAccount; // 用來儲存 LINE userId

    @Column(name = "profile_picture", length = 255)
    private String profilePicture; // 照片檔案路徑

    @Override
    public String toString() {
        return "MemberDetailsBean [memberId=" + memberId + ", fullName=" + fullName
                + ", gender=" + gender + ", dateOfBirth=" + dateOfBirth + ", phoneNumber=" + phoneNumber + ", address="
                + address + ", joinDate=" + joinDate + ", nationality=" + nationality + ", preferredLanguage="
                + preferredLanguage + ", newsletterSubscription=" + newsletterSubscription + ", socialMediaAccount="
                + socialMediaAccount + ", profilePicture=" + profilePicture + "]";
    }

}
