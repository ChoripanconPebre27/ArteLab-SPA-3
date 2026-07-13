package dsy.artelab.usuarios.dto;

public class UsuarioResponseDto {

    private final Long id;
    private final String nombreUsuario;
    private final String correo;

    public UsuarioResponseDto(Long id, String nombreUsuario, String correo) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
    }

    public Long getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }
}
