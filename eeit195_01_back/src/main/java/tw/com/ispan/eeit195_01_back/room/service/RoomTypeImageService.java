package tw.com.ispan.eeit195_01_back.room.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import tw.com.ispan.eeit195_01_back.room.bean.RoomType;
import tw.com.ispan.eeit195_01_back.room.bean.RoomTypeImage;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeImageRepository;
import tw.com.ispan.eeit195_01_back.room.repository.RoomTypeRepository;

@Service
@Transactional
public class RoomTypeImageService {

    private final RoomTypeImageRepository roomTypeImageRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeImageService(RoomTypeImageRepository roomTypeImageRepository,
            RoomTypeRepository roomTypeRepository) {
        this.roomTypeImageRepository = roomTypeImageRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public void saveImage(MultipartFile file, Integer roomTypeId, Integer positionId) throws IOException {

        System.out.println("roomTypeId: " + roomTypeId);
        System.out.println("positionId: " + positionId);
        // 檢查檔案大小，避免過大的檔案
        long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("檔案超過 10MB");
        }

        // 找出對應的 RoomType
        Optional<RoomType> optional = roomTypeRepository.findById(roomTypeId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("找不到 id 為 " + roomTypeId + " 的房型");
        }

        try {
            // 確保 ID 唯一性：這裡使用 roomTypeId 和 positionId 組合成唯一的 roomTypeImageId
            RoomType roomType = optional.get();
            String roomTypeImageId = roomTypeId + "_" + positionId;
            System.out.println("roomTypeImageId:" + roomTypeImageId);
            // 檢查是否已存在該位置的圖片，若存在則覆蓋
            Optional<RoomTypeImage> existingImage = roomTypeImageRepository.findById(roomTypeImageId);
            RoomTypeImage roomTypeImage = existingImage.orElseGet(() -> RoomTypeImage.builder()
                    .roomTypeImageId(roomTypeImageId)
                    .roomType(roomType)
                    .build());

            // 更新圖片內容
            roomTypeImage.setImage(file.getBytes());

            System.out.println("準備存第" + (positionId + 1) + "個檔案");

            // 儲存圖片檔案到資料庫
            roomTypeImageRepository.save(roomTypeImage);

        } catch (IOException e) {
            throw new RuntimeException("檔案處理失敗", e);
        }
    }

    public RoomTypeImage findImageById(String roomTypeImageId) {
        Optional<RoomTypeImage> optional = roomTypeImageRepository.findById(roomTypeImageId.toString());
        return optional.isEmpty() ? null : optional.get();
    }
}
