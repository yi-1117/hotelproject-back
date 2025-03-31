package tw.com.ispan.eeit195_01_back.member.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewBean;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewCommentBean;
import tw.com.ispan.eeit195_01_back.member.repository.ReviewCommentRepository;

@Service
public class ReviewCommentService {

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    // 新增留言（僅能回覆根留言）
    public ReviewCommentBean addComment(Integer reviewId, Integer memberId, String content, Integer parentCommentId) {
        ReviewCommentBean comment = new ReviewCommentBean();
        comment.setReview(new ReviewBean());
        comment.getReview().setReviewId(reviewId);
        comment.setMember(new MemberBean());
        comment.getMember().setMemberId(memberId);
        comment.setContent(content);

        // 僅允許回覆根留言
        if (parentCommentId != null) {
            ReviewCommentBean parent = reviewCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new RuntimeException("根留言不存在"));

            // 判斷是否為根留言（parentComment 必須為 null）
            if (parent.getParentComment() != null) {
                throw new RuntimeException("只允許回覆根留言");
            }
            comment.setParentComment(parent);
        }

        return reviewCommentRepository.save(comment);
    }

    // 根據 commentId 查詢對應的 reviewId
    public Integer getReviewIdByCommentId(Integer commentId) {
        ReviewCommentBean parentComment = reviewCommentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("找不到父留言"));
        return parentComment.getReview().getReviewId();
    }

    // 取得評論底下的所有留言（不包含子留言）
    public List<ReviewCommentBean> getReviewComments(Integer reviewId) {
        return reviewCommentRepository.findByReviewReviewIdAndParentCommentIsNull(reviewId);
    }

    // 取得某則留言的所有回覆（根留言）
    public List<ReviewCommentBean> getReplies(Integer commentId) {
        return reviewCommentRepository.findByParentCommentCommentId(commentId);
    }

    // 取得某則留言的所有回覆（遞迴方式）
    public List<ReviewCommentBean> getCommentThread(Integer commentId) {
        ReviewCommentBean comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));
        return buildThread(comment);
    }

    private List<ReviewCommentBean> buildThread(ReviewCommentBean comment) {
        List<ReviewCommentBean> replies = reviewCommentRepository.findByParentCommentCommentId(comment.getCommentId());
        for (ReviewCommentBean reply : replies) {
            reply.setReplies(buildThread(reply)); // 遞迴取得子留言
        }
        return replies;
    }

    public List<ReviewCommentBean> getFlatCommentList(Integer reviewId) {
        List<ReviewCommentBean> allComments = reviewCommentRepository.findByReviewReviewIdAndParentCommentIsNull(reviewId);
    
        // 設定為平坦結構
        allComments.forEach(comment -> {
            comment.setReplies(null); // 解除巢狀結構
    
            // 避免無窮遞迴
            if (comment.getReview() != null) {
                comment.getReview().setReviewComments(null); // 改為 Comments
            }
    
            comment.getReview().getMember().setPassword(null); // 不回傳敏感資料
        });
    
        return allComments;
    }
}
