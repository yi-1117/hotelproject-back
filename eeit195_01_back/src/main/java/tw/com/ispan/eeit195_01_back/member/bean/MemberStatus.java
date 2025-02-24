package tw.com.ispan.eeit195_01_back.member.bean;

public enum MemberStatus {
    ACTIVE, // 正常會員
    INACTIVE, // 非活動會員
    SUSPENDED, // 暫停使用的會員
    PENDING; // 等待驗證或啟用的會員
}