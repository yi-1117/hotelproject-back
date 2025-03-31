package tw.com.ispan.eeit195_01_back;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(exclude = { RedisAutoConfiguration.class })
@EnableJpaAuditing // 啟用 JPA 審計功能
@Slf4j
public class Eeit19501BackApplication {

    public static void main(String[] args) {
        // 先設定 Profile，再執行 Spring Boot
        try {
            // 取得主機名稱
            String hostname = InetAddress.getLocalHost().getHostName();

            // 設定對應的 Profile
            String profile;
            switch (hostname) {
                case "docker":
                    profile = "docker";
                    break;
                default:
                    profile = "default"; // 預設環境
            }

            // **在 Spring Boot 啟動前設定 Profile**
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, profile);
            log.info("Using profile: " + profile);

        } catch (UnknownHostException e) {
            log.error("無法取得主機名稱，使用 local profile", e);
            System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "local");
        }

        // **這行要放在後面**
        SpringApplication.run(Eeit19501BackApplication.class, args);
    }
}
