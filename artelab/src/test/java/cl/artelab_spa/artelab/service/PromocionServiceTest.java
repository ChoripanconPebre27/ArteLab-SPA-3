package cl.artelab_spa.artelab.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.artelab_spa.artelab.client.UsuarioClient;
import cl.artelab_spa.artelab.dto.UsuarioLookupDto;
import cl.artelab_spa.artelab.exception.BusinessRuleViolationException;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Promocion;
import cl.artelab_spa.artelab.repository.CategoriaRepository;
import cl.artelab_spa.artelab.repository.PromocionRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private PromocionService promocionService;

    @Test
    void getAllReturnsPromocionesFromRepository() {
        Promocion promocion = buildPromocion(1L, "Navidad", 15);
        when(promocionRepository.findAll()).thenReturn(List.of(promocion));

        List<Promocion> result = promocionService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void saveForUsuarioPersistsPromotionWhenUserExists() {
        Promocion promocion = buildPromocion(null, "Black Friday", 20);
        UsuarioLookupDto usuario = new UsuarioLookupDto(3L, "ana", "ana@mail.cl");
        when(usuarioClient.findUsuarioById(3L)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(buildCategoria()));
        when(promocionRepository.save(promocion)).thenReturn(promocion);

        Promocion saved = promocionService.saveForUsuario(3L, promocion);

        assertNotNull(saved);
        assertEquals("Black Friday", saved.getDes());
        verify(promocionRepository).save(promocion);
    }

    @Test
    void savePersistsPromotionWhenDataIsValid() {
        Promocion promocion = buildPromocion(null, "Invierno", 20);
        Promocion persisted = buildPromocion(2L, "Invierno", 20);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(buildCategoria()));
        when(promocionRepository.save(promocion)).thenReturn(persisted);

        Promocion saved = promocionService.save(promocion);

        assertEquals(2L, saved.getId());
        verify(promocionRepository).save(promocion);
    }

    @Test
    void saveForUsuarioThrowsWhenUserDoesNotExist() {
        Promocion promocion = buildPromocion(null, "Cyber", 25);
        when(usuarioClient.findUsuarioById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promocionService.saveForUsuario(99L, promocion));
    }

    @Test
    void saveRejectsDiscountOutsideRange() {
        Promocion promocion = buildPromocion(null, "Cyber", 101);

        assertThrows(BusinessRuleViolationException.class, () -> promocionService.save(promocion));
    }

    @Test
    void saveRejectsEndDateBeforeStartDate() {
        Promocion promocion = buildPromocion(null, "Cyber", 20);
        promocion.setFechaTer(LocalDate.of(2026, 6, 30));

        assertThrows(BusinessRuleViolationException.class, () -> promocionService.save(promocion));
    }

    @Test
    void saveRejectsMissingCategoria() {
        Promocion promocion = buildPromocion(null, "Cyber", 20);
        promocion.setCategoria(null);

        assertThrows(BusinessRuleViolationException.class, () -> promocionService.save(promocion));
    }

    @Test
    void saveThrowsWhenCategoriaDoesNotExist() {
        Promocion promocion = buildPromocion(null, "Cyber", 20);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promocionService.save(promocion));
    }

    @Test
    void getByIdReturnsPromotionWhenItExists() {
        Promocion promocion = buildPromocion(1L, "Cyber", 20);
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));

        Promocion found = promocionService.getById(1L);

        assertEquals("Cyber", found.getDes());
    }

    @Test
    void getByIdThrowsWhenPromotionDoesNotExist() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promocionService.getById(99L));
    }

    @Test
    void updateModifiesExistingPromotion() {
        Promocion existing = buildPromocion(1L, "Cyber", 20);
        Promocion request = buildPromocion(null, "Cyber Plus", 30);
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(buildCategoria()));
        when(promocionRepository.save(existing)).thenReturn(existing);

        Promocion updated = promocionService.update(1L, request);

        assertEquals("Cyber Plus", updated.getDes());
        assertEquals(30, updated.getDescuento());
    }

    @Test
    void updateThrowsWhenPromotionDoesNotExist() {
        Promocion request = buildPromocion(null, "Cyber Plus", 30);
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promocionService.update(99L, request));
    }

    @Test
    void deleteRemovesExistingPromotion() {
        Promocion promocion = buildPromocion(1L, "Cyber", 20);
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));

        promocionService.delete(1L);

        verify(promocionRepository).delete(promocion);
    }

    @Test
    void deleteThrowsWhenPromotionDoesNotExist() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promocionService.delete(99L));
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
}
