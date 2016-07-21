/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller;

import com.cd.Facede;
import com.cd.sis.bean.Condominio;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class ConfiguracoesController implements Initializable {

    private Facede f;

    @FXML
    private ComboBox<Condominio> box_condominio;
   
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        f = Facede.getInstance();

        List<Condominio> li = f.buscarCondominios();
        box_condominio.setItems(FXCollections.observableArrayList(li));

        condiminioSelecionado(null);
    }

    @FXML
    public void condiminioSelecionado(ActionEvent event) {
        Condominio c = box_condominio.getValue();
        if (c == null) {
        //    tabPane.setVisible(false);
            return;
        }
    }

}
