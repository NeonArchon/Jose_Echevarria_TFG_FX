package com.example.tfg_fx.model.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "comprar")
public class Comprar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // clave primaria única

    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "id_producto")
    private Long idProducto;

    private int cantidad;

}
