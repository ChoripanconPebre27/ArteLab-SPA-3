package dsy.artelab.usuarios.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dsy.artelab.usuarios.model.Usuario;
import dsy.artelab.usuarios.repository.UsuarioRepository;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    buildUsuario("admin", "admin@mail.cl", "admin123"),
                    buildUsuario("ana", "ana@mail.cl", "ana123"),
                    buildUsuario("luis", "luis@mail.cl", "luis123")));
        }
    }

    private Usuario buildUsuario(String nombreUsuario, String correo, String clave) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        return usuario;
    }
}
