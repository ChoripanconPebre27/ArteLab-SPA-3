package cl.artelab_spa.artelab.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import cl.artelab_spa.artelab.controller.CategoriaController;
import cl.artelab_spa.artelab.model.Categoria;

@Component
public class CategoriaModelAssembler
        implements RepresentationModelAssembler<Categoria, EntityModel<Categoria>> {

    @Override
    public EntityModel<Categoria> toModel(Categoria categoria) {
        return EntityModel.of(categoria,
                linkTo(methodOn(CategoriaController.class)
                        .buscarCategoria(categoria.getId()))
                        .withSelfRel(),
                linkTo(methodOn(CategoriaController.class)
                        .listarCategorias())
                        .withRel("categorias"),
                linkTo(methodOn(CategoriaController.class)
                        .actualizarCategoria(categoria.getId(), null))
                        .withRel("actualizar"),
                linkTo(methodOn(CategoriaController.class)
                        .eliminarCategoria(categoria.getId()))
                        .withRel("eliminar")

        );
    }
}
