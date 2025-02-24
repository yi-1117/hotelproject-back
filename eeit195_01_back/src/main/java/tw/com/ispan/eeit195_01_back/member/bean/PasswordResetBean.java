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
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "password_reset")
@Data
public class PasswordResetBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_id")
    private Integer resetId; // 重設請求編號 (主鍵)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id") // 用於與 MemberBean 關聯
    private MemberBean memberBean; // 會員，與 Member 關聯

    @Column(name = "reset_token", nullable = false, unique = true)
    private String resetToken; // 重設密碼的 Token

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime; // 請求重設的時間

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime; // 重設 Token 的過期時間

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed; // 用於檢查 Token 是否已被使用，避免重設兩次

	public Integer getResetId() {
		return resetId;
	}

	public void setResetId(Integer resetId) {
		this.resetId = resetId;
	}

	public MemberBean getMemberBean() {
		return memberBean;
	}

	public void setMemberBean(MemberBean memberBean) {
		this.memberBean = memberBean;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(LocalDateTime requestTime) {
		this.requestTime = requestTime;
	}

	public LocalDateTime getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(LocalDateTime expireTime) {
		this.expireTime = expireTime;
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}
    
    
}
