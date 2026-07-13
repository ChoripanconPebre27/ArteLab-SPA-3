package cl.artelab_spa.artelab.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.artelab_spa.artelab.client.UsuarioClient;
import cl.artelab_spa.artelab.dto.UsuarioLookupDto;
import cl.artelab_spa.artelab.exception.BusinessRuleViolationException;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Promocion;
import cl.artelab_spa.artelab.repository.CategoriaRepository;
import cl.artelab_spa.artelab.repository.PromocionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PromocionService {

    private static final Logger log = LoggerFactory.getLogger(PromocionService.class);

    private final PromocionRepository promocionRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioClient usuarioClient;

    public PromocionService(
            PromocionRepository promocionRepository,
            CategoriaRepository categoriaRepository,
            UsuarioClient usuarioClient) {
        this.promocionRepository = promocionRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioClient = usuarioClient;
    }

    public List<Promocion> getAll() {
        List<Promocion> promociones = promocionRepository.findAll();
        log.info("Retrieved promociones, count={}", promociones.size());
        return promociones;
    }

    public Promocion save(Promocion promocion) {
        validateDiscount(promocion.getDescuento());
        validateDates(promocion);
        promocion.setCategoria(resolveCategoria(promocion.getCategoria()));
        Promocion saved = promocionRepository.save(promocion);
        log.info("Created promocion id={} descuento={}", saved.getId(), saved.getDescuento());
        return saved;
    }

    public Promocion saveForUsuario(Long usuarioId, Promocion promocion) {
        UsuarioLookupDto usuario = usuarioClient.findUsuarioById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        validateDiscount(promocion.getDescuento());
        validateDates(promocion);
        promocion.setCategoria(resolveCategoria(promocion.getCategoria()));
        Promocion saved = promocionRepository.save(promocion);
        log.info("Created promocion id={} for usuario id={} nombreUsuario={}",
                saved.getId(), usuario.getId(), usuario.getNombreUsuario());
        return saved;
    }

    public Promocion getById(Long id) {
        log.info("Looking up promocion by id={}", id);
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promocion no encontrada"));
        log.info("Found promocion id={}", id);
        return promocion;
    }

    public Promocion update(Long id, Promocion nuevo) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promocion no encontrada"));
        validateDiscount(nuevo.getDescuento());
        validateDates(nuevo);
        promocion.setDes(nuevo.getDes());
        promocion.setDescuento(nuevo.getDescuento());
        promocion.setFechaIni(nuevo.getFechaIni());
        promocion.setFechaTer(nuevo.getFechaTer());
        promocion.setCategoria(resolveCategoria(nuevo.getCategoria()));
        Promocion updated = promocionRepository.save(promocion);
        log.info("Updated promocion id={} descuento={}", updated.getId(), updated.getDescuento());
        return updated;
    }

    public void delete(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promocion no encontrada"));
        promocionRepository.delete(promocion);
        log.info("Deleted promocion id={}", id);
    }

    private void validateDiscount(Integer descuento) {
        if (descuento == null || descuento <= 0 || descuento > 100) {
            log.warn("Promocion discount business rule violation: {}", descuento);
            throw new BusinessRuleViolationException("El descuento debe estar entre 1 y 100.");
        }
    }

    private void validateDates(Promocion promocion) {
        if (promocion.getFechaIni() == null || promocion.getFechaTer() == null) {
            log.warn("Promocion date business rule violation: missing dates");
            throw new BusinessRuleViolationException("Las fechas de inicio y termino son obligatorias.");
        }
        if (promocion.getFechaTer().isBefore(promocion.getFechaIni())) {
            log.warn("Promocion date business rule violation: fechaIni={} fechaTer={}",
                    promocion.getFechaIni(), promocion.getFechaTer());
            throw new BusinessRuleViolationException("La fecha de termino no puede ser anterior a la fecha de inicio.");
        }
    }

    private Categoria resolveCategoria(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            log.warn("Promocion category business rule violation: missing category");
            throw new BusinessRuleViolationException("La categoria de la promocion es obligatoria.");
        }
        return categoriaRepository.findById(categoria.getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada"));
    }
}
