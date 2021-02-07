package lk.lemono.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by imran on 2/6/21.
 */
@SpringBootApplication
@EnableJpaRepositories(Config.REPOSITORY_PATH)
@EntityScan(Config.ENTITY_PATH)
public class AuthenticationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}
