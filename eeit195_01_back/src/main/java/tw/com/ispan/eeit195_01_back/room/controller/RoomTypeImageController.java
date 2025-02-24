package tw.com.ispan.eeit195_01_back.room.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeImage;
import tw.com.ispan.eeit195_01_back.room.service.RoomTypeImageService;

@RestController
@RequestMapping("/api/room-type-image")
@CrossOrigin
public class RoomTypeImageController {

    private RoomTypeImageService roomTypeImageService;

    public RoomTypeImageController(RoomTypeImageService roomTypeImageService) {
        this.roomTypeImageService = roomTypeImageService;
    }

    private byte[] defaultImage = null;

    // 初始化方法，載入預設圖片
    @PostConstruct
    public void initialize() throws IOException {
        byte[] buffer = new byte[8192];

        ClassLoader classLoader = getClass().getClassLoader();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedInputStream is = new BufferedInputStream(classLoader.getResourceAsStream("static/images/no-image.jpg"));
        int len = is.read(buffer);
        while (len != -1) {
            os.write(buffer, 0, len);
            len = is.read(buffer);
        }
        is.close();
        this.defaultImage = os.toByteArray();
    }

    // 根據房型圖片 ID 返回對應的圖片
    @GetMapping(path = "/{roomTypeImageId}", produces = { MediaType.IMAGE_JPEG_VALUE })
    public @ResponseBody byte[] getRoomTypeImage(@PathVariable String roomTypeImageId) {
        byte[] result = this.defaultImage; // 默認圖片

        // 根據房型圖片 ID 查找圖片資料
        RoomTypeImage roomTypeImage = roomTypeImageService.findImageById(roomTypeImageId);

        if (roomTypeImage != null) {
            result = roomTypeImage.getImage(); // 獲取圖片數據
        }

        return result;
    }

    @PostMapping(path = { "/create/{roomTypeId}" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadRoomTypeImage(
            @PathVariable Integer roomTypeId,
            @RequestPart("files") MultipartFile[] files) {

        Map<String, String> message = new HashMap<>();

        // 處理檔案上傳，只處理前六張圖片
        try {
            
            // 檢查圖片數量
            if (files.length == 0 || files.length > 6) {
                message.put("errorFile", "檔案數量 " + files.length + " 錯誤");
                return ResponseEntity.badRequest().body(message);
            }
            for (int i = 0; i < 6; i++) {
                roomTypeImageService.saveImage(files[i], roomTypeId, i);
                System.out.println("房型id: " + roomTypeId);
                System.out.println("位置: " + i);
            }
            message.put("success", "檔案上傳成功");
            return ResponseEntity.ok().body(message);
        } catch (IllegalArgumentException e) {
            // 如果發生了房型不正確的情況
            message.put("errorFile", e.getMessage());
            return ResponseEntity.badRequest().body(message);

        } catch (RuntimeException e) {
            // 檔案處理錯誤
            message.put("errorFile", "Failed to process image file: " + e.getMessage());
            return ResponseEntity.badRequest().body(message);

        } catch (Exception e) {
            // 其他未預期的錯誤
            message.put("errorFile", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @PutMapping(path = { "/update/{roomTypeId}" }, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> updateRoomTypeImages(
            @PathVariable Integer roomTypeId,
            @RequestPart("files") MultipartFile[] files) {

        Map<String, String> message = new HashMap<>();
        System.err.println("共" + files.length + "個檔案");

        try {
            // 確保圖片數量符合要求
            if (files.length == 0 || files.length > 6) {
                message.put("errorFile", "檔案數量錯誤，最多可上傳 6 張圖片");
                return ResponseEntity.badRequest().body(message);
            }

            // 依序存入新的圖片（覆蓋舊圖片）
            for (int i = 0; i < files.length; i++) {
                roomTypeImageService.saveImage(files[i], roomTypeId, i);
            }

            message.put("success", "圖片更新成功");
            return ResponseEntity.ok().body(message);

        } catch (IllegalArgumentException e) {
            message.put("errorFile", e.getMessage());
            return ResponseEntity.badRequest().body(message);
        } catch (RuntimeException e) {
            message.put("errorFile", "圖片處理失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(message);
        } catch (Exception e) {
            message.put("errorFile", "發生未預期的錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

}
