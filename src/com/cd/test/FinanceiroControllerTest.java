/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.test;

import com.cd.sis.controller.find.FindMorador;
import com.cd.sis.bean.*;
import com.cd.sis.controller.*;
import com.cd.sis.controller.find.FindBoleto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lettiery
 */
public class FinanceiroControllerTest {
    
    BoletoController bo;
    MoradorController mc;
    CondominioController cc;
    public FinanceiroControllerTest() {
    }
    
    @Before
    public void setUp() {
        bo = new BoletoController();
        mc = new MoradorController();
        cc = new CondominioController();
        bo.removeAll();
        cc.removeAll();
        mc.removeAll();
    }
    
    @After
    public void tearDown() {
    }
    
    
    public Condominio cadastrarCondominioEMoradores(int unidades) throws Exception{
        Condominio c = new Condominio();
        c.setAgencia("1617-9");
        c.setCnpj("87.276.962/0001-56");
        c.setBanco("BB");
        c.setConta("40719-4");
        c.setEndereco("Rua Eugênio Carneiro Monteiro, N 51");
        c.setNome("Sorento");
        c.setTaxaCondominial(200);
        c.setUnidades(unidades);
        cc.create(c);
        
        for(int i = 1; i <= c.getUnidades();i++){
            Morador m = new Morador();
            m.setCpf("000.000.000-00");
            m.setEmail("email@email.com");
            m.setNome("Morador "+i);
            m.setTelefone("(83)9999-9999");
            m.setUnidade("0"+i);
            m.setCondominio(c);
            mc.create(m);
        }
        Morador m = FindMorador.moradoreComNome("Morador 1");
        m.setNome("Sindico");
        mc.edit(m);
        c.setSindico(m);
        cc.edit(c);
        return cc.getReference(c);
    }
    
    /**
     * Test of criarBoletosMesAtual method, of class FinanceiroController.
     */
    @Test
    public void testCriarBoletosMesAtual() throws Exception {
        System.out.println("criarBoletosMesAtual");
        int unidades = 20;
        Condominio cond = cadastrarCondominioEMoradores(unidades);
        int boletos = bo.criarBoletosMesAtual(cond);
        assertEquals(unidades-1, boletos);
        assertEquals(unidades-1, FindBoleto.boletosDoCondominio(cond).size());
        assertEquals(unidades-1, FindBoleto.boletosNaoPagosDoCondominio(cond).size());   
    }
    
    @Test
    public void testPagarBoleto() throws Exception {
        System.out.println("testPagarBoleto");
        int unidades = 20;
        Condominio cond = cadastrarCondominioEMoradores(unidades);
        bo.criarBoletosMesAtual(cond);
        
        Morador m = FindMorador.moradoreComNome("Morador 2");
        
        Boleto boleto = FindBoleto.boletosNaoPagoDoMorador(m).get(0);
        bo.pagarBoleto(boleto, 2, 2);
        
        assertEquals(0, FindBoleto.boletosNaoPagoDoMorador(m).size());
        assertTrue(bo.getReference(boleto).isPago());
        assertEquals(bo.getReference(boleto).getValorPago(), cond.getTaxaCondominial()+4, 0);
        
    }
    
    @Test
    public void testFidBoletosNaoPagos() throws Exception {
        System.out.println("FidBoletosNaoPagos");
        int unidades = 20;
        Condominio cond = cadastrarCondominioEMoradores(unidades);
        bo.criarBoletosMesAtual(cond);
        
        Morador m = FindMorador.moradoreComNome("Morador 2");
        
        Boleto boleto = FindBoleto.boletosNaoPagoDoMorador(m).get(0);
        
        assertEquals(1, FindBoleto.boletosNaoPagosDoCondominio(cond).size());
        assertEquals(1, FindBoleto.boletosNaoPagosDoCondominio(cond).get(0).getTaxa());
        
    }
    
}
