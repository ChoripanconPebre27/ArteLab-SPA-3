package cl.artelab_spa.artelab.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import cl.artelab_spa.artelab.model.Categoria;
import cl.artelab_spa.artelab.model.Producto;
import cl.artelab_spa.artelab.model.Promocion;
import cl.artelab_spa.artelab.repository.CategoriaRepository;
import cl.artelab_spa.artelab.repository.ProductoRepository;
import cl.artelab_spa.artelab.repository.PromocionRepository;
import net.datafaker.Faker;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final PromocionRepository promocionRepository;

    public DataLoader(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            PromocionRepository promocionRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.promocionRepository = promocionRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0 || productoRepository.count() > 0 || promocionRepository.count() > 0) {
            return;
        }

        Faker faker = new Faker();
        Random random = new Random();

        List<Categoria> categorias = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Categoria categoria = new Categoria();
            categoria.setDes(faker.commerce().department());
            categorias.add(categoria);
        }
        categorias = categoriaRepository.saveAll(categorias);

        List<Producto> productos = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Producto producto = new Producto();
            producto.setDes(faker.commerce().productName());
            producto.setPrecio(random.nextInt(20000) + 100);
            producto.setStock(random.nextInt(100));
            producto.setCategoria(categorias.get(random.nextInt(categorias.size())));
            productos.add(producto);
        }
        productoRepository.saveAll(productos);

        List<Promocion> promociones = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Promocion promocion = new Promocion();
            promocion.setDes(faker.commerce().promotionCode());
            promocion.setFechaIni(faker.date().future(30, java.util.concurrent.TimeUnit.DAYS)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());
            promocion.setFechaTer(faker.date().future(60, java.util.concurrent.TimeUnit.DAYS)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate());
            promocion.setDescuento(random.nextInt(50) + 1);
            promocion.setCategoria(categorias.get(random.nextInt(categorias.size())));
            promociones.add(promocion);
        }
        promocionRepository.saveAll(promociones);
    }
}
