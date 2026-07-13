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

import java.time.LocalDate;
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

import cl.artelab_spa.artelab.assemblers.PromocionModelAssembler;
import cl.artelab_spa.artelab.exception.GlobalExceptionHandler;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Promocion;
import cl.artelab_spa.artelab.service.PromocionService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PromocionControllerTest {

    @Mock
    private PromocionService promocionService;

    @Spy
    private PromocionModelAssembler assembler = new PromocionModelAssembler();

    @InjectMocks
    private PromocionController promocionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(promocionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listarPromocionesRetorna200ConHateoas() throws Exception {
        Promocion promocion = buildPromocion(1L, "Verano", 15);
        when(promocionService.getAll()).thenReturn(List.of(promocion));

        mockMvc.perform(get("/api/v1/promociones").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Verano")))
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")))
                .andExpect(content().string(containsString("/api/v1/promociones/1")));

        verify(promocionService).getAll();
    }

    @Test
    void listarPromocionesSinDatosRetorna204() throws Exception {
        when(promocionService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/promociones").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarPromocionRetorna200ConSelf() throws Exception {
        Promocion promocion = buildPromocion(1L, "Verano", 15);
        when(promocionService.getById(1L)).thenReturn(promocion);

        mockMvc.perform(get("/api/v1/promociones/1").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("/api/v1/promociones/1")));
    }

    @Test
    void buscarPromocionInexistenteRetorna404() throws Exception {
        when(promocionService.getById(99L)).thenThrow(new EntityNotFoundException("Promocion no encontrada"));

        mockMvc.perform(get("/api/v1/promociones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarPromocionRetorna201ConLocationYLinks() throws Exception {
        Promocion saved = buildPromocion(2L, "Invierno", 20);
        when(promocionService.save(any(Promocion.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/promociones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPromocionJson("Invierno", 20, "2026-07-01", "2026-07-10")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/promociones/2")))
                .andExpect(content().string(containsString("\"self\"")));
    }

    @Test
    void agregarPromocionInvalidaRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/promociones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPromocionJson("Invierno", 101, "2026-07-01", "2026-07-10")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarPromocionRetorna200ConHateoas() throws Exception {
        Promocion saved = buildPromocion(1L, "Primavera", 25);
        when(promocionService.update(any(Long.class), any(Promocion.class))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/promociones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPromocionJson("Primavera", 25, "2026-09-01", "2026-09-10")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")));
    }

    @Test
    void eliminarPromocionRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/promociones/1"))
                .andExpect(status().isNoContent());

        verify(promocionService).delete(1L);
    }

    private Promocion buildPromocion(Long id, String des, Integer descuento) {
        Promocion promocion = new Promocion();
        promocion.setId(id);
        promocion.setDes(des);
        promocion.setDescuento(descuento);
        promocion.setFechaIni(LocalDate.of(2026, 7, 1));
        promocion.setFechaTer(LocalDate.of(2026, 7, 10));
        promocion.setCategoria(buildCategoria());
        return promocion;
    }

    private Categoria buildCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setDes("Lapices");
        return categoria;
    }

    private String validPromocionJson(String des, int descuento, String fechaIni, String fechaTer) {
        return """
                {
                  "des":"%s",
                  "descuento":%d,
                  "fechaIni":"%s",
                  "fechaTer":"%s",
                  "categoria":{"id":1}
                }
                """.formatted(des, descuento, fechaIni, fechaTer);
    }
}
