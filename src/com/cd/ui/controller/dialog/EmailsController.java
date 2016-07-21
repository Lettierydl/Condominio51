/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller.dialog;

import com.cd.sis.bean.Boleto;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.EmailController;
import com.cd.sis.controller.gerador.GeradorRelatorio;
import com.cd.util.Arquivo;
import com.cd.util.JavaFXUtil;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.commons.mail.EmailException;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class EmailsController extends DialogController<List<Morador>> {

    private List<Morador> naoEnviados = new ArrayList<>();
    private List<File> anexos = null;
    private List<File> anexoFixo = new ArrayList<>();


    @FXML
    public ComboBox<String> assunto_box;
    @FXML
    public TextArea email;
    @FXML
    public ListView<String> listAnexos;

    @FXML
    TableView<Morador> table;
    @FXML
    private TableColumn colUnidade;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colMorador;
    @FXML
    private TableColumn colNaoEnviar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("unidade"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMorador.setCellValueFactory(new PropertyValueFactory<>("nome"));

        configurarColunaNaoEnviar();

        assunto_box.setItems(FXCollections.observableArrayList(
                EmailController.ASSUNTO_RELATORIOS,
                EmailController.ASSUNTO_BOLETO_MES_ATUAL,
                EmailController.ASSUNTO_COBRANCA_BOLETO));
        assunto_box.setValue(EmailController.ASSUNTO_RELATORIOS);
        modificarTextoEmail();
        
        listAnexos.setItems(FXCollections.observableArrayList());
        for(File f: anexoFixo){
            listAnexos.getItems().add(f.getName());
        }
    }

    @FXML
    public void modificarTextoEmail() {
        this.email.setText(f.getMenssagemEmail(assunto_box.getValue()));
    }

    @FXML
    public void anexarArquivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleciona Arquivo");
        try{
            fileChooser.setInitialDirectory(Arquivo.getArquivoPadrao(entity.get(0).getCondominio()));
        }catch(Exception e){
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDFs", "*.pdf")
        );
        
        listAnexos.getItems().clear();
        anexos = fileChooser.showOpenMultipleDialog(email.getScene().getWindow());
        listAnexos.setItems(FXCollections.observableArrayList());
        for(File f: anexoFixo){
            listAnexos.getItems().add(f.getName());
        }
        for(File f: anexos){
            listAnexos.getItems().add(f.getName());
        }
    }

    @Override
    protected void okClicado() {
        try {
            if (entity.size() - naoEnviados.size() <= 0) {
                Dialogs.create().title("Nenhum Boleto selecionado")
                        .masthead("Nenhum Boleto selecionado")
                        .message("Por favor, selecione pelomenos um boleto para o envio")
                        .showInformation();
                return;
            }

            f.setMenssagemEmail(email.getText(), assunto_box.getValue());
            
            ProgressDialogController progress = DialogController.getProgressDialog();
            progress.setTitulo_msg("Enviando Emails");
            progress.setWait_msg("Por favor aguardo os envios dos emails");
            progress.show();
            //progress.start();

            new Thread() {
                @Override
                public void run() {
                    try {
                        List<Morador> falha = enviarEmalis();
                        if (falha.isEmpty()) {
                            progress.concluir("Emails enviados com sucesso\n", (entity.size() - naoEnviados.size()) + " emails enviados com sucesso\n");
                        } else {
                            progress.concluir("Emails enviados com sucesso\n",
                                    falha.size() + " emails não obteram exito no envio\n");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();

        } catch (Exception ex) {
            Dialogs.create().title("Erro ao Salvar Boletos")
                    .showException(ex);
        }
        dialogStage.close();
    }

    private List<Morador> enviarEmalis() {
        List<Morador> falha = null;
        try{
           anexoFixo.addAll(anexos);
        }catch(NullPointerException ne){}
        List<Morador> enviar = new ArrayList<>(entity);
        enviar.removeAll(this.naoEnviados);
        try {
            falha = f.enviarEmails(enviar, anexoFixo, assunto_box.getValue(), this.email.getText());
        } catch (EmailException ex) {
            Logger.getLogger(EmailsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return falha;
    }

    @Override
    public boolean isEntradaValida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void addAnexoFixo(File anexoFixo) {
        if(this.anexoFixo == null){
            this.anexoFixo = new ArrayList<>();
        }
        this.anexoFixo.add(anexoFixo);
        listAnexos.getItems().clear();
        for(File f: this.anexoFixo){
            listAnexos.getItems().add(f.getName());
        }
    }
    
    @Override
    public void setEntity(List<Morador> entity) {
        this.entity = entity;
        atualizarTable();
    }

    @Override
    public String getTitulo() {
        return "Enviar Emails";
    }

    private void atualizarTable() {
        this.table.setItems(FXCollections.observableArrayList(entity));
    }

    @FXML
    protected void configurarColunaNaoEnviar() {
        colNaoEnviar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Morador, Morador>, ObservableValue<Morador>>() {
            @Override
            public ObservableValue<Morador> call(TableColumn.CellDataFeatures<Morador, Morador> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colNaoEnviar.setCellFactory(new Callback<TableColumn<Morador, Morador>, TableCell<Morador, Morador>>() {
            @Override
            public TableCell<Morador, Morador> call(TableColumn<Morador, Morador> btnCol) {
                return new TableCell<Morador, Morador>() {
                    @Override
                    public void updateItem(final Morador obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final ToggleButton button = new ToggleButton("Não Enviar");
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (button.isArmed()) {
                                    naoEnviados.add(obj);
                                    //entity.remove(obj);
                                } else {
                                    naoEnviados.remove(obj);
                                    //entity.add(obj);
                                }
                            }
                        });
                    }
                };
            }
        });
    }
}
