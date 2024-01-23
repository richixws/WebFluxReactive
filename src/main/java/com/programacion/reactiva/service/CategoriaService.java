package com.programacion.reactiva.service;

import com.programacion.reactiva.models.document.Categoria;
import reactor.core.publisher.Mono;

public interface CategoriaService {
    Mono<Categoria> save(Categoria categoria);
}
