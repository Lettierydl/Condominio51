/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller.gerador;

import com.cd.sis.bean.Boleto;
import com.cd.sis.bean.Morador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lettiery
 */
public class GeradorRelatorio {
    
    
    
    
    public static Map<Morador, List<Boleto> > separarPorMorador(List<Boleto> boletos){
        Map<Morador, List<Boleto> > m = new HashMap<>();
        for(Boleto b : boletos){
            if(m.get(b.getMorador()) == null){
                m.put(b.getMorador() , new ArrayList<>());
            }
            m.get(b.getMorador()).add(b);
        }
        for(Morador mor : m.keySet()){
            Collections.sort(m.get(mor));
        }
        return m;
    }
    
}
