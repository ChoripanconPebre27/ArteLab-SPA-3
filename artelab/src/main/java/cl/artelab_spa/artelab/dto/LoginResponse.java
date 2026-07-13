package cl.artelab_spa.artelab.dto;

public class LoginResponse {

    private final String token;
    private final String tokenType;
    private final long expiresInSeconds;

    public LoginResponse(String token, long expiresInSeconds) {
        this.token = token;
        this.tokenType = "Bearer";
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInSeconds() {
        return expiresInSeconds;
    }
}
