package tw.com.ispan.eeit195_01_back.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import tw.com.ispan.eeit195_01_back.member.bean.PasswordResetBean;
import tw.com.ispan.eeit195_01_back.member.service.PasswordResetService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/members")
@CrossOrigin
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * 發送密碼重設請求
     * 
     * @param memberId 會員ID
     * @return ResponseEntity 回應結果
     */
    @PostMapping("/password-reset-request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestParam Integer memberId) {
        Map<String, String> response = new HashMap<>();
        try {
            // 發送重設請求
            PasswordResetBean resetRequest = passwordResetService.requestPasswordReset(memberId);
            response.put("status", "success");
            response.put("message", "Password reset request sent successfully.");
            response.put("resetToken", resetRequest.getResetToken()); // 傳送 resetToken 供前端使用
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 使用 reset token 重設密碼
     * 
     * @param resetToken  重設密碼的 Token
     * @param newPassword 新密碼
     * @return ResponseEntity 回應結果
     */
    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam("token") String resetToken,
            @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        passwordResetService.resetPassword(resetToken, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Password reset successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/password/reset")
    public RedirectView showResetPasswordPage(@RequestParam String token) {
        // 檢查 token 是否有效
        boolean tokenValid = passwordResetService.validateToken(token);
        if (!tokenValid) {
            return new RedirectView("/error?message=Invalid%20or%20expired%20reset%20token");
        }
        // 將使用者導向前端重設密碼頁面
        return new RedirectView("http://192.168.23.148:5173/reset-password?token=" + token);
    }

}


