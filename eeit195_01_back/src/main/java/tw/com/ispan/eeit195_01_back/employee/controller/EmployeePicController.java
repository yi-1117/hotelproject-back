package tw.com.ispan.eeit195_01_back.employee.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import tw.com.ispan.eeit195_01_back.employee.service.EmployeeService;

@RestController
@RequestMapping("/employee")
@CrossOrigin
@Slf4j
public class EmployeePicController {
    @Autowired
    private EmployeeService employeeService;

    //一、員工上傳大頭貼
    @PostMapping("/upload-profile-picture/{employeeId}")
    public ResponseEntity<Map<String, String>> uploadAndUpdateProfilePicture(
            @PathVariable Integer employeeId,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        String fileUrl = null;
        String originalFileName = null; 

        // 檢查是否有新檔案上傳
        if (file != null && !file.isEmpty()) {
            // 檢查檔案類型
            if (!isValidImageType(file)) {
                return ResponseEntity.badRequest().body(createResponse("error","只允許上傳圖片檔案", null, null));
            }
            // 檢查檔案大小（5MB）
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (file.getSize() > maxSize) {
                return ResponseEntity.status(413).body(createResponse("error","檔案大小超過限制（最大 5MB）", null, null));
            }            
            String uploadDir = new File("/uploads/").getAbsolutePath(); // WY的Mac儲存到專案內
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs(); // 建立目錄
            }
            try {
                // 儲存新檔案
                fileUrl = employeeService.createPictureFilePath(file, employeeId, uploadDir);
                originalFileName = file.getOriginalFilename();
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(createResponse("error","檔案上傳失敗", null,null));
            }
        }
        try {
            // 更新會員的頭像 URL（若沒有新檔案，則保持不變）
            employeeService.updateProfilePicture(employeeId, fileUrl);
            String message = (fileUrl == null) ? "未更新頭像，維持原照片" : "頭像更新成功";
            return ResponseEntity.ok(createResponse("success",message, fileUrl,originalFileName));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createResponse("error",e.getMessage(), null, null));
        }
    }
    // 二、檢查圖片檔
    private boolean isValidImageType(MultipartFile file) {
        if (file == null || file.getContentType() == null) {
            return false;
        }
        String[] allowedTypes = { "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp", "image/svg+xml" };
        String contentType = file.getContentType();
            return Arrays.asList(allowedTypes).contains(contentType);
    }
    // 三、創造回應
    private Map<String, String> createResponse(String status,String message, String imageUrl,String FileName) {
        Map<String, String> response = new HashMap<>();
        response.put("status",status);
        response.put("message", message);
        response.put("imageUrl", imageUrl);
        response.put("fileName",FileName);
        return response;
    }
    //四、取得員工大頭貼
    @GetMapping("/profile_pictures/{employeeId}/{fileName}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Integer employeeId, @PathVariable String fileName) {
        try {
            //Path filePath = Paths.get("C:/default_profile_pictures/" + employeeId + "/" + fileName); // windows本機
            Path filePath = Paths.get("uploads/" + employeeId + "/" + fileName); //WY的Mac 記得註解
            Resource resource = new FileSystemResource(filePath);
            System.out.println("File path: " + filePath.toString());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            // 根據檔案類型設置 MIME 類型（避免瀏覽器無法顯示）
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
