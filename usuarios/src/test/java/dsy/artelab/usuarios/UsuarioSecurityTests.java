package dsy.artelab.usuarios;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import dsy.artelab.usuarios.dto.UsuarioLookupDto;
import dsy.artelab.usuarios.dto.UsuarioRequestDto;
import dsy.artelab.usuarios.dto.UsuarioResponseDto;
import dsy.artelab.usuarios.model.Usuario;
import dsy.artelab.usuarios.repository.UsuarioRepository;
import dsy.artelab.usuarios.service.UsuarioService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UsuarioSecurityTests {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void saveUsuarioStoresBCryptHashAndReturnsDtoWithoutPassword() {
        String rawPassword = "clave123";
        UsuarioRequestDto request = new UsuarioRequestDto();
        request.setNombreUsuario("usr" + System.nanoTime() % 1000000000);
        request.setClave(rawPassword);
        request.setCorreo("usr" + System.nanoTime() + "@mail.cl");

        UsuarioResponseDto response = usuarioService.saveUsuario(request);

        assertNotNull(response.getId());
        assertResponseDtoDoesNotExposePassword();
        assertLookupDtoDoesNotExposePassword();

        Usuario saved = usuarioRepository.findByCorreo(response.getCorreo()).orElseThrow();
        assertFalse(rawPassword.equals(saved.getClave()));
        assertTrue(saved.getClave().startsWith("$2"));
        assertTrue(passwordEncoder.matches(rawPassword, saved.getClave()));
    }

    private void assertResponseDtoDoesNotExposePassword() {
        assertFalse(hasFieldNamed(UsuarioResponseDto.class, "clave"));
        assertFalse(hasFieldNamed(UsuarioResponseDto.class, "password"));
    }

    private void assertLookupDtoDoesNotExposePassword() {
        assertFalse(hasFieldNamed(UsuarioLookupDto.class, "clave"));
        assertFalse(hasFieldNamed(UsuarioLookupDto.class, "password"));
    }

    private boolean hasFieldNamed(Class<?> type, String fieldName) {
        return Arrays.stream(type.getDeclaredFields())
                .map(Field::getName)
                .anyMatch(fieldName::equals);
    }
}
