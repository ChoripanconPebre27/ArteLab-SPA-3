package cl.artelab_spa.artelab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario debe tener un maximo de 50 caracteres.")
    private String username;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 128, message = "La clave debe tener un maximo de 128 caracteres.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
