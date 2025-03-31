package tw.com.ispan.eeit195_01_back.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewBean;
import tw.com.ispan.eeit195_01_back.member.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // 新增評論
    public ReviewBean saveReview(ReviewBean review) {
        return reviewRepository.save(review);
    }

    // 取得所有評論
    public List<ReviewBean> getAllReviews() {
        return reviewRepository.findAll();
    }

    public ReviewBean updateReview(Integer reviewId, ReviewBean review) {
        // 找到對應的評論
        ReviewBean existingReview = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalStateException("評論不存在"));

        // 確保會員只能修改自己發表的評論
        if (!existingReview.getMember().getMemberId().equals(review.getMember().getMemberId())) {
            throw new IllegalStateException("無權限修改他人的評論");
        }

        // 更新評論內容和評分
        existingReview.setComment(review.getComment());
        existingReview.setRating(review.getRating());
        existingReview.setUpdatedAt(review.getUpdatedAt());

        // 保存更新後的評論
        return reviewRepository.save(existingReview);
    }

    // 取得單一評論
    public Optional<ReviewBean> getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId);
    }

    // 取得會員的所有評論
    public List<ReviewBean> getReviewsByMemberId(Integer memberId) {
        return reviewRepository.findByMember_MemberId(memberId);
    }

    // 刪除評論
    
    public boolean deleteReview(Integer reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }
}
