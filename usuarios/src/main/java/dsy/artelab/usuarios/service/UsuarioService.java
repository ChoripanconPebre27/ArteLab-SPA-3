package dsy.artelab.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dsy.artelab.usuarios.dto.UsuarioLookupDto;
import dsy.artelab.usuarios.dto.UsuarioRequestDto;
import dsy.artelab.usuarios.dto.UsuarioResponseDto;
import dsy.artelab.usuarios.exception.ResourceConflictException;
import dsy.artelab.usuarios.model.Usuario;
import dsy.artelab.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponseDto> getUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        log.info("Retrieved all usuarios, count={}", usuarios.size());
        return usuarios.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public UsuarioResponseDto getUsuario(Long id) {
        log.info("Looking up usuario by id={}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        log.info("Found usuario id={}", id);
        return mapToResponseDto(usuario);
    }

    public Optional<UsuarioLookupDto> findUsuarioLookupById(Long id) {
        log.info("Looking up usuario lookup DTO by id={}", id);
        return usuarioRepository.findById(id)
                .map(this::mapToLookupDto);
    }

    public UsuarioResponseDto saveUsuario(UsuarioRequestDto request) {
        validateUniqueCorreo(request.getCorreo());
        validateUniqueNombreUsuario(request.getNombreUsuario());
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(request.getNombreUsuario());
        usuario.setCorreo(request.getCorreo());
        usuario.setClave(passwordEncoder.encode(request.getClave()));
        Usuario saved = usuarioRepository.save(usuario);
        log.info("Created usuario id={} correo={} nombreUsuario={}",
                saved.getId(), saved.getCorreo(), saved.getNombreUsuario());
        return mapToResponseDto(saved);
    }

    public UsuarioResponseDto updateUsuario(Long id, UsuarioRequestDto request) {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (!existing.getCorreo().equals(request.getCorreo())) {
            validateUniqueCorreoExcludingId(request.getCorreo(), id);
        }
        if (!existing.getNombreUsuario().equals(request.getNombreUsuario())) {
            validateUniqueNombreUsuarioExcludingId(request.getNombreUsuario(), id);
        }

        existing.setNombreUsuario(request.getNombreUsuario());
        existing.setClave(passwordEncoder.encode(request.getClave()));
        existing.setCorreo(request.getCorreo());
        Usuario updated = usuarioRepository.save(existing);
        log.info("Updated usuario id={} correo={} nombreUsuario={}",
                updated.getId(), updated.getCorreo(), updated.getNombreUsuario());
        return mapToResponseDto(updated);
    }

    public void deleteUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
        log.info("Deleted usuario id={}", id);
    }

    private UsuarioLookupDto mapToLookupDto(Usuario usuario) {
        return new UsuarioLookupDto(usuario.getId(), usuario.getNombreUsuario(), usuario.getCorreo());
    }

    private UsuarioResponseDto mapToResponseDto(Usuario usuario) {
        return new UsuarioResponseDto(usuario.getId(), usuario.getNombreUsuario(), usuario.getCorreo());
    }

    private void validateUniqueCorreo(String correo) {
        if (usuarioRepository.existsByCorreo(correo)) {
            log.warn("Correo uniqueness violation: {}", correo);
            throw new ResourceConflictException("El correo ya esta en uso.");
        }
    }

    private void validateUniqueNombreUsuario(String nombreUsuario) {
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            log.warn("Nombre de usuario uniqueness violation: {}", nombreUsuario);
            throw new ResourceConflictException("El nombre de usuario ya esta en uso.");
        }
    }

    private void validateUniqueCorreoExcludingId(String correo, Long id) {
        if (usuarioRepository.existsByCorreoAndIdNot(correo, id)) {
            log.warn("Correo uniqueness violation on update: {} id={}", correo, id);
            throw new ResourceConflictException("El correo ya esta en uso por otro usuario.");
        }
    }

    private void validateUniqueNombreUsuarioExcludingId(String nombreUsuario, Long id) {
        if (usuarioRepository.existsByNombreUsuarioAndIdNot(nombreUsuario, id)) {
            log.warn("Nombre de usuario uniqueness violation on update: {} id={}", nombreUsuario, id);
            throw new ResourceConflictException("El nombre de usuario ya esta en uso por otro usuario.");
        }
    }
}
