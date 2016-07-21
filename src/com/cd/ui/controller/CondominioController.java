/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller;

import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.find.FindCondominio;
import com.cd.ui.controller.dialog.DialogController;
import com.cd.util.JavaFXUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Lettiery
 */
public class CondominioController extends ControllerUI<Condominio> {

    @FXML
    private TableColumn colunaTaxa;
    @FXML
    private TableColumn colunaUnid;
    @FXML
    private TableColumn colunaSindico;
    @FXML
    private TableColumn colunaCnpj;
    @FXML
    private TableColumn colunaNome;

    @FXML
    @Override
    protected void preencherTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colunaSindico.setCellValueFactory(new PropertyValueFactory<>("sindico"));
        colunaUnid.setCellValueFactory(new PropertyValueFactory<>("unidades"));
        colunaTaxa.setCellValueFactory(new PropertyValueFactory<>("taxaCondominial"));

        JavaFXUtil.colunMoradorFormat(colunaSindico);
        JavaFXUtil.colunValueMoedaFormat(colunaTaxa);
        JavaFXUtil.colunMoradorFormat(colunaSindico);

        atualizarLista();
    }

    @Override
    protected void atualizarLista() {
        try {
            String cond = this.pesquisa.getText();
            observavel = FindCondominio.condominiosComNomeQueInicia(cond);
            tabela.getItems().clear();
            tabela.setItems(FXCollections.observableList(observavel));
            configurarColunaEditar();
        } catch (Exception e) {
        }
    }

    @Override
    protected void digitado(KeyEvent event) {
        String text = pesquisa.getText();
        atualizarLista();
        if (observavel.size() == 1 && event.getCode().isLetterKey()) {
            int i = text.length();
            pesquisa.setText(observavel.get(0).getNome());
            pesquisa.selectRange(i, observavel.get(0).getNome().length());
        }
    }

    @Override
    protected void abrirModalCreate() {
        super.showDialog("dialogCondominio", editar, DialogController.CREATE_MODAL);
    }
    
    
    @Override
    protected void abrirModalEdit() {
        super.showDialog("dialogCondominio", editar, DialogController.EDIT_MODAL);
    }
}
