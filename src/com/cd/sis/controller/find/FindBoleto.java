/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller.find;

import com.cd.sis.controller.FindEntity;
import com.cd.sis.bean.*;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Lettiery
 */
public class FindBoleto extends FindEntity{
    
    public static List<Boleto> boletosDoMorador(Morador c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.idMorador = :mor ",
                        Boleto.class);
        q.setParameter("mor", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosNaoPagoDoMorador(Morador c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.idMorador = :mor  and o.pago = false",
                        Boleto.class);
        q.setParameter("mor", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosDoCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.condominio = :cond ",
                        Boleto.class);
        q.setParameter("cond", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosNaoPagosDoCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.condominio = :cond and o.pago = false",
                        Boleto.class);
        q.setParameter("cond", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosMesAtualCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.condominio = :cond "
                       + "and day(o.mesReferencia) = day(curdate()) ",
                        Boleto.class);
        q.setParameter("cond", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosNaoPagosMesAtualCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.condominio = :cond and "
                                + " o.pago = false"
                                + " day(o.mesReferencia) = day(curdate()) and o.mesReferencia >= curdate()",
                        Boleto.class);
        q.setParameter("cond", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static Morador moradoreComNome(String nome){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Morador as o where o.nome = :nome ",
                        Morador.class);
        q.setParameter("nome", nome);
        Morador m = (Morador) q.getSingleResult();
        return m;
    }
    
    
}
