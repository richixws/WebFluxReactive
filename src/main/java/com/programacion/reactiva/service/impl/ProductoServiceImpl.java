package com.programacion.reactiva.service.impl;

import com.programacion.reactiva.models.document.Categoria;
import com.programacion.reactiva.models.document.Producto;
import com.programacion.reactiva.respository.CategoriaRepository;
import com.programacion.reactiva.respository.ProductoRepository;
import com.programacion.reactiva.service.CategoriaService;
import com.programacion.reactiva.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private CategoriaRepository carepository;

    @Override
    public Flux<Producto> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Producto> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return repository.save(producto);
    }

    @Override
    public Mono<Categoria> saveCategoria(Categoria categoria) {
        return carepository.save(categoria);
    }

    @Override
    public Mono<Void> deleteProducto(String id) {
        return carepository.deleteById(id);
    }
}
