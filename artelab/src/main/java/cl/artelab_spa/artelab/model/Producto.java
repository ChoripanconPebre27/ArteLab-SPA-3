package cl.artelab_spa.artelab.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Producto disponible en el catalogo de ArteLab")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador unico del producto", example = "123456", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id; // NÃºmero de 6 digitos del codigo de barra. Es el ID del producto. Ej: "123456"


    @Column(length = 30, nullable = false)
    @NotBlank(message = "La descripcion del producto no puede estar vacia")
    @Size(max = 30, message = "La descripcion del producto debe tener un maximo de 30 caracteres")
    @Schema(description = "Descripcion o nombre del producto", example = "Set 12 marcadores Artel")
    private String des; // Nombre. Ej: "Set 12 marcadores color Artel"


    @Column(nullable = false)
    @NotNull(message = "El precio del producto es obligatorio")
    @Positive(message = "El precio debe ser un valor positivo")
    @Schema(description = "Precio unitario del producto en pesos chilenos", example = "8990")
    private Integer precio; // Precio. Ej: "8990"


    @Column(nullable = false)
    @NotNull(message = "El stock del producto es obligatorio")
    @PositiveOrZero(message = "El stock debe ser cero o un nÃºmero positivo")
    @Schema(description = "Cantidad disponible para venta", example = "7")
    private Integer stock; // Cantidad de productos a libre disposiciÃ³n. Ej: "7"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoria", nullable = false, columnDefinition = "INTEGER")
    @NotNull(message = "La categoria del producto es obligatoria")
    @Schema(description = "Categoria asociada al producto", example = "{\"id\":1,\"des\":\"Lapices\"}")
    private Categoria categoria;

}
