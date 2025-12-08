package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.Comprar;
import java.util.List;

public interface DAO_Comprar_Itf {

   public boolean registrarCompra(Comprar compra);

    List<Comprar> procesarCompras(List<Comprar> compras);

    Comprar obtenerCompraPorId(Long id);

    List<Comprar> obtenerComprasPorCliente(Long idCliente);

    List<Comprar> obtenerComprasPorProducto(Long idProducto);

    List<Comprar> obtenerTodasLasCompras();

    boolean eliminarCompra(Long id);

    void guardarItem(Comprar item);

    List<Comprar> obtenerItemsPorCompra(Long idCompraTotal);

}
