package tw.com.ispan.eeit195_01_back.member.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberStatus; // 引入 MemberStatus 枚舉
import tw.com.ispan.eeit195_01_back.member.dto.ChangePasswordDto;
import tw.com.ispan.eeit195_01_back.member.service.MemberDetailsService;
import tw.com.ispan.eeit195_01_back.member.service.MemberService;
import tw.com.ispan.eeit195_01_back.member.service.VerificationEmailService;

@RestController
@RequestMapping("/api/members")
@CrossOrigin
public class MemberController {

    @Autowired
    private MemberService memberService;

    @SuppressWarnings("unused")
    @Autowired
    private MemberDetailsService memberDetailsService;

    @Autowired
    private VerificationEmailService verificationEmailService;

    @GetMapping("/search")
    public Page<MemberBean> searchMembers(
            @RequestParam Optional<String> search, // 搜尋條件，可選填
            @RequestParam Optional<String> status, // 會員狀態，可選填
            @RequestParam Optional<Boolean> isVerified, // 是否驗證，可選填
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String searchQuery = search.orElse(""); // 預設為空字串
        String statusQuery = status.orElse(""); // 預設為空字串
        Boolean verified = isVerified.orElse(null); // 預設為 null（代表不篩選）

        Pageable pageable = PageRequest.of(page, size);

        return memberService.searchMembers(searchQuery, statusQuery, verified, pageable);
    }

    // 登入
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody MemberBean member) {
        Map<String, Object> response = new HashMap<>();

        // 使用 email 查找會員
        Optional<MemberBean> foundMemberOpt = memberService.findByEmail(member.getEmail());

        // 檢查會員是否存在
        if (foundMemberOpt.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }

        MemberBean foundMember = foundMemberOpt.get(); // 獲取實際的 MemberBean 物件

        // 檢查會員是否驗證
        if (!foundMember.getIsVerified()) {
            response.put("status", "error");
            response.put("memberId", foundMember.getMemberId()); // 新增 memberId 回應
            response.put("message", "您的信箱尚未驗證，請先驗證您的信箱。");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden
        }

        // 檢查會員是否密碼正確
        if (!member.getPassword().equals(foundMember.getPassword())) { // 明碼比對
            response.put("status", "error");
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401 Unauthorized
        }

        // 檢查會員是否為活躍狀態
        if (foundMember.getStatus() != MemberStatus.ACTIVE) {
            response.put("status", foundMember.getStatus());
            response.put("memberId", foundMember.getMemberId()); // 新增 memberId 回應
            response.put("message", "非活躍狀態，無法登入");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // 403 Forbidden
        }

        // 成功回應
        response.put("message", "Login successful");
        response.put("memberId", foundMember.getMemberId()); // 新增 memberId 回應
        response.put("status", foundMember.getStatus()); // 回傳會員的狀態 (ACTIVE 或 INACTIVE)
        return ResponseEntity.ok(response); // 200 OK
    }

    // 註冊
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerMember(@RequestBody MemberBean member) {
        Map<String, Object> response = new HashMap<>();

        // 檢查 Email 是否唯一
        Optional<MemberBean> existingMemberOpt = memberService.findByEmail(member.getEmail());
        if (existingMemberOpt.isPresent()) {
            MemberBean existingMember = existingMemberOpt.get();
            // 如果找到已註冊的會員，回傳是否已驗證
            boolean isVerified = existingMember.getIsVerified(); // 假設會員有這個字段
            response.put("status", "success");
            response.put("message", "Email is already registered");
            response.put("memberId", existingMember.getMemberId()); // 已註冊的會員 ID
            response.put("isVerified", isVerified); // 已註冊，但根據是否驗證返回狀態
            return ResponseEntity.ok(response);
        }

        // 設定 Status
        if (!setMemberStatus(member)) {
            return createErrorResponse("Invalid status value", HttpStatus.BAD_REQUEST);
        }

        // 註冊會員，注意這裡會員尚未設置 password
        MemberBean savedMember = memberService.registerMember(member);

        // 創建會員詳細資料
        // try {
        // MemberDetailsBean memberDetails = new MemberDetailsBean();
        // memberDetails.setMemberBean(savedMember); // 設定會員詳細資料的關聯
        // memberDetailsService.saveOrUpdateDetails(memberDetails); // 保存會員詳細資料
        // } catch (Exception e) {
        // response.put("status", "error");
        // response.put("message", "Failed to create member details: " +
        // e.getMessage());
        // return
        // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        // }

        // 回應成功訊息，包含 memberId
        response.put("status", "success");
        response.put("message", "Member registered successfully");
        response.put("memberId", savedMember.getMemberId()); // 取得資料庫自動生成的 memberId
        response.put("isVerified", false); // 默認為未驗證

        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 回傳包含 memberId 的成功訊息
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<?> findByEmail(@RequestParam String email) {
        // 查詢會員資料
        Optional<MemberBean> memberOpt = memberService.findByEmail(email);

        // 如果找不到會員，回應錯誤訊息
        if (memberOpt.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "No member found with this email");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // 如果找到會員，回應成功訊息
        MemberBean member = memberOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("memberId", member.getMemberId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-password")
    public ResponseEntity<Map<String, Object>> completeRegistration(@RequestBody Map<String, Object> requestData) {
        Integer memberId = Integer.valueOf(requestData.get("memberId").toString());
        String password = (String) requestData.get("password");

        Map<String, Object> response = new HashMap<>();

        // 根據 memberId 查詢會員
        MemberBean member = memberService.findMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 確保該會員已驗證
        if (!member.getIsVerified()) {
            return createErrorResponse("Member not verified", HttpStatus.BAD_REQUEST);
        }

        // 設置密碼並標註為已驗證
        member.setPassword(password); // 不加密，直接設置密碼

        // 設置為已驗證
        member.setIsVerified(true);

        // 更新會員資料
        member.setUpdatedAt(LocalDateTime.now());
        memberService.saveMember(member);

        // 回應成功訊息
        response.put("status", "success");
        response.put("message", "Registration complete and member verified");
        response.put("memberId", member.getMemberId()); // 回傳 memberId

        return ResponseEntity.ok(response); // 回傳完成註冊的成功訊息
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberBean> getMemberById(@PathVariable Integer id) {
        Optional<MemberBean> member = memberService.findMemberById(id);
        return member.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 查多筆
    @GetMapping
    public ResponseEntity<Page<MemberBean>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberBean> members = memberService.findAllMembers(pageable);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMember(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        // 檢查會員是否存在
        Optional<MemberBean> memberOptional = memberService.findMemberById(id);
        if (!memberOptional.isPresent()) {
            response.put("status", "error");
            response.put("message", "Member not found. Unable to delete.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // HTTP 404
        }

        MemberBean member = memberOptional.get();

        // 檢查該會員是否有未驗證的信箱記錄
        if (verificationEmailService.hasUnverifiedEmail(member)) {
            response.put("status", "error");
            response.put("message", "Cannot delete member. Email verification is incomplete.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // HTTP 403
        }

        // 如果存在，執行刪除
        try {
            memberService.deleteMember(id);
            response.put("status", "success");
            response.put("message", "Member deleted successfully.");
            return ResponseEntity.ok(response); // HTTP 200
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to delete member. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // HTTP 500
        }
    }

    // 軟刪除會員
    @PutMapping("/soft-delete/{id}")
    public ResponseEntity<Map<String, String>> softDeleteMember(@PathVariable Integer id) {
        Map<String, String> response = new HashMap<>();

        // 確認會員是否存在
        Optional<MemberBean> optionalMember = memberService.findMemberById(id);
        if (!optionalMember.isPresent()) {
            response.put("status", "error");
            response.put("message", "會員不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        MemberBean memberBean = optionalMember.get();
        if (memberBean.getStatus() == MemberStatus.INACTIVE) {
            response.put("status", "error");
            response.put("message", "該會員已經是非活動狀態");
            return ResponseEntity.badRequest().body(response);
        }

        // 更新會員狀態為 INACTIVE
        memberBean.setStatus(MemberStatus.INACTIVE);
        memberService.saveMember(memberBean); // 儲存更新
        response.put("status", "success");
        response.put("message", "會員狀態已更新為 INACTIVE");
        return ResponseEntity.ok(response);
    }

    // 恢復會員
    @PutMapping("/reactivate/{memberId}")
    public String reactivateMember(@PathVariable Integer memberId) {
        memberService.reactivateMember(memberId);
        return "會員已成功恢復為正常狀態";
    }

    // 修改密碼
    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        try {
            // 查找會員
            Optional<MemberBean> existingMemberOpt = memberService.findMemberById(changePasswordDto.getMemberId());
            if (existingMemberOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
            }

            MemberBean existingMember = existingMemberOpt.get();

            // 驗證舊密碼是否正確
            if (!existingMember.getPassword().equals(changePasswordDto.getOldPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("舊密碼錯誤");
            }

            // 檢查新舊密碼是否相同
            if (existingMember.getPassword().equals(changePasswordDto.getNewPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("舊密碼與新密碼不能相同");
            }

            // 更新會員密碼
            existingMember.setPassword(changePasswordDto.getNewPassword());
            existingMember.setUpdatedAt(LocalDateTime.now()); // 記錄更新時間
            memberService.updateMember(changePasswordDto.getMemberId(), existingMember);

            // 回傳成功訊息
            return ResponseEntity.ok("密碼更新成功");

        } catch (Exception e) {
            // 捕獲所有異常並回傳錯誤訊息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the password");
        }
    }

    // 檢查 Email 是否唯一
    @SuppressWarnings("unused")
    private boolean isEmailUnique(String email, Integer excludeId) {
        Optional<MemberBean> existingMember = memberService.findByEmail(email);
        return existingMember
                .map(member -> member.getMemberId().equals(excludeId)) // 如果有值，檢查條件
                .orElse(true); // 如果 Optional 沒有值，認為是唯一
    }

    // 設定並驗證 Member 的 Status
    private boolean setMemberStatus(MemberBean member) {
        if (member.getStatus() == null) {
            member.setStatus(MemberStatus.ACTIVE);
            return true;
        }
        try {
            member.setStatus(MemberStatus.valueOf(member.getStatus().name()));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // 建立錯誤回應
    public ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", "error");
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
