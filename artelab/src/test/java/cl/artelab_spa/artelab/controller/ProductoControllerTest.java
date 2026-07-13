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

import cl.artelab_spa.artelab.assemblers.ProductoModelAssembler;
import cl.artelab_spa.artelab.exception.GlobalExceptionHandler;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Producto;
import cl.artelab_spa.artelab.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @Spy
    private ProductoModelAssembler assembler = new ProductoModelAssembler();

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listarProductosRetorna200ConHateoas() throws Exception {
        Producto producto = buildProducto(1L, "Lapiz", 500, 10);
        when(productoService.getAll()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/productos").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Lapiz")))
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")))
                .andExpect(content().string(containsString("/api/v1/productos/1")));

        verify(productoService).getAll();
    }

    @Test
    void listarProductosSinDatosRetorna204() throws Exception {
        when(productoService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/productos").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void buscarProductoRetorna200ConSelf() throws Exception {
        Producto producto = buildProducto(1L, "Lapiz", 500, 10);
        when(productoService.getById(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/v1/productos/1").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"self\"")))
                .andExpect(content().string(containsString("/api/v1/productos/1")));
    }

    @Test
    void buscarProductoInexistenteRetorna404() throws Exception {
        when(productoService.getById(99L)).thenThrow(new EntityNotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/api/v1/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarProductoRetorna201ConLocationYLinks() throws Exception {
        Producto producto = buildProducto(null, "Cuaderno", 1200, 5);
        Producto saved = buildProducto(2L, "Cuaderno", 1200, 5);
        when(productoService.save(any(Producto.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/productos/2")))
                .andExpect(content().string(containsString("\"self\"")));
    }

    @Test
    void agregarProductoInvalidoRetorna400() throws Exception {
        Producto producto = buildProducto(null, "Cuaderno", -1, 5);

        mockMvc.perform(post("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarProductoRetorna200ConHateoas() throws Exception {
        Producto nuevo = buildProducto(null, "Cuaderno Pro", 2000, 8);
        Producto saved = buildProducto(1L, "Cuaderno Pro", 2000, 8);
        when(productoService.update(any(Long.class), any(Producto.class))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"actualizar\"")))
                .andExpect(content().string(containsString("\"eliminar\"")));
    }

    @Test
    void eliminarProductoRetorna204() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isNoContent());

        verify(productoService).delete(1L);
    }

    private Producto buildProducto(Long id, String des, Integer precio, Integer stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setDes(des);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(buildCategoria());
        return producto;
    }

    private Categoria buildCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setDes("Lapices");
        return categoria;
    }
}
