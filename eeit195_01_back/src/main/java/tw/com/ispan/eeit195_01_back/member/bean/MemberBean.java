package tw.com.ispan.eeit195_01_back.member.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit195_01_back.points.bean.MemberLevelsBean;
import tw.com.ispan.eeit195_01_back.points.bean.PointsBean;
import tw.com.ispan.eeit195_01_back.points.bean.PointsHistoryBean;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingCart;
import tw.com.ispan.eeit195_01_back.shop.bean.ShoppingOrder;

@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Entity
@Table(name = "member")
@Data
public class MemberBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId; // 會員編號 (主鍵)

	@Column(name = "password", nullable = true, length = 100)
	private String password; // 密碼

	@Column(nullable = true, unique = true)
	private String email; // 電子郵件地址

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = true)
	private MemberStatus status; // 預設為 Actice 狀態

	@Column(name = "created_at", nullable = true, updatable = true)
	private LocalDateTime createdAt; // 註冊時間

	@Column(name = "updated_at", nullable = true, updatable = true)
	private LocalDateTime updatedAt; // 最後更新時間

	// 取得 isVerified 的方法
	@Column(name = "is_verified", nullable = false)
	@Builder.Default
	private Boolean isVerified = false; // 設定預設值為 false, 是否驗證

	// @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval =
	// true)
	// private Set<VerificationEmailBean> verificationEmails = new HashSet<>();

	@OneToOne(mappedBy = "memberBean", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JsonIgnore
	private MemberDetailsBean memberDetailsBean; // 與 MemberDetails 的一對一關聯

	@OneToOne(mappedBy = "member", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JsonIgnore
	private PointsBean Points;

	@OneToMany(mappedBy = "member", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<PointsHistoryBean> memberPointsHistory;

	@OneToMany(mappedBy = "member", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<MemberLevelsBean> memberLevels; // 會員擁有的等級

	@Override // 不准刪
	public String toString() {
		return "MemberBean [memberId=" + memberId + ", password=" + password + ", email="
				+ email + ", status=" + status + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// // 設定 isVerified 的方法
	// public void setIsVerified(Boolean isVerified) {
	// this.isVerified = isVerified;
	// }

	@OneToOne(mappedBy = "member", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }) // 反向關聯到購物車
	@JsonIgnore
	private ShoppingCart shoppingCart;

	@OneToMany(mappedBy = "member", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, fetch = FetchType.LAZY) // 反向關聯到訂單
	@JsonIgnore
	@Builder.Default
	private List<ShoppingOrder> shoppingOrders = new ArrayList<>(); // 會員的訂單

	
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	@Builder.Default
	private List<ReviewLikeBean> reviewLikes = new ArrayList<>();

}
