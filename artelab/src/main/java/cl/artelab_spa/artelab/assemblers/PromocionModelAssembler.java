package cl.artelab_spa.artelab.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import cl.artelab_spa.artelab.controller.PromocionController;
import cl.artelab_spa.artelab.model.Promocion;

@Component
public class PromocionModelAssembler implements RepresentationModelAssembler<Promocion, EntityModel<Promocion>> {

    @Override
    public EntityModel<Promocion> toModel(Promocion promocion) {
        return EntityModel.of(promocion,
                linkTo(methodOn(PromocionController.class).buscarPromocion(promocion.getId())).withSelfRel(),
                linkTo(methodOn(PromocionController.class).listarPromociones()).withRel("promociones"),
                linkTo(methodOn(PromocionController.class).actualizarPromocion(promocion.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(PromocionController.class).eliminarPromocion(promocion.getId())).withRel("eliminar"));
    }
}
