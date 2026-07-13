package dsy.artelab.usuarios.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Usuario registrado para autenticacion y operaciones del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador unico del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(length = 12, nullable = false, unique = true)
    @Schema(description = "Nombre de usuario", example = "admin")
    private String nombreUsuario;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 60, nullable = false)
    @Schema(description = "Clave del usuario. Solo se acepta en solicitudes de escritura", example = "admin123", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String clave;

    @Column(length = 30, nullable = false, unique = true)
    @Schema(description = "Correo electronico unico del usuario", example = "admin@mail.cl")
    private String correo;

}
