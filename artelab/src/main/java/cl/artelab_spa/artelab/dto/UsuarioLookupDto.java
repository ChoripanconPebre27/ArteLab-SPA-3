package cl.artelab_spa.artelab.dto;

public class UsuarioLookupDto {

    private Long id;
    private String nombreUsuario;
    private String correo;

    public UsuarioLookupDto() {
    }

    public UsuarioLookupDto(Long id, String nombreUsuario, String correo) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
