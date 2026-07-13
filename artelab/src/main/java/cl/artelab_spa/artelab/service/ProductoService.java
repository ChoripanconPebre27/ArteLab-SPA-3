package cl.artelab_spa.artelab.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.artelab_spa.artelab.exception.BusinessRuleViolationException;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Producto;
import cl.artelab_spa.artelab.repository.CategoriaRepository;
import cl.artelab_spa.artelab.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


@Service
@Transactional
public class ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }


    public List<Producto> getAll() {
        List<Producto> productos = productoRepository.findAll();
        log.info("Retrieved productos, count={}", productos.size());
        return productos;
    }


    public Producto save(Producto producto) {
        validatePrice(producto.getPrecio());
        validateStock(producto.getStock());
        producto.setCategoria(resolveCategoria(producto.getCategoria()));
        Producto saved = productoRepository.save(producto);
        log.info("Created producto id={} precio={} stock={}", saved.getId(), saved.getPrecio(), saved.getStock());
        return saved;
    }


    public Producto getById(Long id) {
        log.info("Looking up producto by id={}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        log.info("Found producto id={}", id);
        return producto;
    }


    public Producto update(Long id, Producto nuevo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        validatePrice(nuevo.getPrecio());
        validateStock(nuevo.getStock());
        producto.setDes(nuevo.getDes());
        producto.setPrecio(nuevo.getPrecio());
        producto.setStock(nuevo.getStock());
        producto.setCategoria(resolveCategoria(nuevo.getCategoria()));
        Producto updated = productoRepository.save(producto);
        log.info("Updated producto id={} precio={} stock={}", updated.getId(), updated.getPrecio(), updated.getStock());
        return updated;
    }


    public void delete(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
        productoRepository.delete(producto);
        log.info("Deleted producto id={}", id);
    }


    private void validatePrice(Integer precio) {
        if (precio == null || precio <= 0) {
            log.warn("Producto price business rule violation: {}", precio);
            throw new BusinessRuleViolationException("El precio debe ser mayor que cero.");
        }
    }

    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            log.warn("Producto stock business rule violation: {}", stock);
            throw new BusinessRuleViolationException("El stock no puede ser negativo.");
        }
    }

    private Categoria resolveCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            log.warn("Producto category business rule violation: missing category");
            throw new BusinessRuleViolationException("La categoria del producto es obligatoria.");
        }
        return categoriaRepository.findById(categoria.getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    }
}

