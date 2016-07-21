/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller;

import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.find.FindMorador;
import com.cd.ui.controller.dialog.DialogController;
import com.cd.ui.controller.dialog.MoradorDialogController;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Lettiery
 */
public class MoradorController extends ControllerUI<Morador> {
    
    @FXML
    private ComboBox<Condominio> condominio_box;
    
    @FXML
    private TableColumn colunaUnidade;
    @FXML
    private TableColumn colunaTelefone;
    @FXML
    private TableColumn colunaEmail;
    @FXML
    private TableColumn colunaCpf;
    @FXML
    private TableColumn colunaNome;
   
    @FXML
    @Override
    protected void preencherTabela() {
        List<Condominio> li = f.buscarCondominios();
        condominio_box.setItems(FXCollections.observableArrayList(li));
        try{
            condominio_box.setValue(li.get(0));
        }catch(Exception e){}//lista vazia
        
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colunaUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));

        atualizarLista();
    }

    @FXML
    @Override
    protected void atualizarLista() {
        try {
            String nome = this.pesquisa.getText();
            tabela.getItems().clear();
            observavel = FindMorador.moradoreComNomeOuUnidadeQueInicia(nome, condominio_box.getValue());
            tabela.setItems(FXCollections.observableList(observavel));
            configurarColunaEditar();
        } catch (Exception e) {
        }
    }
    
    @FXML
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
    
    @FXML
    @Override
    protected void abrirModalCreate() {
        MoradorDialogController.setCondominio(condominio_box.getValue());
        super.showDialog("dialogMorador", editar, DialogController.CREATE_MODAL);
    }
    
    @FXML
    @Override
    protected void abrirModalEdit() {
        MoradorDialogController.setCondominio(condominio_box.getValue());
        super.showDialog("dialogMorador", editar, DialogController.EDIT_MODAL);
    }
}
