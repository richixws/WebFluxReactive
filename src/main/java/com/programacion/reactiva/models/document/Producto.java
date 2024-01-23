package com.programacion.reactiva.models.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "productos")
public class Producto {

    @Id
    private String id;
    @NotEmpty(message = "el campo nombre no puede ser vacio")
    private String nombre;
    @NotNull(message = "el campo precio no puede ser vacio")
    private Double precio;

    @Valid
    @NotNull
    private  Categoria categoria;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;

    private  String foto;

    public Producto(String nombre, Double precio, Categoria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria=categoria;
    }


}
