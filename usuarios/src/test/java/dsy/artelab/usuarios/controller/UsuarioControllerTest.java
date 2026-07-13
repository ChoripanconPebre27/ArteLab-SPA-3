package dsy.artelab.usuarios.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dsy.artelab.usuarios.assemblers.UsuarioModelAssembler;
import dsy.artelab.usuarios.dto.UsuarioLookupDto;
import dsy.artelab.usuarios.dto.UsuarioRequestDto;
import dsy.artelab.usuarios.dto.UsuarioResponseDto;
import dsy.artelab.usuarios.exception.GlobalExceptionHandler;
import dsy.artelab.usuarios.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Spy
    private UsuarioModelAssembler assembler = new UsuarioModelAssembler();

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void obtenerUsuariosRetorna200ConHateoas() throws Exception {
        UsuarioResponseDto usuario = new UsuarioResponseDto(1L, "ana", "ana@mail.cl");
        when(usuarioService.getUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ana")))
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")))
                .andExpect(content().string(containsString("/api/v1/usuarios/1")));

        verify(usuarioService).getUsuarios();
    }

    @Test
    void obtenerUsuariosSinDatosRetorna204() throws Exception {
        when(usuarioService.getUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/usuarios").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void obtenerUsuarioRetorna200ConSelf() throws Exception {
        when(usuarioService.getUsuario(1L)).thenReturn(new UsuarioResponseDto(1L, "ana", "ana@mail.cl"));

        mockMvc.perform(get("/api/v1/usuarios/1").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("/api/v1/usuarios/1")));
    }

    @Test
    void obtenerUsuarioInexistenteRetorna404() throws Exception {
        when(usuarioService.getUsuario(99L)).thenThrow(new EntityNotFoundException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void lookupUsuarioRetorna200() throws Exception {
        UsuarioLookupDto lookup = new UsuarioLookupDto(1L, "ana", "ana@mail.cl");
        when(usuarioService.findUsuarioLookupById(1L)).thenReturn(Optional.of(lookup));

        mockMvc.perform(get("/api/v1/usuarios/1/lookup"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ana@mail.cl")));
    }

    @Test
    void lookupUsuarioInexistenteRetorna404() throws Exception {
        when(usuarioService.findUsuarioLookupById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99/lookup"))
                .andExpect(status().isNotFound());
    }

    @Test
    void guardarUsuarioRetorna201ConLocationYLinks() throws Exception {
        UsuarioRequestDto request = buildRequest("ana", "clave123", "ana@mail.cl");
        UsuarioResponseDto saved = new UsuarioResponseDto(2L, "ana", "ana@mail.cl");
        when(usuarioService.saveUsuario(any(UsuarioRequestDto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/usuarios/2")))
                .andExpect(content().string(containsString("\"self\"")));
    }

    @Test
    void guardarUsuarioInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarUsuarioRetorna200ConHateoas() throws Exception {
        UsuarioRequestDto request = buildRequest("ana", "clave123", "ana@mail.cl");
        UsuarioResponseDto updated = new UsuarioResponseDto(1L, "ana", "ana@mail.cl");
        when(usuarioService.updateUsuario(any(Long.class), any(UsuarioRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")));
    }

    @Test
    void actualizarUsuarioInexistenteRetorna404() throws Exception {
        UsuarioRequestDto request = buildRequest("ana", "clave123", "ana@mail.cl");
        when(usuarioService.updateUsuario(any(Long.class), any(UsuarioRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Usuario no encontrado"));

        mockMvc.perform(put("/api/v1/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarUsuarioRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).deleteUsuario(1L);
    }

    @Test
    void eliminarUsuarioInexistenteRetorna404() throws Exception {
        org.mockito.Mockito.doThrow(new EntityNotFoundException("Usuario no encontrado"))
                .when(usuarioService).deleteUsuario(99L);

        mockMvc.perform(delete("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    private UsuarioRequestDto buildRequest(String nombreUsuario, String clave, String correo) {
        UsuarioRequestDto request = new UsuarioRequestDto();
        request.setNombreUsuario(nombreUsuario);
        request.setClave(clave);
        request.setCorreo(correo);
        return request;
    }
}
