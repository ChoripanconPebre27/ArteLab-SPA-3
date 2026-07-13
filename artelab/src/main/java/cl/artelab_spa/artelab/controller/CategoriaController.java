package cl.artelab_spa.artelab.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import cl.artelab_spa.artelab.assemblers.CategoriaModelAssembler;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Categorías", description = "Operaciones relacionadas con categorías de productos")
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    private final CategoriaModelAssembler assembler;

    public CategoriaController(CategoriaService categoriaService, CategoriaModelAssembler assembler) {
        this.categoriaService = categoriaService;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar categorías", description = "Obtiene una lista de todas las categorías disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No se encontraron categorías"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CollectionModel<EntityModel<Categoria>>> listarCategorias() {
        List<EntityModel<Categoria>> categorias = categoriaService.getAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (categorias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CollectionModel.of(
                categorias,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel()
        ));
    }


    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría con la información proporcionada")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría creada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o categoría con descripción duplicada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Categoria>> agregarCategoria(
            @Valid @RequestBody Categoria categoria) {
        Categoria saved = categoriaService.save(categoria);
        return ResponseEntity
                .created(
                        linkTo(
                                methodOn(CategoriaController.class)
                                        .buscarCategoria(saved.getId())
                        ).toUri()
                )
                .body(assembler.toModel(saved));
    }


    @GetMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Buscar categoría", description = "Obtiene una categoría específica por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Categoria>> buscarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaService.getById(id);
        return ResponseEntity.ok(assembler.toModel(categoria));
    }


    @PutMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar categoría", description = "Actualiza la información de una categoría existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o categoría con descripción duplicada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Categoria>> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody Categoria nuevo) {
        Categoria actualizada = categoriaService.update(id, nuevo);
        return ResponseEntity.ok(
                assembler.toModel(actualizada)
        );
    }

    
    @DeleteMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría existente por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Categoría eliminada exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
