package tw.com.ispan.eeit195_01_back.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.eeit195_01_back.member.service.ReviewLikeService;

@RestController
@RequestMapping("/api/review-likes")
public class ReviewLikeController {

    @Autowired
    private ReviewLikeService reviewLikeService;

    // 會員對評論按讚或取消按讚
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleLike(@RequestParam Integer reviewId, @RequestParam Integer memberId) {
        boolean liked = reviewLikeService.toggleLike(reviewId, memberId);
        return ResponseEntity.ok(liked ? "已按讚" : "已取消按讚");
    }
}
