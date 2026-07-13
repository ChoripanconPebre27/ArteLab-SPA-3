package cl.artelab_spa.artelab.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Promocion aplicada a una categoria de productos")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INTEGER")
    @Schema(description = "Identificador unico de la promocion", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id; // ID de promoción. Ej: "1"


    @Column(length = 30, nullable = false)
    @NotBlank(message = "La descripcion de la promocion no puede estar vacia")
    @Size(max = 30, message = "La descripcion de la promocion debe tener un maximo de 30 caracteres")
    @Schema(description = "Descripcion o nombre de la promocion", example = "Pinceles 20 Abril")
    private String des; // Nombre de promoción. Ej: "Pinceles 20% Abril"


    @Column(nullable = false)
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha de inicio de vigencia", example = "2026-04-01")
    private LocalDate fechaIni; // Fecha inicio en formato yyyy-mm-dd. Ej: "2026-04-01"


    @Column(nullable = false)
    @NotNull(message = "La fecha de termino es obligatoria")
    @Schema(description = "Fecha de termino de vigencia", example = "2026-04-30")
    private LocalDate fechaTer; // Fecha término en formato yyyy-mm-dd. Ej: "2026-04-30"


    @Column(nullable = false)
    @NotNull(message = "El descuento es obligatorio")
    @Min(value = 1, message = "El descuento debe ser al menos 1")
    @Max(value = 100, message = "El descuento no puede exceder 100")
    @Schema(description = "Porcentaje de descuento aplicado", example = "20")
    private Integer descuento; // Porcentaje de descuento. Ej: "20"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCategoria", nullable = false, columnDefinition = "INTEGER")
    @NotNull(message = "La categoria de la promocion es obligatoria")
    @Schema(description = "Categoria sobre la que aplica la promocion", example = "{\"id\":1,\"des\":\"Lapices\"}")
    private Categoria categoria;

}
