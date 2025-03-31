package tw.com.ispan.eeit195_01_back.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewLikeBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewBean;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLikeBean, Integer> {

    // 檢查會員是否已經對某則評論按讚
    Optional<ReviewLikeBean> findByReviewAndMember(ReviewBean review, MemberBean member);

    // 刪除會員對某則評論的按讚記錄
    void deleteByReviewAndMember(ReviewBean review, MemberBean member);
}
