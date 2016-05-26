/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.sis.bean.Morador;
import static com.cd.sis.controller.ControllerEntity.em;
import java.util.List;
import javax.persistence.EntityNotFoundException;

/**
 *
 * @author Lettiery
 */
public class MoradorController extends ControllerEntity<Morador>{

    @Override
    public void create(Morador entity) throws Exception {
        try {
            beginTransaction();
            em.persist(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Morador entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void destroy(Morador entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                getReference(entity);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Morador com código " + entity.getId()
                        + " não existe.");
            }
            em.remove(getReference(entity));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    
    @Override
    public Morador getReference(Morador entity) {
        em = getEntityManager();
        return em.getReference(Morador.class, entity.getId());
    }

    @Override
    public void removeAll() {
        try {
            beginTransaction();
            List <Morador>resultList = em.createNativeQuery("SELECT * FROM morador;", Morador.class).getResultList();
            for(Morador m : resultList){
                em.remove(m);
            }
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }
    
}
