package tw.com.ispan.eeit195_01_back.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.eeit195_01_back.member.bean.MemberDetailsBean;

public interface MemberDetailsRepository extends JpaRepository<MemberDetailsBean, Integer> {
    // 根據 memberId 查詢會員詳細資料
    Optional<MemberDetailsBean> findByMemberId(Integer memberId);

    Optional<MemberDetailsBean> findBySocialMediaAccount(String socialMediaAccount);
    
}
