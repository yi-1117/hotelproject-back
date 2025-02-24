package tw.com.ispan.eeit195_01_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = { RedisAutoConfiguration.class })
@EnableJpaAuditing // 啟用 JPA 審計功能
public class Eeit19501BackApplication {

	public static void main(String[] args) {
		SpringApplication.run(Eeit19501BackApplication.class, args);
	}

}
