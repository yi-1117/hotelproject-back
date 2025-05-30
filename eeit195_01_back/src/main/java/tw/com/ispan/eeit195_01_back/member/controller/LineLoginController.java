package tw.com.ispan.eeit195_01_back.member.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import tw.com.ispan.eeit195_01_back.member.bean.MemberDetailsBean;
import tw.com.ispan.eeit195_01_back.member.service.MemberDetailsService;

@RestController
@RequestMapping("/api/line-login")
@CrossOrigin(origins = "http://localhost:5173") // 允許來自前端的請求
public class LineLoginController {

        @Value("${line.client.id}")
        private String clientId;

        @Value("${line.client.secret}")
        private String clientSecret;

        @Value("${line.redirect.uri}")
        private String redirectUri;

        String redirectUrl;

        private static final String TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";
        private static final String PROFILE_URL = "https://api.line.me/v2/profile";

        @Autowired
        private MemberDetailsService memberDetailsService; // 注入 MemberDetailsService

        // 用戶點擊登入後重定向到 LINE 授權頁面
        // @GetMapping("/line-login")
        // public void login(HttpServletResponse response) throws Exception {
        // String authorizationUrl = "https://access.line.me/oauth2/v2.1/authorize?" +
        // "response_type=code" +
        // "&client_id=" + clientId +
        // "&redirect_uri=" + redirectUri +
        // "&state=yourState123" + // 防止 CSRF 攻擊
        // "&scope=profile"; // 請求用戶的基本資料

        // response.sendRedirect(authorizationUrl);
        // }

        @GetMapping("/callback")
        public ResponseEntity<Map<String, Object>> callback(
                        @RequestParam("code") String code,
                        @RequestParam("state") String state) {

                if (!"yourState123".equals(state)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid state"));
                }

                String tokenRequestBody = "grant_type=authorization_code&code=" + code
                                + "&redirect_uri=" + redirectUri
                                + "&client_id=" + clientId
                                + "&client_secret=" + clientSecret;

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                HttpEntity<String> entity = new HttpEntity<>(tokenRequestBody, headers);

                try {
                        // 取得 Access Token
                        ResponseEntity<String> tokenResponse = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity,
                                        String.class);
                        String accessToken = extractAccessTokenFromResponse(tokenResponse.getBody());

                        // 取得用戶資訊
                        String profileResponse = getLineProfile(accessToken);
                        Map<String, String> userProfile = extractUserProfileFromResponse(profileResponse);

                        String userId = userProfile.get("userId");
                        String displayName = userProfile.get("displayName");
                        String pictureUrl = userProfile.get("pictureUrl");

                        memberDetailsService.updateLineProfile(userId, displayName, pictureUrl);

                        // 檢查 userId 是否已經存在於資料庫
                        Optional<MemberDetailsBean> existingMember = memberDetailsService
                                        .findBySocialMediaAccount(userId);

                        // Map<String, Object> responseBody = new HashMap<>();
                        if (existingMember.isPresent()) {
                                // 會員已存在，導向會員中心
                                redirectUrl = "http://localhost:5173/member-center/login?member-id=" 
                                              + existingMember.get().getMemberId()
                                              + "&access-token=" + accessToken;
                            } else {
                                // 會員不存在，導向註冊頁面
                                redirectUrl = "http://localhost:5173/member-center/register";
                            }
                    
                            // **使用 302 Redirect 讓前端直接跳轉**
                            return ResponseEntity.status(HttpStatus.FOUND)
                                                 .header("Location", redirectUrl)
                                                 .build();

                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(Map.of("error", "處理 LINE 登入時發生錯誤"));
                }
        }

        // 解析 Access Token
        private String extractAccessTokenFromResponse(String responseBody) {
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        return jsonNode.get("access_token").asText();
                } catch (Exception e) {
                        throw new RuntimeException("無法解析 Access Token", e);
                }
        }

        // 取得 LINE 用戶資料
        private String getLineProfile(String accessToken) {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);

                // 發送 GET 請求取得用戶資料
                ResponseEntity<String> response = restTemplate.exchange(PROFILE_URL, HttpMethod.GET,
                                new HttpEntity<>(headers), String.class);

                return response.getBody();
        }

        // 解析用戶資料中的 userId
        private Map<String, String> extractUserProfileFromResponse(String profileResponse) {
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode jsonNode = objectMapper.readTree(profileResponse);

                        String userId = jsonNode.has("userId") ? jsonNode.get("userId").asText() : "";
                        String displayName = jsonNode.has("displayName") ? jsonNode.get("displayName").asText() : "";
                        String pictureUrl = jsonNode.has("pictureUrl") ? jsonNode.get("pictureUrl").asText() : "";
                        String statusMessage = jsonNode.has("statusMessage") ? jsonNode.get("statusMessage").asText()
                                        : "";

                        // 將結果封裝為 Map
                        Map<String, String> userProfile = new HashMap<>();
                        userProfile.put("userId", userId);
                        userProfile.put("displayName", displayName);
                        userProfile.put("pictureUrl", pictureUrl);
                        userProfile.put("statusMessage", statusMessage);

                        return userProfile;
                } catch (Exception e) {
                        throw new RuntimeException("無法解析用戶資料", e);
                }
        }
}