package com.programacion.reactiva.respository;

import com.programacion.reactiva.models.document.Categoria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoriaRepository extends ReactiveMongoRepository<Categoria, String> {

}
