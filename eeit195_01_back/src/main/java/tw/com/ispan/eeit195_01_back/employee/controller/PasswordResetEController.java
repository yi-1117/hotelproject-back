package tw.com.ispan.eeit195_01_back.employee.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import tw.com.ispan.eeit195_01_back.employee.bean.EPasswordResetBean;
import tw.com.ispan.eeit195_01_back.employee.service.EmployeePasswordResetService;
import tw.com.ispan.eeit195_01_back.employee.service.EmployeeService;

@RestController
@RequestMapping("/employee")
@CrossOrigin
public class PasswordResetEController {
    // JavaDoc 是 Java 官方的註解格式，用來 自動產生 API 文件，讓開發者更容易理解程式碼。
    // 這些標註（如 @param, @return）不是普通註解，而是 JavaDoc 特有的標記，通常寫在 /** ... */ 這種註解區塊內。

    @Autowired
    EmployeeService employeeService;
    @Autowired
    EmployeePasswordResetService employeePasswordResetService;

    /**
     * 發送密碼重設請求
     * 
     * @param memberId 會員ID
     * @return ResponseEntity 回應結果
     */
    @PostMapping("/password-reset-request")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@RequestParam Integer employeeId) {
        System.out.println("employeeId: " + employeeId);
        Map<String, String> response = new HashMap<>();
        try {
            // 發送重設請求
            EPasswordResetBean resetRequest = employeePasswordResetService.requestPasswordReset(employeeId);
            response.put("status", "success");
            response.put("message", "Password reset request sent successfully.");
            response.put("resetToken", resetRequest.getResetToken()); // 傳送 resetToken 供前端使用
            System.out.println("準備回傳OK");
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
        employeePasswordResetService.resetPassword(resetToken, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Password reset successfully.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/password/reset")
    public RedirectView showResetPasswordPage(@RequestParam String token) {
        // 檢查 token 是否有效
        boolean tokenValid = employeePasswordResetService.validateToken(token);
        if (!tokenValid) {
            return new RedirectView("/error?message=Invalid%20or%20expired%20reset%20token");
            // %20 代表 URL 空格，所以實際的錯誤訊息是：Invalid or expired reset token
        }
        // 將使用者導向前端重設密碼頁面
        return new RedirectView("http://192.168.23.148:5173/admin/dashboard?passwordtoken=" + token); // 192.168.23.148
    }
}
