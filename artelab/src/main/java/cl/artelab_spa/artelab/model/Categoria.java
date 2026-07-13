package cl.artelab_spa.artelab.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Categoria usada para agrupar productos y promociones")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador unico de la categoria", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id; // ID de categoría. Ej: "1"

    
    @Column(length = 30, nullable = false)
    @NotBlank(message = "La descripcion de la categoria no puede estar vacia")
    @Size(max = 30, message = "La descripcion de la categoria debe tener un maximo de 30 caracteres")
    @Schema(description = "Descripcion o nombre visible de la categoria", example = "Lapices")
    private String des; // Nombre de categoría. Ej: "Lápices"

    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @Schema(description = "Productos asociados a la categoria", hidden = true)
    private List<Producto> productos;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    @Schema(description = "Promociones asociadas a la categoria", hidden = true)
    private List<Promocion> promociones;

}
