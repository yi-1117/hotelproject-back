package tw.com.ispan.eeit195_01_back.member.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.ReviewBean;
import tw.com.ispan.eeit195_01_back.member.service.MemberService;
import tw.com.ispan.eeit195_01_back.member.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")  // 允許來自前端的請求
@Tag(name = "ReviewController", description = "評論相關 API")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberService memberService;

    // 新增評論
    @PostMapping
    public ResponseEntity<ReviewBean> addReview(@RequestBody ReviewBean review) {
        try {
            // 假設前端會傳送會員 ID，這樣可以從 request body 取得 memberId
            Integer memberId = review.getMember().getMemberId();

            // 通過 memberId 查找會員
            MemberBean member = memberService.getMemberById(memberId);

            // 設定會員為發表評論的會員
            review.setMember(member);

            // 儲存評論
            ReviewBean savedReview = reviewService.saveReview(review);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // 取得所有評論
    @GetMapping
    public ResponseEntity<List<ReviewBean>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // 取得單一評論
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewBean> getReviewById(@PathVariable Integer reviewId) {
        Optional<ReviewBean> review = reviewService.getReviewById(reviewId);
        return review.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 取得某會員的評論
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReviewBean>> getReviewsByMemberId(@PathVariable Integer memberId) {
        return ResponseEntity.ok(reviewService.getReviewsByMemberId(memberId));
    }

    // 修改評論
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewBean> updateReview(@PathVariable Integer reviewId, @RequestBody ReviewBean review) {
        try {
            ReviewBean updatedReview = reviewService.updateReview(reviewId, review);
            return ResponseEntity.ok(updatedReview); // 回傳更新後的評論
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 若發生錯誤，回傳400狀態碼
        }
    }

    // 刪除評論
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Integer reviewId) {
        boolean deleted = reviewService.deleteReview(reviewId);
        return deleted ? ResponseEntity.ok("刪除成功") : ResponseEntity.notFound().build();
    }
}
