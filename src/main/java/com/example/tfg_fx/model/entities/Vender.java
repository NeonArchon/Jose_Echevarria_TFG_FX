package com.example.tfg_fx.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import com.example.tfg_fx.model.entities.*;

import jakarta.persistence.*;

import jakarta.persistence.*;

@Entity
@Table(name = "vender")
public class Vender {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // para autoincremental en MySQL
        private Long id;

        // Resto de atributos
        private String descripcion;
        private double precio;

        // Constructor vacío
        public Vender() {}

        // Getters y Setters
        public Long getId() {
            return id;
        }

        //getters y setters


    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
