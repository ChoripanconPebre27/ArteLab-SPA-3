package cl.artelab_spa.artelab.service;

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

import cl.artelab_spa.artelab.exception.BusinessRuleViolationException;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Producto;
import cl.artelab_spa.artelab.repository.CategoriaRepository;
import cl.artelab_spa.artelab.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void getAllReturnsAllProductosFromRepository() {
        Producto producto = buildProducto(1L, "Cuaderno", 1500, 10);
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> productos = productoService.getAll();

        assertNotNull(productos);
        assertEquals(1, productos.size());
        assertEquals("Cuaderno", productos.get(0).getDes());
    }

    @Test
    void savePersistsProductoWhenDataIsValid() {
        Producto producto = buildProducto(null, "Lapiz", 1200, 5);
        Producto persisted = buildProducto(10L, "Lapiz", 1200, 5);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(buildCategoria()));
        when(productoRepository.save(producto)).thenReturn(persisted);

        Producto result = productoService.save(producto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(productoRepository).save(producto);
    }

    @Test
    void saveRejectsNegativePrice() {
        Producto producto = buildProducto(null, "Goma", -100, 1);

        assertThrows(BusinessRuleViolationException.class, () -> productoService.save(producto));
    }

    @Test
    void saveRejectsNegativeStock() {
        Producto producto = buildProducto(null, "Goma", 100, -1);

        assertThrows(BusinessRuleViolationException.class, () -> productoService.save(producto));
    }

    @Test
    void saveRejectsMissingCategoria() {
        Producto producto = buildProducto(null, "Goma", 100, 1);
        producto.setCategoria(null);

        assertThrows(BusinessRuleViolationException.class, () -> productoService.save(producto));
    }

    @Test
    void saveThrowsWhenCategoriaDoesNotExist() {
        Producto producto = buildProducto(null, "Goma", 100, 1);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productoService.save(producto));
    }

    @Test
    void getByIdReturnsProductoWhenItExists() {
        Producto producto = buildProducto(1L, "Cuaderno", 1500, 10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto found = productoService.getById(1L);

        assertEquals("Cuaderno", found.getDes());
    }

    @Test
    void getByIdThrowsWhenProductoDoesNotExist() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productoService.getById(99L));
    }

    @Test
    void updateModifiesExistingProducto() {
        Producto existing = buildProducto(1L, "Cuaderno", 2500, 4);
        Producto nuevo = buildProducto(null, "Cuaderno Premium", 3200, 6);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(buildCategoria()));
        when(productoRepository.save(existing)).thenReturn(existing);

        Producto updated = productoService.update(1L, nuevo);

        assertEquals("Cuaderno Premium", updated.getDes());
        assertEquals(3200, updated.getPrecio());
        assertEquals(6, updated.getStock());
    }

    @Test
    void updateThrowsWhenProductoDoesNotExist() {
        Producto nuevo = buildProducto(null, "Cuaderno Premium", 3200, 6);
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productoService.update(99L, nuevo));
    }

    @Test
    void deleteRemovesExistingProducto() {
        Producto producto = buildProducto(1L, "Cuaderno", 1500, 10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        productoService.delete(1L);

        verify(productoRepository).delete(producto);
    }

    @Test
    void deleteThrowsWhenProductoDoesNotExist() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productoService.delete(99L));
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
