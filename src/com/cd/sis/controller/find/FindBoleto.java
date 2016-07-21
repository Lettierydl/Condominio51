/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller.find;

import com.cd.sis.controller.FindEntity;
import com.cd.sis.bean.*;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Lettiery
 */
public class FindBoleto extends FindEntity{
    
    public static Boleto getBoleto(Date mesReferencia, Morador c){
        mesReferencia.setDate(1);
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.idMorador = :mor "
                      + "and o.mesReferencia = :di ",
                        Boleto.class);
        q.setParameter("mor", c);
        q.setParameter("di", mesReferencia);
        
        Boleto boleto = (Boleto) q.getSingleResult();
        
        return boleto;
    }
    
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
    
    public static List<Boleto> boletosNaoPagosDoMorador(Morador m){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.idMorador = :mor and o.pago = false",
                        Boleto.class);
        q.setParameter("mor", m);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosPagosDoCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.condominio = :cond and o.pago = true",
                        Boleto.class);
        q.setParameter("cond", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static List<Boleto> boletosPagosDoMorador(Morador m){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.idMorador = :mor and o.pago = true",
                        Boleto.class);
        q.setParameter("mor", m);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    
    public static List<Boleto> boletosMesAtualCondominio(Condominio c){
        Query q = getEntityManager()
                .createNativeQuery(
                        "select * from boleto as o where " +
                        "month(o.mes_referencia) = month(curdate()) and " +
                        "YEAR(o.mes_referencia) = YEAR(curdate()) and "
                        + "o.id_condominio = ? "
                        + "order by o.mes_referencia",
                        Boleto.class);
        q.setParameter(1, c.getId());
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    public static Boleto boletoMesAtualMorador(Morador mor){
        Query q = getEntityManager()
                .createNativeQuery(
                        "select * from boleto as o where " +
                        "month(o.mes_referencia) = month(curdate()) and " +
                        "YEAR(o.mes_referencia) = YEAR(curdate()) and "
                        + "o.id_morador = ? "
                        + "order by o.mes_referencia",
                        Boleto.class);
        q.setParameter(1, mor.getId());
        Boleto boleto = (Boleto) q.getSingleResult();
        return boleto;
    }
    
    public static List<Boleto> boletosNaoPagosMesAtualCondominio(Condominio c){
        Query q = getEntityManager()
                .createQuery(
                        "select o from Boleto as o where o.condominio = :cond and "
                                + " o.pago = false"
                                + " month(o.mesReferencia) = month(curdate()) and "
                                + " YEAR(o.mesReferencia) = YEAR(curdate()) and "
                                + " o.mesReferencia >= curdate()",
                        Boleto.class);
        q.setParameter("cond", c);
        List<Boleto> boletos = q.getResultList();
        return boletos;
    }
    
    
}
