/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.sis.bean.*;
import com.cd.sis.controller.find.FindBoleto;
import com.cd.sis.controller.find.FindMorador;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityNotFoundException;

/**
 *
 * @author Lettiery
 */
public class BoletoController extends ControllerEntity<Boleto>{
    
    
    public double debitoMorador(Morador m){
        double debito = 0;
        for(Boleto b : FindBoleto.boletosNaoPagoDoMorador(m)){
            debito += b.getTaxa();
        }
        return debito;
    }
    
    public int criarBoletosMesAtual(Condominio cond) throws Exception{
        int criados = 0;
        for(Morador m : FindMorador.moradoresDoCondominio(cond)){
            if(cond.getSindico().equals(m)){// sindico isento
                continue;
            }
            Boleto b = new Boleto();
            b.setMesReferencia(new Date());
            b.setCondominio(cond);
            b.setMorador(m);
            b.setTaxa(cond.getTaxaCondominial());
            b.setPago(false);
            create(b);
            criados++;
        }
        return criados;
    }
    
    public void pagarBoleto(Boleto boleto) throws EntidadeNaoExistenteException {
        pagarBoleto(boleto, 0 ,0);
    }
    
    public void pagarBoleto(Boleto boleto, double juros, double multa) throws EntidadeNaoExistenteException {
        boleto.setPago(true);
        boleto.setDataPago(new Date());
        boleto.setValorPago(boleto.getTaxa() + juros + multa);
        edit(boleto);
    }
    
    
    
    
    @Override
    public void create(Boleto entity) throws Exception {
        try {
            beginTransaction();
            em.persist(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void edit(Boleto entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            em.merge(entity);
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    @Override
    public void destroy(Boleto entity) throws EntidadeNaoExistenteException {
        try {
            beginTransaction();
            try {
                getReference(entity);
            } catch (EntityNotFoundException enfe) {
                throw new EntidadeNaoExistenteException(
                        "O Boleto com código " + entity.getId()
                        + " não existe.");
            }
            em.remove(getReference(entity));
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }

    
    @Override
    public Boleto getReference(Boleto entity) {
        em = getEntityManager();
        return em.getReference(Boleto.class, entity.getId());
    }

    @Override
    public void removeAll() {
        try {
            beginTransaction();
            List <Boleto>resultList = em.createNativeQuery("SELECT * FROM boleto;", Boleto.class).getResultList();
            for(Boleto p : resultList){
                em.remove(p);
            }
            commitTransaction();
        } finally {
            closeEntityManager();
        }
    }
    
}
