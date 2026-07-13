package cl.artelab_spa.artelab.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import cl.artelab_spa.artelab.assemblers.PromocionModelAssembler;
import cl.artelab_spa.artelab.model.Promocion;
import cl.artelab_spa.artelab.service.PromocionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/promociones")
@Tag(name = "Promociones", description = "Controlador para la gestión de promociones")
public class PromocionController {

    private final PromocionService promocionService;

    private final PromocionModelAssembler assembler;

    public PromocionController(PromocionService promocionService, PromocionModelAssembler assembler) {
        this.promocionService = promocionService;
        this.assembler = assembler;
    }

    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar promociones", description = "Obtiene una lista de todas las promociones disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de promociones obtenida correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No se encontraron promociones"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CollectionModel<EntityModel<Promocion>>> listarPromociones() {
        List<EntityModel<Promocion>> promociones = promocionService.getAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (promociones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CollectionModel.of(
                promociones,
                linkTo(methodOn(PromocionController.class).listarPromociones()).withSelfRel()
        ));
    }


    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear promocion", description = "Crea una nueva promocion con la información proporcionada")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Promocion creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Promocion>> agregarPromocion(@Valid @RequestBody Promocion promocion) {
        Promocion saved = promocionService.save(promocion);
        return ResponseEntity
                .created(linkTo(methodOn(PromocionController.class).buscarPromocion(saved.getId())).toUri())
                .body(assembler.toModel(saved));
    }

    @PostMapping(value = "/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear promocion para usuario", description = "Crea una nueva promocion para un usuario específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Promocion creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "Servicio de usuarios no disponible"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Promocion>> agregarPromocionParaUsuario(@PathVariable Long usuarioId, @Valid @RequestBody Promocion promocion) {
        Promocion saved = promocionService.saveForUsuario(usuarioId, promocion);
        return ResponseEntity
                .created(linkTo(methodOn(PromocionController.class).buscarPromocion(saved.getId())).toUri())
                .body(assembler.toModel(saved));
    }



    @GetMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Buscar promocion", description = "Obtiene la información de una promocion específica por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promocion encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Promocion no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Promocion>> buscarPromocion(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(promocionService.getById(id)));
    }



    @PutMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar promocion", description = "Actualiza la información de una promocion existente por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Promocion actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Promocion no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Promocion>> actualizarPromocion(@PathVariable Long id, @Valid @RequestBody Promocion nuevo) {
        return ResponseEntity.ok(assembler.toModel(promocionService.update(id, nuevo)));
    }


    @DeleteMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar promocion", description = "Elimina una promocion existente por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Promocion eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Promocion no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarPromocion(@PathVariable Long id) {
        promocionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
