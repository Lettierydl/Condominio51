/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.sis.controller;

import com.cd.sis.controller.ControllerEntity;
import javax.persistence.EntityManager;

/**
 *
 * @author Lettiery
 */
public abstract class FindEntity {
    
    
    protected static EntityManager getEntityManager(){
        return ControllerEntity.getEntityManager();
    }
}
