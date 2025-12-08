package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.CompraTotal;
import com.example.tfg_fx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DAO_CompraTotal implements DAO_CompraTotal_Itf{


    @Override
    public void guardarCompra(CompraTotal compra) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(compra);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompraTotal obtenerCompraPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(CompraTotal.class, id);
        }
    }

    @Override
    public List<CompraTotal> obtenerComprasPorUsuario(Long idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM CompraTotal c WHERE c.usuario.id = :idUser",
                            CompraTotal.class
                    ).setParameter("idUser", idUsuario)
                    .getResultList();
        }
    }
}
