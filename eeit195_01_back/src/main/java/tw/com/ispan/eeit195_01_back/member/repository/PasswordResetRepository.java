package tw.com.ispan.eeit195_01_back.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.PasswordResetBean;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetBean, Integer> {

    // 根據會員ID和是否已使用來查詢重設請求

    Optional<PasswordResetBean> findByMemberBeanMemberIdAndIsUsedFalse(Integer memberId);

    Optional<PasswordResetBean> findByMemberBeanAndIsUsedFalse(MemberBean memberBean);

    // 根據重設 Token 查詢
    Optional<PasswordResetBean> findByResetToken(String resetToken);

    void deleteByMemberBeanAndIsUsedFalse(MemberBean member);  // 刪除未使用的舊 token
}
