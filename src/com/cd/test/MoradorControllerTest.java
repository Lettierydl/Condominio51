/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.test;

import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.CondominioController;
import com.cd.sis.controller.MoradorController;
import javax.persistence.EntityNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Lettiery
 */
public class MoradorControllerTest {
    MoradorController cm;
    CondominioController cc;
    
    public MoradorControllerTest() {
    }
    
    @Before
    public void setUp() {
        cm = new MoradorController();
        cm.removeAll();
        
        cc = new CondominioController();
        cc.removeAll();
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
     * Test of create method, of class MoradorController.
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("Create");
        Morador m = iniciarMorador();
        Condominio c = iniciarCondominio();
        cc.create(c);
        
        cm.create(m);
        assertEquals(m, cm.getReference(m));
       
    }

    /**
     * Test of edit method, of class MoradorController.
     */
    @Test
    public void testEdit() throws Exception {
        System.out.println("Edit");
        Morador m = iniciarMorador();
        
        Condominio c = iniciarCondominio();
        cc.create(c);
        m.setCondominio(c);
        
        cm.create(m);
        
       
        m.setNome("Nome2");
        m.setTelefone("000000");
        
        cm.edit(m);
        
        Morador m2 = cm.getReference(m);
        assertEquals(m, m2);
        assertEquals("Nome2", m2.getNome());
        assertEquals("000000", m2.getTelefone()); 
    }
    
    /**
     * Test of edit method, of class MoradorController.
     */
    @Test
    public void testEditCondominio() throws Exception {
        System.out.println("Edit_Condominio");
        Morador m = iniciarMorador();
        
        Condominio c = iniciarCondominio();
        cc.create(c);
        m.setCondominio(c);
        
        cm.create(m);
        
        Condominio c2 = iniciarCondominio();
        cc.create(c2);
        
        m.setCondominio(c2);
        
        cm.edit(m);
        
        assertEquals(m, cm.getReference(m));
        assertEquals(m.getCondominio(), cm.getReference(m).getCondominio());
    }

    /**
     * Test of destroy method, of class MoradorController.
     */
    @Test(expected = EntityNotFoundException.class)
    public void testDestroy() throws Exception {
        System.out.println("Destroy");
        Morador m = iniciarMorador();
        Condominio c = iniciarCondominio();
        cc.create(c);
        
        cm.create(m);
        
        cm.destroy(m);
        cm.getReference(m);
    }

    /**
     * Test of getReference method, of class MoradorController.
     */
    @Test
    public void testGetReference() throws Exception {
        System.out.println("GetReference");
        Morador m = iniciarMorador();
        Condominio c = iniciarCondominio();
        cc.create(c);
        
        cm.create(m);
        assertEquals(m, cm.getReference(m));
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void testGetReferenceException() throws Exception {
        System.out.println("GetReference_Exception");
        Morador m = iniciarMorador();
        Condominio c = iniciarCondominio();
        cc.create(c);
        
        cm.create(m);
        cm.destroy(m);
        cm.getReference(m);
    }

    
}
