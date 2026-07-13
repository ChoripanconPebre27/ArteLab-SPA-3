package cl.artelab_spa.artelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.artelab_spa.artelab.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

}
