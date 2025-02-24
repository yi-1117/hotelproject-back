package tw.com.ispan.eeit195_01_back.employee.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;


@NoArgsConstructor //解決無參數建構子

@Builder(toBuilder = true)
@AllArgsConstructor
@Entity
@Table(name = "employee")
@Data
public class EmployeeBean {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id")
	private Integer employeeId; // 會員編號 (主鍵)

	@Column(name = "password", nullable = true, length = 100)
	private String password; // 密碼

	@Column(nullable = false, unique = true)
	private String email; // 電子郵件地址

    @Column(name = "address", nullable = false, length = 255)
    private String address; // 居住地址

    @Column(name = "full_name", length = 100)
    private String fullName; // 全名

    @Column(name = "gender", nullable = false, length = 10)
    private String gender; // 性別

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth; // 生日

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber; // 電話號碼

    @Column(name = "join_date", nullable = false, updatable = false) // 不允許更新
    private LocalDateTime joinDate; // 加入日期

    @Column(name = "updated_at", nullable = false, updatable = true)
    private LocalDateTime updatedAt; // 最後更新時間

    @Column(name = "profile_picture", length = 255)
    private String profilePicture; // 照片檔案路徑

    @Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
	private EmployeeStatus status; // 預設為 ACTIVE在職 狀態

    @Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = true, columnDefinition = "VARCHAR(255) DEFAULT 'STAFF'")
	private EmployeeRole role; // 預設為 STAFF一般員工 狀態

    @PrePersist
    protected void onCreate() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now(); // 新增時自動設定
        }
    }
    
    @Override
    public String toString() {
        return "EmployeeBean [employeeId=" + employeeId + ", password=" + password + ", email=" + email + ", address="
                + address + ", gender=" + gender + ", dateOfBirth=" + dateOfBirth + ", phoneNumber=" + phoneNumber
                + ", joinDate=" + joinDate + ", updatedAt=" + updatedAt + ", profilePicture=" + profilePicture
                + ", status=" + status + ", role=" + role + "]";
    }

}
