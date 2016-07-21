/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.exception;

import com.cd.sis.bean.Boleto;
import java.util.List;

/**
 *
 * @author Lettiery
 */
public class BoletoJaSalvo extends Exception {

    private Boleto b;
    private List<Boleto> boletos;
    
    /**
     * Creates a new instance of <code>BoletoJaSalvo</code> without detail
     * message.
     */
    public BoletoJaSalvo(List<Boleto> boletos ) {
         super(boletos.size()+"Boletos já Salvos");
        this.boletos = boletos;
    }

    public BoletoJaSalvo(String msg) {
        super(msg);
    }
    
    public BoletoJaSalvo(Boleto b){
        super("Boleto"+b+" já Salvo");
        this.b = b;
    }

    public Boleto getBoleto() {
        return b;
    }

    public void setBoleto(Boleto b) {
        this.b = b;
    }

    public List<Boleto> getBoletos() {
        return boletos;
    }

    public void setBoletos(List<Boleto> boletos) {
        this.boletos = boletos;
    }
    
    
}
