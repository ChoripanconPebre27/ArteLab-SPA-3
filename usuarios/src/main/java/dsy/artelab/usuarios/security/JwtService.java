package dsy.artelab.usuarios.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;
    private final long expirationSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.secret = secret;
        this.expirationSeconds = expirationMinutes * 60;
    }

    public String generateToken(String subject) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + expirationSeconds;
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + escapeJson(subject) + "\",\"iat\":" + issuedAt + ",\"exp\":" + expiresAt + "}";
        String unsignedToken = base64Url(header.getBytes(StandardCharsets.UTF_8))
                + "." + base64Url(payload.getBytes(StandardCharsets.UTF_8));
        return unsignedToken + "." + sign(unsignedToken);
    }

    public boolean isTokenValid(String token, String expectedSubject) {
        return extractSubject(token)
                .filter(expectedSubject::equals)
                .filter(subject -> !isExpired(token))
                .filter(subject -> hasValidSignature(token))
                .isPresent();
    }

    public Optional<String> extractSubject(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }
        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return extractJsonString(payload, "sub");
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    private boolean isExpired(String token) {
        return extractExpiration(token)
                .map(exp -> exp <= Instant.now().getEpochSecond())
                .orElse(true);
    }

    private Optional<Long> extractExpiration(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Optional.empty();
        }
        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return extractJsonLong(payload, "exp");
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private boolean hasValidSignature(String token) {
        int lastDot = token.lastIndexOf('.');
        if (lastDot < 0) {
            return false;
        }
        String unsignedToken = token.substring(0, lastDot);
        String signature = token.substring(lastDot + 1);
        return sign(unsignedToken).equals(signature);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(key);
            return base64Url(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token JWT", ex);
        }
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private Optional<String> extractJsonString(String json, String field) {
        String marker = "\"" + field + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return Optional.empty();
        }
        int valueStart = start + marker.length();
        int valueEnd = json.indexOf('"', valueStart);
        if (valueEnd < 0) {
            return Optional.empty();
        }
        return Optional.of(json.substring(valueStart, valueEnd));
    }

    private Optional<Long> extractJsonLong(String json, String field) {
        String marker = "\"" + field + "\":";
        int start = json.indexOf(marker);
        if (start < 0) {
            return Optional.empty();
        }
        int valueStart = start + marker.length();
        int valueEnd = valueStart;
        while (valueEnd < json.length() && Character.isDigit(json.charAt(valueEnd))) {
            valueEnd++;
        }
        if (valueStart == valueEnd) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(json.substring(valueStart, valueEnd)));
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
