package com.programacion.reactiva;

import com.programacion.reactiva.models.document.Categoria;
import com.programacion.reactiva.models.document.Producto;
import com.programacion.reactiva.models.document.Usuario;
import com.programacion.reactiva.respository.ProductoRepository;
import com.programacion.reactiva.service.CategoriaService;
import com.programacion.reactiva.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class ProgramacionReactivaApplication implements CommandLineRunner {

    private static final Logger log= LoggerFactory.getLogger(ProgramacionReactivaApplication.class);

    @Autowired
    private ProductoService service;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(ProgramacionReactivaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        mongoTemplate.dropCollection("productos").subscribe();
        mongoTemplate.dropCollection("categorias").subscribe();

        Categoria electronico=new Categoria("Electronico");
        Categoria deporte=new Categoria("Deporte");
        Categoria computacion=new Categoria("Computacion");
        Categoria muebles=new Categoria("muebles");

        Flux.just(electronico,deporte,computacion,muebles)
            .flatMap( c -> service.saveCategoria(c))
            .doOnNext( c -> log.info("Categoria creada"+c.getNombre()+", Id"+c.getId()))
            .thenMany(
                    Flux.just( new Producto("tv panasonic Pantalla lcd",456.90,electronico),
                               new Producto("sonuy camara",177.90,electronico),
                               new Producto("Apple tv",200.80,electronico),
                               new Producto("mouse logitech", 400.50,computacion))
                         .flatMap(producto -> {
                                producto.setCreateAt(new Date());
                                return service.save(producto);
                          })
                    ).subscribe( producto -> log.info("Insert: "+producto.getNombre()+" "+producto.getPrecio()));


    }
}
