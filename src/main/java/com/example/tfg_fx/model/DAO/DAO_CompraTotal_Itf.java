package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.CompraTotal;
import java.util.List;

public interface DAO_CompraTotal_Itf {

    void guardarCompra(CompraTotal compra);

    CompraTotal obtenerCompraPorId(Long id);

    List<CompraTotal> obtenerComprasPorUsuario(Long idUsuario);

}
