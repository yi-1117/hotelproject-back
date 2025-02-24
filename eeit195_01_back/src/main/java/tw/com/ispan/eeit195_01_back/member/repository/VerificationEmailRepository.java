package tw.com.ispan.eeit195_01_back.member.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.VerificationEmailBean;

public interface VerificationEmailRepository extends JpaRepository<VerificationEmailBean, Integer> {
    List<VerificationEmailBean> findByMember(MemberBean member); // 根據會員查詢所有驗證碼記錄

    Optional<VerificationEmailBean> findByMemberAndVerificationCode(MemberBean member, String verificationCode); // 查詢特定會員和驗證碼的記錄

    Optional<VerificationEmailBean> findByVerificationCode(String verificationCode);

    boolean existsByMember(MemberBean member); // 檢查該會員是否有驗證碼記錄

    // 查詢某會員在指定時間之後發送的所有驗證郵件
    List<VerificationEmailBean> findByMemberAndSentTimeAfter(MemberBean member, LocalDateTime sentTime);

    
    
}