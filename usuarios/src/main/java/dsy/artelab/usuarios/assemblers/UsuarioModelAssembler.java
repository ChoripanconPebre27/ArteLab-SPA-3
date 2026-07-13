package dsy.artelab.usuarios.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import dsy.artelab.usuarios.controller.UsuarioController;
import dsy.artelab.usuarios.dto.UsuarioResponseDto;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioResponseDto, EntityModel<UsuarioResponseDto>> {

    @Override
    public EntityModel<UsuarioResponseDto> toModel(UsuarioResponseDto usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuario(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).obtenerUsuarios()).withRel("usuarios"),
                linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(UsuarioController.class).eliminarUsuario(usuario.getId())).withRel("eliminar"));
    }
}
