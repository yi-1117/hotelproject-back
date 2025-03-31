package tw.com.ispan.eeit195_01_back.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewCommentBean;
import tw.com.ispan.eeit195_01_back.member.service.ReviewCommentService;

import java.util.List;

@RestController
@RequestMapping("/api/review-comments")
public class ReviewCommentController {

    @Autowired
    private ReviewCommentService reviewCommentService;

    // 新增留言或回覆
    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody ReviewCommentBean comment) {
        Integer reviewId = comment.getReview().getReviewId();
        Integer memberId = comment.getMember().getMemberId();
        Integer parentCommentId = comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null;

        // 如果有 parentCommentId，需要先檢查 parentComment 的 reviewId
        if (parentCommentId != null) {
            Integer parentReviewId = reviewCommentService.getReviewIdByCommentId(parentCommentId);
            if (!reviewId.equals(parentReviewId)) {
                return ResponseEntity.badRequest().body("回覆失敗：子留言的 reviewId 與父留言不一致");
            }
        }

        // 驗證通過，執行新增邏輯
        ReviewCommentBean result = reviewCommentService.addComment(reviewId, memberId, comment.getContent(),
                parentCommentId);
        return ResponseEntity.ok(result);
    }

    // 取得某條評論的留言（不包含子留言）
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReviewCommentBean>> getReviewComments(@PathVariable Integer reviewId) {
        List<ReviewCommentBean> comments = reviewCommentService.getReviewComments(reviewId);
        return ResponseEntity.ok(comments);
    }

    // 取得某則留言的所有回覆
    @GetMapping("/replies/{commentId}")
    public ResponseEntity<List<ReviewCommentBean>> getReplies(@PathVariable Integer commentId) {
        List<ReviewCommentBean> replies = reviewCommentService.getReplies(commentId);
        return ResponseEntity.ok(replies);
    }

    // 取得某留言的所有回覆（包含巢狀結構）
    @GetMapping("/thread/{commentId}")
    public ResponseEntity<List<ReviewCommentBean>> getCommentThread(@PathVariable Integer commentId) {
        List<ReviewCommentBean> thread = reviewCommentService.getCommentThread(commentId);
        return ResponseEntity.ok(thread);
    }

    @GetMapping("/flat/{reviewId}")
    public ResponseEntity<List<ReviewCommentBean>> getFlatCommentList(@PathVariable Integer reviewId) {
        List<ReviewCommentBean> comments = reviewCommentService.getFlatCommentList(reviewId);
        return ResponseEntity.ok(comments);
    }

}
