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

import cl.artelab_spa.artelab.assemblers.ProductoModelAssembler;
import cl.artelab_spa.artelab.model.Producto;
import cl.artelab_spa.artelab.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "Productos", description = "Operaciones relacionadas con productos")
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    private final ProductoModelAssembler assembler;

    public ProductoController(ProductoService productoService, ProductoModelAssembler assembler) {
        this.productoService = productoService;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Listar productos", description = "Obtiene una lista de todos los productos disponibles")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "No se encontraron productos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.getAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(CollectionModel.of(
                productos,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel()
        ));
    }


    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto con la información proporcionada")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o producto con nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Producto>> agregarProducto(@Valid @RequestBody Producto producto) {
        Producto saved = productoService.save(producto);
        return ResponseEntity
                .created(linkTo(methodOn(ProductoController.class).buscarProducto(saved.getId())).toUri())
                .body(assembler.toModel(saved));
    }


    @Operation(summary = "Buscar producto", description = "Obtiene la información de un producto específico por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<Producto>> buscarProducto(@PathVariable Long id) {
        return ResponseEntity.ok(assembler.toModel(productoService.getById(id)));
    }


    @PutMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Actualizar producto", description = "Actualiza la información de un producto existente por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o producto con nombre duplicado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<EntityModel<Producto>> actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto nuevo) {
        return ResponseEntity.ok(assembler.toModel(productoService.update(id, nuevo)));
    }

    
    @DeleteMapping(value = "{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(summary = "Eliminar producto", description = "Elimina un producto existente por su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
