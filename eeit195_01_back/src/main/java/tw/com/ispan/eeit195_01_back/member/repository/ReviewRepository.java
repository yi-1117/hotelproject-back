package tw.com.ispan.eeit195_01_back.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewBean;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewBean, Integer> {

    // 查詢某會員的所有評論
    List<ReviewBean> findByMember_MemberId(Integer memberId);
}
