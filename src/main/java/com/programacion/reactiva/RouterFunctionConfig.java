package com.programacion.reactiva;

import com.programacion.reactiva.handler.ProductoHandler;
import com.programacion.reactiva.models.document.Producto;
import com.programacion.reactiva.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig   {


    @Bean
    public RouterFunction<ServerResponse> routes(ProductoHandler handler){

     //   return route(GET("/api/v2/productos").or(GET("/api/v3/productos")), request -> handler.listar(request));
        return route(GET("/api/v2/productos").or(GET("/api/v3/productos")), handler::listar)
                .andRoute(GET("/api/v2/productos/{id}"),handler::ver)
                .andRoute(POST("/api/v2/productos"),handler::crear)
                .andRoute(PUT("/api/v2/productos/{id}"),handler::update)
                .andRoute(DELETE("/api/v2/productos/{id}"),handler::eliminar)
                .andRoute(POST("/api/v2/productos/upload/{id}"), handler::upload);

    }


}
