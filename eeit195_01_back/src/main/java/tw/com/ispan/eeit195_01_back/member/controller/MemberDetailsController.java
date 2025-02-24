package tw.com.ispan.eeit195_01_back.member.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import tw.com.ispan.eeit195_01_back.member.bean.MemberBean;
import tw.com.ispan.eeit195_01_back.member.bean.MemberDetailsBean;
import tw.com.ispan.eeit195_01_back.member.service.MemberDetailsService;
import tw.com.ispan.eeit195_01_back.member.service.MemberService;

@RestController
@RequestMapping("/api/members")
@CrossOrigin
@Slf4j
public class MemberDetailsController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberDetailsService memberDetailsService;

    // 建立錯誤回應
    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    // 根據會員 ID 查詢會員詳細資料
    @GetMapping("/details/{memberId}")
    public ResponseEntity<?> getMemberDetails(@PathVariable Integer memberId) {
        Optional<MemberDetailsBean> memberDetails = memberDetailsService.getMemberDetailsByMemberId(memberId);

        if (memberDetails.isPresent()) {
            return ResponseEntity.ok(memberDetails.get());
        } else {
            return createErrorResponse("Member details not found", HttpStatus.NOT_FOUND);
        }
    }

    // 查詢所有會員詳細資料（支援分頁功能）
    @GetMapping("/details")
    public ResponseEntity<?> getAllMemberDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 分頁查詢
            Page<MemberDetailsBean> memberDetailsPage = memberDetailsService
                    .getAllMemberDetails(PageRequest.of(page, size));

            // 建立回應
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", memberDetailsPage.getContent());
            response.put("currentPage", memberDetailsPage.getNumber());
            response.put("totalItems", memberDetailsPage.getTotalElements());
            response.put("totalPages", memberDetailsPage.getTotalPages());

            return ResponseEntity.ok(response); // 返回成功回應
        } catch (Exception e) {
            // 錯誤處理
            return createErrorResponse("Error while retrieving member details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 新增或更新會員詳細資料
    @PostMapping("/details/{memberId}")
    public ResponseEntity<Map<String, Object>> createOrUpdateDetails(
            @PathVariable Integer memberId,
            @RequestBody MemberDetailsBean details) {
        Map<String, Object> response = new HashMap<>();
        log.info("details: " + details);
        try {
            // 確保會員存在
            MemberBean member = memberService.findMemberById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

            // // 設定會員關聯
            details.setMemberBean(member);

            // 儲存或更新會員詳細資料
            memberDetailsService.saveOrUpdateDetails(details);

            // 成功回應
            response.put("status", "success");
            response.put("message", "Details saved successfully.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // 無效請求回應
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // 系統錯誤回應
            response.put("status", "error");
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 刪除會員詳細資料
    @DeleteMapping("/details/{memberId}")
    public ResponseEntity<Map<String, String>> deleteMemberDetails(@PathVariable Integer memberId) {
        Map<String, String> response = new HashMap<>();
        try {
            memberDetailsService.deleteMemberDetails(memberId);
            // 刪除成功後，回傳成功訊息
            response.put("status", "success");
            response.put("message", "Member details deleted successfully");
            return ResponseEntity.ok(response); // 使用 ok() 回應成功
        } catch (Exception e) {
            // 如果發生錯誤，返回錯誤訊息
            return createErrorResponse("Error while deleting member details", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 上傳並更新會員頭像
    private boolean isValidImageType(MultipartFile file) {
        String[] allowedTypes = { "image/jpeg", "image/png", "image/gif" };
        String contentType = file.getContentType();
        return Arrays.asList(allowedTypes).contains(contentType);
    }

    @PostMapping("/upload-profile-picture/{memberId}")
    public ResponseEntity<Map<String, String>> uploadAndUpdateProfilePicture(
            @PathVariable Integer memberId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        String fileUrl = null;

        // 檢查是否有新檔案上傳
        if (file != null && !file.isEmpty()) {

            // 檢查檔案類型
            if (!isValidImageType(file)) {
                return ResponseEntity.badRequest().body(createResponse("只允許上傳圖片檔案", null));
            }

            // 檢查檔案大小（5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.status(413).body(createResponse("檔案大小超過限制（最大 5MB）", null));
            }

            String uploadDir = System.getenv("UPLOAD_DIR"); 
            if (uploadDir == null || uploadDir.isEmpty()) {
                uploadDir = "C:/default_profile_pictures/"; // 預設值
            }

            try {
                // 儲存新檔案
                fileUrl = memberDetailsService.saveFile(file, memberId, uploadDir);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(createResponse("檔案上傳失敗", null));
            }
        }

        try {
            // 更新會員的頭像 URL（若沒有新檔案，則保持不變）
            memberDetailsService.updateProfilePicture(memberId, fileUrl);

            String message = (fileUrl == null) ? "未更新頭像，維持原照片" : "頭像更新成功";
            return ResponseEntity.ok(createResponse(message, fileUrl));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createResponse(e.getMessage(), null));
        }
    }

    private Map<String, String> createResponse(String message, String imageUrl) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("imageUrl", imageUrl);
        return response;
    }

    // @GetMapping("/profile_pictures/{memberId}/{fileName}")
    // public ResponseEntity<Resource> getProfilePicture(@PathVariable Integer memberId, @PathVariable String fileName) {
    //     try {
    //         Path filePath = Paths.get("C:/default_profile_pictures/" + memberId + "/" + fileName);
    //         Resource resource = new FileSystemResource(filePath);

    //         if (!resource.exists()) {
    //             return ResponseEntity.notFound().build();
    //         }

    //         return ResponseEntity.ok()
    //                 .contentType(MediaType.IMAGE_JPEG) // 可以根據實際圖片格式調整
    //                 .body(resource);
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    //     }
    // }

    @GetMapping("/profile_pictures/{memberId}")
    public ResponseEntity<Resource> getLatestProfilePicture(@PathVariable Integer memberId) {
        try {
            Path memberDir = Paths.get("C:/default_profile_pictures/" + memberId);
            if (!Files.exists(memberDir)) {
                return ResponseEntity.notFound().build();
            }

            // 找出最新的檔案
            Optional<Path> latestFile = Files.list(memberDir)
                    .filter(Files::isRegularFile)
                    .max(Comparator.comparingLong(p -> p.toFile().lastModified()));

            if (latestFile.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(latestFile.get());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}