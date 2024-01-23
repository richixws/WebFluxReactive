package com.programacion.reactiva.controller;

import com.programacion.reactiva.models.document.Producto;
import com.programacion.reactiva.respository.ProductoRepository;
import com.programacion.reactiva.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String path;

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<Producto>> upload(@PathVariable String id, @RequestPart FilePart file){

        return  productoService.findById(id).flatMap(p -> {

            p.setFoto(UUID.randomUUID().toString()+"-"+file.filename()
                    .replace(" ","")
                    .replace(":","")
                    .replace("",""));
            return  file.transferTo(new File(path+p.getFoto())).then(productoService.save(p));
        }).map( p -> ResponseEntity.ok(p)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    //version 2 upload con objeto
    @PostMapping("/v2")
    public  Mono<ResponseEntity<Producto>> crearConFoto(Producto producto, @RequestPart FilePart file){

        if (producto.getCreateAt() == null){
            producto.setCreateAt(new Date());
        }

        producto.setFoto(UUID.randomUUID().toString()+"-"+file.filename()
                .replace(" ","")
                .replace(":","")
                .replace("",""));

        return file.transferTo(new File(path+producto.getFoto())).then(productoService.save(producto))
                .map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p));
    }


    @GetMapping
    private Mono<ResponseEntity<Flux<Producto>>> listar(){

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productoService.findAll()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> detalle(@PathVariable("id") String id){

        return productoService.findById(id).map( p -> ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String,Object>>> crear(@Valid @RequestBody Mono<Producto> monoProducto){

        Map<String, Object> respuesta=new HashMap<>();

        return monoProducto.flatMap(producto -> {
            if(producto.getCreateAt() == null){
                producto.setCreateAt(new Date());
            }

            return productoService.save(producto).map( p ->{
                   respuesta.put("producto",p);
                   respuesta.put("mensaje","producto creado con exito");
                   respuesta.put("timestamp",new Date());
                   return  ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(respuesta);
            });

        }).onErrorResume( t ->{
            return Mono.just(t).cast(WebExchangeBindException.class)
                    .flatMap(e -> Mono.just(e.getFieldErrors()))
                    .flatMapMany(errors -> Flux.fromIterable(errors))
                    .map(fieldError -> "El campo"+fieldError.getField()+" "+fieldError.getDefaultMessage())
                    .collectList()
                    .flatMap(list -> {
                        respuesta.put("errors",list);
                        respuesta.put("timestamp",new Date());
                        respuesta.put("status",HttpStatus.BAD_REQUEST.value());
                        return Mono.just(ResponseEntity.badRequest().body(respuesta));
                    });
        });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> updateproducto(@RequestBody Producto producto, @PathVariable String id){

        return productoService.findById(id).flatMap(p ->{
            p.setNombre(producto.getNombre());
            p.setPrecio(producto.getPrecio());
            p.setCategoria(producto.getCategoria());
            return productoService.save(p);

        }).map(p -> ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminarProducto(@PathVariable String id){

        return  productoService.findById(id).flatMap(p -> {
           return  productoService.deleteProducto(p.getId()).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }






}
