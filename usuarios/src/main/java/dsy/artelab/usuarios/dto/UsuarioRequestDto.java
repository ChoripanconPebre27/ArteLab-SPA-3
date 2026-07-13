package dsy.artelab.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDto {

    @NotNull(message = "El nombre de usuario es obligatorio")
    @NotBlank(message = "El nombre de usuario no puede estar vacio")
    @Size(min = 3, max = 12, message = "El nombre de usuario debe tener entre 3 y 12 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "El nombre de usuario solo puede contener letras, numeros y guion bajo.")
    private String nombreUsuario;

    @NotNull(message = "La clave es obligatoria")
    @NotBlank(message = "La clave no puede estar vacia")
    @Size(min = 6, max = 16, message = "La clave debe tener entre 6 y 16 caracteres.")
    private String clave;

    @NotNull(message = "El correo es obligatorio")
    @NotBlank(message = "El correo no puede estar vacio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = 30, message = "El correo debe tener un maximo de 30 caracteres.")
    private String correo;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
