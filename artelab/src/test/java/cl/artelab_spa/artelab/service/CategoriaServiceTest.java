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

import cl.artelab_spa.artelab.exception.ResourceConflictException;
import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void getAllReturnsAllCategorias() {
        Categoria categoria = new Categoria(1L, "Lápices", null, null);
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> categorias = categoriaService.getAll();

        assertNotNull(categorias);
        assertEquals(1, categorias.size());
        assertEquals("Lápices", categorias.get(0).getDes());
    }

    @Test
    void savePersistsCategoriaWhenDescriptionIsUnique() {
        Categoria categoria = new Categoria(null, "Lápices", null, null);
        Categoria persisted = new Categoria(1L, "Lápices", null, null);
        when(categoriaRepository.existsByDes("Lápices")).thenReturn(false);
        when(categoriaRepository.save(categoria)).thenReturn(persisted);

        Categoria saved = categoriaService.save(categoria);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(categoriaRepository).save(categoria);
    }

    @Test
    void saveRejectsDuplicateDescription() {
        Categoria categoria = new Categoria(null, "Lápices", null, null);
        when(categoriaRepository.existsByDes("Lápices")).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> categoriaService.save(categoria));
    }

    @Test
    void getByIdThrowsWhenCategoriaDoesNotExist() {
        when(categoriaRepository.findById(55L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoriaService.getById(55L));
    }

    @Test
    void updateModifiesCategoriaWhenDescriptionIsUnique() {
        Categoria existing = new Categoria(1L, "Papeleria", null, null);
        Categoria request = new Categoria(null, "Pinturas", null, null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.existsByDesAndIdNot("Pinturas", 1L)).thenReturn(false);
        when(categoriaRepository.save(existing)).thenReturn(existing);

        Categoria updated = categoriaService.update(1L, request);

        assertEquals("Pinturas", updated.getDes());
        verify(categoriaRepository).save(existing);
    }

    @Test
    void updateRejectsDuplicateDescription() {
        Categoria existing = new Categoria(1L, "Papeleria", null, null);
        Categoria request = new Categoria(null, "Pinturas", null, null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoriaRepository.existsByDesAndIdNot("Pinturas", 1L)).thenReturn(true);

        assertThrows(ResourceConflictException.class, () -> categoriaService.update(1L, request));
    }

    @Test
    void deleteRemovesExistingCategoria() {
        Categoria categoria = new Categoria(1L, "Papeleria", null, null);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        categoriaService.delete(1L);

        verify(categoriaRepository).delete(categoria);
    }

    @Test
    void deleteThrowsWhenCategoriaDoesNotExist() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoriaService.delete(99L));
    }
}
