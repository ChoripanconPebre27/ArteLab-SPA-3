package dsy.artelab.usuarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import dsy.artelab.usuarios.dto.UsuarioRequestDto;
import dsy.artelab.usuarios.dto.UsuarioLookupDto;
import dsy.artelab.usuarios.dto.UsuarioResponseDto;
import dsy.artelab.usuarios.exception.ResourceConflictException;
import dsy.artelab.usuarios.model.Usuario;
import dsy.artelab.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void getUsuariosReturnsMappedDtos() {
        Usuario usuario = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDto> result = usuarioService.getUsuarios();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ana", result.get(0).getNombreUsuario());
    }

    @Test
    void saveUsuarioEncryptsPasswordAndPersistsUser() {
        UsuarioRequestDto request = new UsuarioRequestDto();
        request.setNombreUsuario("ana");
        request.setCorreo("ana@mail.cl");
        request.setClave("clave123");
        when(usuarioRepository.existsByCorreo("ana@mail.cl")).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario("ana")).thenReturn(false);
        when(passwordEncoder.encode("clave123")).thenReturn("encoded");
        when(usuarioRepository.save(org.mockito.ArgumentMatchers.any(Usuario.class))).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        UsuarioResponseDto response = usuarioService.saveUsuario(request);

        assertEquals(10L, response.getId());
        assertEquals("ana", response.getNombreUsuario());
        verify(usuarioRepository).save(org.mockito.ArgumentMatchers.any(Usuario.class));
    }

    @Test
    void saveUsuarioRejectsDuplicateCorreo() {
        UsuarioRequestDto request = new UsuarioRequestDto();
        request.setNombreUsuario("ana");
        request.setCorreo("ana@mail.cl");
        request.setClave("clave123");
        when(usuarioRepository.existsByCorreo("ana@mail.cl")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> usuarioService.saveUsuario(request));
    }

    @Test
    void saveUsuarioRejectsDuplicateNombreUsuario() {
        UsuarioRequestDto request = buildRequest("ana", "clave123", "ana@mail.cl");
        when(usuarioRepository.existsByCorreo("ana@mail.cl")).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario("ana")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> usuarioService.saveUsuario(request));
    }

    @Test
    void getUsuarioReturnsMappedDtoWhenUserExists() {
        Usuario usuario = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDto response = usuarioService.getUsuario(1L);

        assertEquals(1L, response.getId());
        assertEquals("ana@mail.cl", response.getCorreo());
    }

    @Test
    void getUsuarioThrowsWhenUserDoesNotExist() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.getUsuario(99L));
    }

    @Test
    void findUsuarioLookupByIdReturnsPublicDataWhenUserExists() {
        Usuario usuario = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioLookupDto> response = usuarioService.findUsuarioLookupById(1L);

        assertNotNull(response);
        assertEquals("ana", response.orElseThrow().getNombreUsuario());
    }

    @Test
    void updateUsuarioEncryptsPasswordAndPersistsChanges() {
        Usuario existing = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        UsuarioRequestDto request = buildRequest("ana2", "nueva123", "ana2@mail.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.existsByCorreoAndIdNot("ana2@mail.cl", 1L)).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuarioAndIdNot("ana2", 1L)).thenReturn(false);
        when(passwordEncoder.encode("nueva123")).thenReturn("encoded-new");
        when(usuarioRepository.save(existing)).thenReturn(existing);

        UsuarioResponseDto response = usuarioService.updateUsuario(1L, request);

        assertEquals("ana2", response.getNombreUsuario());
        assertEquals("ana2@mail.cl", response.getCorreo());
        assertEquals("encoded-new", existing.getClave());
        verify(usuarioRepository).save(existing);
    }

    @Test
    void updateUsuarioThrowsWhenUserDoesNotExist() {
        UsuarioRequestDto request = buildRequest("ana", "clave123", "ana@mail.cl");
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.updateUsuario(99L, request));
    }

    @Test
    void updateUsuarioRejectsDuplicateCorreo() {
        Usuario existing = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        UsuarioRequestDto request = buildRequest("ana", "clave123", "otra@mail.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.existsByCorreoAndIdNot("otra@mail.cl", 1L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> usuarioService.updateUsuario(1L, request));
    }

    @Test
    void updateUsuarioRejectsDuplicateNombreUsuario() {
        Usuario existing = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        UsuarioRequestDto request = buildRequest("otra", "clave123", "ana@mail.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioRepository.existsByNombreUsuarioAndIdNot("otra", 1L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> usuarioService.updateUsuario(1L, request));
    }

    @Test
    void deleteUsuarioRemovesExistingUser() {
        Usuario usuario = new Usuario(1L, "ana", "encoded", "ana@mail.cl");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteUsuario(1L);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void deleteUsuarioThrowsWhenUserDoesNotExist() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> usuarioService.deleteUsuario(99L));
    }

    private UsuarioRequestDto buildRequest(String nombreUsuario, String clave, String correo) {
        UsuarioRequestDto request = new UsuarioRequestDto();
        request.setNombreUsuario(nombreUsuario);
        request.setClave(clave);
        request.setCorreo(correo);
        return request;
    }
}
