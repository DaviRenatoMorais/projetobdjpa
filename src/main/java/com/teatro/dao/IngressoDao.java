package com.teatro.dao;

import com.teatro.model.Contrato;
import com.teatro.model.Ingresso;
import com.teatro.model.Peca;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class IngressoDao implements Dao <Ingresso, Long> {
    @Override
    public void salvar(Ingresso ingresso) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(ingresso);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void atualizar(Ingresso ingresso) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(ingresso);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(Ingresso ingresso) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Ingresso encontrada = em.find(Ingresso.class, ingresso.getId());

            if (encontrada != null) {
                em.remove(encontrada);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Ingresso buscarPorId(Long id) {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Ingresso.class, id);
        }
    }

    @Override
    public List<Ingresso> listarTodos() {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT i FROM Ingresso i",
                    Ingresso.class)
            .getResultList();
        }
    }

    public boolean existeIngressoDepoisDaData(
            Contrato contrato,
            LocalDate dataEncerramento) {

        try(EntityManager em = JpaUtil.getEntityManager()) {
            Long quantidade = em.createQuery(
                            "SELECT COUNT(i) FROM Ingresso i " +
                                    "WHERE i.sessao.peca = :peca " +
                                    "AND i.sessao.data > :dataEncerramento",
                            Long.class
                    )
                    .setParameter("peca", contrato.getPeca())
                    .setParameter("dataEncerramento", dataEncerramento)
                    .getSingleResult();
            return quantidade > 0;
        }
    }

    public List<Ingresso> buscarPorPecaEData(Peca peca, LocalDate data) {
        try (EntityManager em = JpaUtil.getEntityManager()){
            return em.createQuery(
                            "SELECT i FROM Ingresso i " +
                                    "WHERE i.sessao.peca = :peca " +
                                    "AND i.sessao.data = :data " +
                                    "ORDER BY i.sessao.horarioInicio, i.cliente.nomeCompleto",
                            Ingresso.class
                    )
                    .setParameter("peca", peca)
                    .setParameter("data", data)
                    .getResultList();

        }
    }
}
