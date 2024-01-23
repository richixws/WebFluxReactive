package com.programacion.reactiva.service;

import com.programacion.reactiva.models.document.Categoria;
import com.programacion.reactiva.models.document.Producto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ProductoService {

    Flux<Producto> findAll();
    Mono<Producto> findById(String id);
    Mono<Producto> save(Producto producto);
    Mono<Categoria> saveCategoria(Categoria categoria);

    Mono<Void> deleteProducto(String id);


}
