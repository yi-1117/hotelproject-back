package tw.com.ispan.eeit195_01_back.member.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import tw.com.ispan.eeit195_01_back.member.bean.ReviewCommentBean;

public class ReviewCommentDto {
    private Integer commentId;
    private String content;
    private LocalDateTime createdAt;
    private Integer memberId;
    private Integer reviewId;
    private List<ReviewCommentDto> replies;

    // Constructor for easier mapping
    public ReviewCommentDto(ReviewCommentBean comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.memberId = comment.getMember().getMemberId();
        this.reviewId = comment.getReview().getReviewId();
        this.replies = new ArrayList<>();
    }
}
