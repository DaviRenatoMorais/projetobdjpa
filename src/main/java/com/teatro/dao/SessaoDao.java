package com.teatro.dao;

import com.teatro.model.Peca;
import com.teatro.model.Sessao;
import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class SessaoDao implements Dao <Sessao, Long> {
    @Override
    public void salvar(Sessao sessao) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(sessao);
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
    public void atualizar(Sessao sessao) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(sessao);
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
    public void remover(Sessao sessao) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();

            Sessao encontrada = em.find(Sessao.class, sessao.getId());

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
    public Sessao buscarPorId(Long id) {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.find(Sessao.class, id);
        }
    }

    @Override
    public List<Sessao> listarTodos() {
        try(EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    "SELECT s FROM Sessao s",
                    Sessao.class)
            .getResultList();
        }
    }

    public List<Sessao> buscarPorData(LocalDate data) {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT s FROM Sessao s WHERE s.data = :data",
                            Sessao.class)
                    .setParameter("data", data)
                    .getResultList();
        }
    }

    public List<Sessao> buscarPorPecaEPeriodo(
            Peca peca,
            LocalDate dataInicio,
            LocalDate dataFim){
        try (EntityManager em = JpaUtil.getEntityManager()) {
            return em.createQuery(
                    // JPQL consulta entidades Java, não diretamente as tabelas do banco
                    "SELECT s FROM Sessao s " + // busca objetos Sessao
                    "WHERE s.peca = :peca " + // somente sessões daquela peça
                    "AND s.data BETWEEN :dataInicio AND :dataFim", // somente sessões dentro do período do contrato
                    Sessao.class)
                    .setParameter("peca", peca)
                    .setParameter("dataInicio", dataInicio)
                    .setParameter("dataFim", dataFim)
                    .getResultList();
        }
    }
}
