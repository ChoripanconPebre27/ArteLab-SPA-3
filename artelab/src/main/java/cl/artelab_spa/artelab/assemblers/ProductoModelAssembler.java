package cl.artelab_spa.artelab.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import cl.artelab_spa.artelab.controller.ProductoController;
import cl.artelab_spa.artelab.model.Producto;

@Component
public class ProductoModelAssembler
        implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class)
                        .buscarProducto(producto.getId()))
                        .withSelfRel(),
                linkTo(methodOn(ProductoController.class)
                        .listarProductos())
                        .withRel("productos"),
                linkTo(methodOn(ProductoController.class)
                        .actualizarProducto(producto.getId(), null))
                        .withRel("actualizar"),
                linkTo(methodOn(ProductoController.class)
                        .eliminarProducto(producto.getId()))
                        .withRel("eliminar")

        );
    }
}
