package app.property.user;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "users")
public class UserProperties {

    private DefaultUser defaultUser;

    @Data
    public static class DefaultUser {

        private String username;

        private String password;

        private String firstName;

        private String lastName;

        private String email;
    }
}
