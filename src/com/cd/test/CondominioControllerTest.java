/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.test;

import com.cd.sis.bean.*;
import com.cd.sis.controller.*;
import javax.persistence.EntityNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lettiery
 */
public class CondominioControllerTest {
    
    CondominioController cc;
    MoradorController mc;
    public CondominioControllerTest() {
    }
    
    @Before
    public void setUp() {
        cc = new CondominioController();
        cc.removeAll();
        mc = new MoradorController();
        mc.removeAll();
    }
    
    @After
    public void tearDown() {
    }
    
    public Morador iniciarMorador(){
        Morador m = new Morador();
        m.setCpf("000.000.000-00");
        m.setEmail("email@email.com");
        m.setNome("nome");
        m.setTelefone("(83)9999-9999");
        m.setUnidade("01");
        return m;
    }
    
    public Condominio iniciarCondominio(){
        Condominio c = new Condominio();
        c.setAgencia("1617-9");
        c.setCnpj("87.276.962/0001-56");
        c.setBanco("BB");
        c.setConta("40719-4");
        c.setEndereco("Rua Eugênio Carneiro Monteiro, N 51");
        c.setNome("Sorento");
        c.setTaxaCondominial(200);
        c.setUnidades(20);
        //c.setSindico(iniciarMorador());
        return c;
    }
    
    
    
    /**
     * Test of create method, of class CondominioController.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("Create");
        Condominio c = iniciarCondominio();
        Morador sin = iniciarMorador();
        mc.create(sin);
        c.setSindico(sin);
        cc.create(c);
        assertEquals(c, cc.getReference(c));
    }

    /**
     * Test of edit method, of class CondominioController.
     */
    @Test
    public void testEdit() throws Exception {
        System.out.println("Edit");
        Condominio c = iniciarCondominio();
        Morador sin = iniciarMorador();
        mc.create(sin);
        c.setSindico(sin);
        cc.create(c);
       
        c.setNome("Nome2");
        c.setTaxaCondominial(300);
        
        cc.edit(c);
        
        Condominio c2 = cc.getReference(c);
        assertEquals(c, c2);
        assertEquals("Nome2", c2.getNome());
        assertEquals(300, c2.getTaxaCondominial(), 0);   
    }
    
    /**
     * Test of edit method, of class CondominioController.
     */
    @Test
    public void testEditSindico() throws Exception {
        System.out.println("Edit_Sindico");
        Condominio c = iniciarCondominio();
        Morador sin = iniciarMorador();
        mc.create(sin);
        c.setSindico(sin);
        cc.create(c);
        
        
        Morador sin2 = iniciarMorador();
        sin2.setNome("Sindico2");
        mc.create(sin2);
        c.setSindico(sin2);
        
        cc.edit(c);
        
        Condominio c2 = cc.getReference(c);
        assertEquals(c, c2);
        assertEquals(sin2, c2.getSindico());   
    }

    /**
     * Test of destroy method, of class CondominioController.
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDestroy() throws Exception {
        System.out.println("Destroy");
        Condominio c = iniciarCondominio();
        Morador sin = iniciarMorador();
        mc.create(sin);
        c.setSindico(sin);
        cc.create(c);
        
        cc.destroy(c);
        cc.getReference(c);
    }

    /**
     * Test of getReference method, of class CondominioController.
     */
    @Test
    public void testGetReference() throws Exception {
        System.out.println("GetReference");
        
        Condominio c = iniciarCondominio();
        Morador sin = iniciarMorador();
        mc.create(sin);
        c.setSindico(sin);
        cc.create(c);
        
        assertEquals(cc.getReference(c), c);
        
        
        c.setNome("Nome2");
        c.setTaxaCondominial(300);
        
        cc.edit(c);
        
        assertEquals(c, cc.getReference(c));
        assertEquals("Nome2", cc.getReference(c).getNome());
        assertEquals(300, cc.getReference(c).getTaxaCondominial(), 0);
        
    }
    
    /**
     * Test of getReference method, of class CondominioController.
     */
    @Test(expected = EntityNotFoundException.class)
    public void testGetReferenceException() throws Exception {
        System.out.println("GetReference_Exception");
        
        Condominio c = iniciarCondominio();
        Morador sin = iniciarMorador();
        mc.create(sin);
        c.setSindico(sin);
        cc.create(c);
        
        cc.destroy(c);
        cc.getReference(c);
    }
    
}
