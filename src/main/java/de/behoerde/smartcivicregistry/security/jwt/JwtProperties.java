// src/main/java/de/behoerde/smartcivicregistry/security/jwt/JwtProperties.java
package de.behoerde.smartcivicregistry.security.jwt;

//import lombok.Getter;
//import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperties {

    private String secretKey;
    private long expiration;
    private RefreshToken refreshToken = new RefreshToken();

    // Getter und Setter

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static class RefreshToken {
        private long expiration;

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }
    }
}

//      @Getter   // lombok
//      @Setter   // lombok
//      public static class RefreshToken { private long expiration;
//      }
//}