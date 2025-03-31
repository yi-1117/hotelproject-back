package tw.com.ispan.eeit195_01_back.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewCommentBean;
import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewCommentBean, Integer> {
    // 查找某條評論的所有留言
    List<ReviewCommentBean> findByReviewReviewIdAndParentCommentIsNull(Integer reviewId);
    // 查找某條留言的所有子留言
    List<ReviewCommentBean> findByParentCommentCommentId(Integer parentCommentId);
}
