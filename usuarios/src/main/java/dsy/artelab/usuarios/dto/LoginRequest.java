package dsy.artelab.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = 30, message = "El correo debe tener un maximo de 30 caracteres.")
    private String correo;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 16, message = "La clave debe tener un maximo de 16 caracteres.")
    private String clave;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }
}
