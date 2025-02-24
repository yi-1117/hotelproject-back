package tw.com.ispan.eeit195_01_back.member.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;

public interface MemberRepository extends JpaRepository<MemberBean, Integer>, JpaSpecificationExecutor<MemberBean> {

    // 透過會員 ID 查詢
    Optional<MemberBean> findByMemberId(Integer memberId);

    // 透過電子郵件查詢
    Optional<MemberBean> findByEmail(String email);

    // 透過電子郵件模糊搜尋（用於手動調用時）
    Page<MemberBean> findByEmailContaining(String email, Pageable pageable);

}
