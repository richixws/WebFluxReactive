package com.programacion.reactiva.handler;

import com.mongodb.internal.connection.Server;
import com.programacion.reactiva.models.document.Producto;
import com.programacion.reactiva.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Component
public class ProductoHandler {

    @Autowired
    private ProductoService productoService;

/**
    public Mono<ServerResponse> upload(ServerRequest request){
        String id=request.pathVariable("id");
        return request.multipartData().map(multipart -> multipart.toSingleValueMap().get("file"))
                .cast(FilePart.class)
                .flatMap(file -> productoService.findById(id)
                        .flatMap(p -> {
                            p.setFoto(file.filename());
                        }))


    }**/

    public Mono<ServerResponse> listar(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.findAll(), Producto.class);
    }

    public Mono<ServerResponse> ver(ServerRequest request) {

        String id = request.pathVariable("id");
        return productoService.findById(id).flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(p)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> crear(ServerRequest request) {

        // Obtener los datos de la solicitud (por ejemplo, el cuerpo de la solicitud)
        return request.bodyToMono(Producto.class) // Supongamos que estás enviando un objeto Producto en el cuerpo de la solicitud
                .flatMap(producto -> {
                    if (producto.getId() == null) {
                        producto.setCreateAt(new Date());
                    }
                    return productoService.save(producto);
                }) // Supongamos que productoService.save() guarda el producto y devuelve un Mono<Producto>
                .flatMap(savedProduct -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(savedProduct)))
                .onErrorResume(Exception.class, ex -> ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("Error al procesar la solicitud")));
    }

    public Mono<ServerResponse> update(ServerRequest request){

        String id=request.pathVariable("id");
        Mono<Producto> producto=request.bodyToMono(Producto.class);

        Mono<Producto> productoDb=productoService.findById(id);
        return productoDb.zipWith(producto,(db,req) -> {
            db.setNombre(req.getNombre());
            db.setPrecio(req.getPrecio());
            db.setCategoria(req.getCategoria());

            return db;
        }).flatMap( p -> ServerResponse.created(URI.create("/api/v2/productos".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.save(p),Producto.class))
          .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updatev2(ServerRequest request){

        String id=request.pathVariable("id");
        Mono<Producto> productos=request.bodyToMono(Producto.class);

        Mono<Producto> productoDb=productoService.findById(id);
        return productoDb.zipWith(productos ,(db,req)->{
            db.setNombre(req.getNombre());
            db.setPrecio(req.getPrecio());
            db.setCategoria(req.getCategoria());
            return db;
        }).flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.save(p), Producto.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> eliminar(ServerRequest request){
        String id=request.pathVariable("id");

        Mono<Producto> productoBd=productoService.findById(id);
        return productoBd.flatMap( p -> productoService.deleteProducto(p.getId()).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
