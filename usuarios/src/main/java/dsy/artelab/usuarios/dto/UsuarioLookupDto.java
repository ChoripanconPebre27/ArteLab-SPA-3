package dsy.artelab.usuarios.dto;

public class UsuarioLookupDto {

    private Long id;
    private String nombreUsuario;
    private String correo;

    public UsuarioLookupDto(Long id, String nombreUsuario, String correo) {
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
