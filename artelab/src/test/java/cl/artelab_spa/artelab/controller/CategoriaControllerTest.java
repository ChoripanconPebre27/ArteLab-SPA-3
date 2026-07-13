package cl.artelab_spa.artelab.controller;

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

import cl.artelab_spa.artelab.assemblers.CategoriaModelAssembler;
import cl.artelab_spa.artelab.exception.GlobalExceptionHandler;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.service.CategoriaService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @Spy
    private CategoriaModelAssembler assembler = new CategoriaModelAssembler();

    @InjectMocks
    private CategoriaController categoriaController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listarCategoriasRetorna200ConHateoas() throws Exception {
        Categoria categoria = buildCategoria(1L, "Lapices");
        when(categoriaService.getAll()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/v1/categorias").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Lapices")))
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")))
                .andExpect(content().string(containsString("/api/v1/categorias/1")));

        verify(categoriaService).getAll();
    }

    @Test
    void listarCategoriasSinDatosRetorna204() throws Exception {
        when(categoriaService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categorias").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarCategoriaRetorna200ConSelf() throws Exception {
        Categoria categoria = buildCategoria(1L, "Papeleria");
        when(categoriaService.getById(1L)).thenReturn(categoria);

        mockMvc.perform(get("/api/v1/categorias/1").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("/api/v1/categorias/1")));
    }

    @Test
    void buscarCategoriaInexistenteRetorna404() throws Exception {
        when(categoriaService.getById(99L)).thenThrow(new EntityNotFoundException("Categoria no encontrada"));

        mockMvc.perform(get("/api/v1/categorias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarCategoriaRetorna201ConLocationYLinks() throws Exception {
        Categoria categoria = buildCategoria(null, "Papeleria");
        Categoria saved = buildCategoria(2L, "Papeleria");

        when(categoriaService.save(any(Categoria.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/categorias/2")))
                .andExpect(content().string(containsString("\"self\"")));
    }

    @Test
    void actualizarCategoriaRetorna200ConHateoas() throws Exception {
        Categoria nuevo = buildCategoria(null, "Pinturas");
        Categoria saved = buildCategoria(1L, "Pinturas");
        when(categoriaService.update(any(Long.class), any(Categoria.class))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")));
    }

    @Test
    void eliminarCategoriaRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).delete(1L);
    }

    private Categoria buildCategoria(Long id, String des) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setDes(des);
        return categoria;
    }
}
