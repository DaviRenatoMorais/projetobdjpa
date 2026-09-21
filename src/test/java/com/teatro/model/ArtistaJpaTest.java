package com.teatro.model;

import com.teatro.util.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ArtistaJpaTest {

    @Test
    void devePersistirEConsultarArtista() {

        EntityManager em = JpaUtil.getEntityManager();

        Artista artista = new Artista();

        artista.setCpf("98765432104");
        artista.setNomeCompleto("João da Silva");
        artista.setTelefone("83999999999");
        artista.setEmail("artista1.teste@email.com");
        artista.setGenero(TipoGenero.MASCULINO);
        artista.setDataNascimento(LocalDate.of(1990, 5, 10));

        em.getTransaction().begin();

        em.persist(artista);

        em.getTransaction().commit();

        Long id = artista.getId();

        em.close();

        EntityManager em2 = JpaUtil.getEntityManager();

        Artista artistaEncontrado = em2.find(Artista.class, id);

        assertNotNull(artistaEncontrado);
        assertEquals("98765432104", artistaEncontrado.getCpf());
        assertEquals("João da Silva", artistaEncontrado.getNomeCompleto());
        assertEquals(TipoGenero.MASCULINO, artistaEncontrado.getGenero());

        em2.getTransaction().begin();
        em2.remove(artistaEncontrado);
        em2.getTransaction().commit();

        em2.close();
    }
}