/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller;

import com.cd.ControllerTelas;
import com.cd.Facede;
import com.cd.sis.bean.Boleto;
import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.find.FindMorador;
import com.cd.ui.controller.dialog.DialogController;
import com.cd.ui.controller.dialog.EmailsController;
import com.cd.util.Arquivo;
import com.cd.util.JavaFXUtil;
import com.cd.util.OperacaoStringUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class RelatoriosController implements Initializable {

    private Facede f;
    private File criado = null;
    
    @FXML
    private ComboBox<Condominio> box_condominio;
    @FXML
    private TabPane tabPane;
    @FXML
    private ComboBox<Morador> cUnidade;
    @FXML
    private CheckBox cIncluirMoradores;

    @FXML
    void condiminioSelecionado(ActionEvent event) {
        Condominio c = box_condominio.getValue();
        if (c == null) {
            tabPane.setVisible(false);
            return;
        }else{
            tabPane.setVisible(true);
        }
        List<Morador> moradorList = FindMorador.moradoresDoCondominio(c);
        cUnidade.getItems().clear();
        Morador all = new Morador("Todos");
        moradorList.add(all);
        cUnidade.setItems(FXCollections.observableArrayList(moradorList));
        cUnidade.setValue(all);
    }

    @FXML
    public void gerarPDFCadastros(ActionEvent event) {
        String name;
        if (!cIncluirMoradores.isSelected() && cUnidade.getValue().getId() == null && "Todos".equals(cUnidade.getValue().getNome())) {
            name = "Cadastro dos Condôminos do Condomínio_"
                    + box_condominio.getValue().getNome()
                    + ".pdf";
        } else if (cIncluirMoradores.isSelected()) {
            name = "Cadastro do Condomínio "
                    + box_condominio.getValue().getNome()
                    + ".pdf";
        } else {
            name = "Cadastro do Condômino "
                    + cUnidade.getValue().getNome()
                    + ".pdf";
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar PDF");
        //fileChooser.setInitialDirectory(Arquivo.desktop);
        fileChooser.setInitialDirectory(Arquivo.getArquivoPadrao(box_condominio.getValue()));

        fileChooser.setInitialFileName(name);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        try {
            File file = fileChooser.showSaveDialog(box_condominio.getScene().getWindow());
            if(file == null){
                return;
            }
            if (!cIncluirMoradores.isSelected() && cUnidade.getValue().getId() == null && "Todos".equals(cUnidade.getValue().getNome())) {
                //cadastro de todos os moradores
                f.gerarPdfCadastro(FindMorador.moradoresDoCondominio(box_condominio.getValue()), file);
            } else if (cIncluirMoradores.isSelected()) {
                //condominio
                f.gerarPdfCadastro(box_condominio.getValue(), file);
            } else {
                //morador
                f.gerarPdfCadastro(cUnidade.getValue(), file);
            }
            JavaFXUtil.abrirArquivoDoSistema(file);
            criado = file;
        } catch (IOException ex) {
            Logger.getLogger(BoletoController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    public void enviarRelatorioCadastroPorEmail(ActionEvent event) {
        if(criado == null){
            gerarPDFCadastros(event);
        }
        showDialog(FindMorador.moradoresDoCondominio(box_condominio.getValue()), "emails");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        f = Facede.getInstance();

        List<Condominio> li = f.buscarCondominios();
        box_condominio.setItems(FXCollections.observableArrayList(li));
        
        tabPane.setVisible(false);
        
        condiminioSelecionado(null);
    }
    
    public boolean showDialog(List<Morador> entity, String dialog) {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/"+dialog+".fxml"));
            System.err.println("fxml/dialog/"+dialog+".fxml");
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            EmailsController controller = loader.getController(); ;
            
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);
            controller.addAnexoFixo(criado);
            
            dialogStage.setTitle(controller.getTitulo());
            dialogStage.setResizable(false);
            // Mostra a janela e espera até o usuário fechar.
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
