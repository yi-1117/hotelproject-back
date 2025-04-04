package tw.com.ispan.eeit195_01_back.member.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonWriter.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import tw.com.ispan.eeit195_01_back.member.bean.MemberDetailsBean;
import tw.com.ispan.eeit195_01_back.member.repository.MemberRepository;
import tw.com.ispan.eeit195_01_back.member.service.MemberDetailsService;

@RestController
@RequestMapping("/api/line")
public class LineBindingController {

    @Value("${line.messaging.channel-token}")
    private String channelToken;

    private static final String PUSH_API_URL = "https://api.line.me/v2/bot/message/push";

     @Autowired
        private MemberDetailsService memberDetailsService; // 注入 MemberDetailsService

        @PostMapping("/bind")
        public ResponseEntity<String> bindLineAccount(@RequestBody Map<String, Object> payload) {
            String lineUserId = (String) payload.get("lineUserId");
        
            if (lineUserId == null || lineUserId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LINE 用戶 ID 無效");
            }
        
            try {
                // 查找該 LINE 用戶 ID 是否已經綁定
                Optional<MemberDetailsBean> existingMember = memberDetailsService.findBySocialMediaAccount(lineUserId);
                
                // 如果該 LINE 用戶 ID 不存在，則返回錯誤訊息
                if (existingMember.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("此 LINE 用戶尚未綁定任何會員");
                }
        
                // 假設完成綁定邏輯後發送訊息
                sendBindingSuccessMessage(lineUserId);
                return ResponseEntity.ok("綁定成功");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("綁定失敗");
            }
        }

    private void sendBindingSuccessMessage(String lineUserId) throws IOException {
        // 確保 lineUserId 不為 null 或空
        if (lineUserId == null || lineUserId.isEmpty()) {
            throw new IllegalArgumentException("LINE 用戶 ID 無效");
        }

        // 使用 Map.of 初始化 body
        Map<String, Object> body = Map.of(
            "to", lineUserId,
            "messages", List.of(Map.of(
                "type", "text",
                "text", "LINE 帳號綁定成功！"
            ))
        );

        // 發送訊息的 API 請求
        HttpURLConnection connection = (HttpURLConnection) new URL(PUSH_API_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + channelToken);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(new ObjectMapper().writeValueAsBytes(body));
        }

        // 檢查回應狀態碼
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("回應失敗，狀態碼：" + responseCode);
        }
    }
}
