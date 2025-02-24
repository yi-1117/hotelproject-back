package tw.com.ispan.eeit195_01_back.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberDetailsBean;
import tw.com.ispan.eeit195_01_back.member.exception.MemberNotFoundException;
import tw.com.ispan.eeit195_01_back.member.exception.VerificationCodeInvalidException;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.member.service.MemberDetailsService;
import tw.com.ispan.eeit195_01_back.member.service.MemberService;
import tw.com.ispan.eeit195_01_back.member.service.VerificationEmailService;

@RestController
@RequestMapping("/api/members")
@CrossOrigin
public class VerificationEmailController {

    @Autowired
    private VerificationEmailService verificationEmailService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberDetailsService memberDetailsService;

    @PostMapping("/send-verification-email")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(@RequestParam Integer memberId) {
        Map<String, String> response = new HashMap<>();
        try {
            // 根據 memberId 查找會員
            Optional<MemberBean> memberOpt = memberRepository.findById(memberId);
            if (!memberOpt.isPresent()) {
                throw new MemberNotFoundException("Member with ID " + memberId + " not found");
            }

            MemberBean member = memberOpt.get();
            // 發送驗證碼
            verificationEmailService.sendVerificationEmail(member.getEmail());

            response.put("status", "success");
            response.put("message", "Verification email sent successfully.");
            return ResponseEntity.ok(response);

        } catch (MemberNotFoundException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage()); // 顯示 "You can only request a verification code once every hour."
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // 回傳 400 錯誤
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send verification email.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 驗證驗證碼
    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        try {
            String email = request.get("email");
            String code = request.get("code");

            // 驗證驗證碼
            boolean isVerified = verificationEmailService.verifyCode(email, code);
            if (isVerified) {
                // 成功驗證後設為已驗證並創建會員詳細資料
                Optional<MemberBean> foundMemberOpt = memberService.findByEmail(email); // 使用 Optional 查找會員
                if (foundMemberOpt.isPresent()) { // 檢查是否存在會員
                    MemberBean member = foundMemberOpt.get(); // 取得實際的會員物件
                    // 設定為已驗證
                    member.setIsVerified(true);
                    memberRepository.save(member); // 儲存會員

                    // 創建會員詳細資料
                    try {
                        MemberDetailsBean memberDetails = new MemberDetailsBean();
                        memberDetails.setMemberBean(member); // 設定會員詳細資料的關聯
                        memberDetailsService.saveOrUpdateDetails(memberDetails); // 儲存會員詳細資料

                        response.put("status", "success");
                        response.put("message", "Email verified and member details created successfully.");
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        response.put("status", "error");
                        response.put("message", "Failed to create member details: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                    }
                } else {
                    response.put("status", "error");
                    response.put("message", "Member not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                throw new VerificationCodeInvalidException("Invalid or expired verification code.");
            }
        } catch (MemberNotFoundException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (VerificationCodeInvalidException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to verify email.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
