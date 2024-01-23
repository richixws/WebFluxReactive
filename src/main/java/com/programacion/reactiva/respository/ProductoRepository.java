package com.programacion.reactiva.respository;

import com.programacion.reactiva.models.document.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoRepository extends ReactiveMongoRepository<Producto,String> {

}
