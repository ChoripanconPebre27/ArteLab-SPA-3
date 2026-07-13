package cl.artelab_spa.artelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.artelab_spa.artelab.model.Categoria;


@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByDes(String des);

    boolean existsByDesAndIdNot(String des, Long id);
}