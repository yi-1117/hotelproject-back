package tw.com.ispan.eeit195_01_back.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewLikeBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewBean;
import tw.com.ispan.eeit195_01_back.member.repository.ReviewLikeRepository;
import tw.com.ispan.eeit195_01_back.member.repository.ReviewRepository;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
public class ReviewLikeService {

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    // 新增或取消按讚
    
    public boolean toggleLike(Integer reviewId, Integer memberId) {
        Optional<ReviewBean> reviewOpt = reviewRepository.findById(reviewId);
        Optional<MemberBean> memberOpt = memberRepository.findById(memberId);

        if (reviewOpt.isPresent() && memberOpt.isPresent()) {
            ReviewBean review = reviewOpt.get();
            MemberBean member = memberOpt.get();

            Optional<ReviewLikeBean> existingLike = reviewLikeRepository.findByReviewAndMember(review, member);

            if (existingLike.isPresent()) {
                // 會員已按讚，則取消按讚
                reviewLikeRepository.deleteByReviewAndMember(review, member);
                return false; // 取消按讚成功
            } else {
                // 會員未按讚，則新增按讚
                ReviewLikeBean newLike = new ReviewLikeBean();
                newLike.setReview(review);
                newLike.setMember(member);
                reviewLikeRepository.save(newLike);
                return true; // 新增按讚成功
            }
        }
        return false; // 找不到 review 或 member
    }
}
