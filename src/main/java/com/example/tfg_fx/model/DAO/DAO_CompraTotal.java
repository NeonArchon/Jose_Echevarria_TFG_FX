package com.example.tfg_fx.model.DAO;

import com.example.tfg_fx.model.entities.CompraTotal;
import com.example.tfg_fx.model.entities.Comprar;
import com.example.tfg_fx.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DAO_CompraTotal implements DAO_CompraTotal_Itf {


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

    public void registrarCompraTotal(CompraTotal compraTotal) {

            Session session = null;
            Transaction tx = null;

            try {
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();

                // reatach usuario
                compraTotal.setUsuario(
                        session.contains(compraTotal.getUsuario())
                                ? compraTotal.getUsuario()
                                : session.merge(compraTotal.getUsuario())
                );

                // reatach de cada item
                for (Comprar item : compraTotal.getItemsCompra()) {

                    // merge del producto
                    item.setProducto(
                            session.contains(item.getProducto())
                                    ? item.getProducto()
                                    : session.merge(item.getProducto())
                    );

                    // merge del usuario en Comprar
                    if (item.getUsuario() != null) {
                        item.setUsuario(
                                session.contains(item.getUsuario())
                                        ? item.getUsuario()
                                        : session.merge(item.getUsuario())
                        );
                    }

                    // relación con compra total
                    item.setCompraTotal(compraTotal);
                }

                session.persist(compraTotal);

                tx.commit();

            } catch (Exception e) {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
                e.printStackTrace();

            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        }
    }