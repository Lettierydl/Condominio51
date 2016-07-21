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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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
public class EmailsBoletosController extends DialogController<List<Boleto>> {

    private List<Boleto> naoEnviados = new ArrayList<>();
    private File balancete = null;

    @FXML
    public ComboBox<String> assunto_box;
    @FXML
    public TextArea email;

    @FXML
    TableView<Boleto> table;
    @FXML
    private TableColumn colMes;
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
        colMes.setCellValueFactory(new PropertyValueFactory<>("mesReferencia"));
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("morador"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("morador"));
        colMorador.setCellValueFactory(new PropertyValueFactory<>("morador"));

        JavaFXUtil.colunDataMesFormatDate(colMes);
        JavaFXUtil.colunUnidadeMoradorFormat(colUnidade);
        JavaFXUtil.colunEmailMoradorFormat(colEmail);
        JavaFXUtil.colunMoradorFormat(colMorador);
        configurarColunaNaoEnviar();

        assunto_box.setItems(FXCollections.observableArrayList(
                EmailController.ASSUNTO_BOLETO_MES_ATUAL,
                EmailController.ASSUNTO_COBRANCA_BOLETO));
        assunto_box.setValue(EmailController.ASSUNTO_BOLETO_MES_ATUAL);
        modificarTextoEmail();
    }

    @FXML
    public void modificarTextoEmail() {
        this.email.setText(f.getMenssagemEmail(assunto_box.getValue()));
    }

    @FXML
    public void anexarBalancete() {
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

        balancete = fileChooser.showOpenDialog(email.getScene().getWindow());
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
                        List<Boleto> falha = enviarEmalis();
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

    private List<Boleto> enviarEmalis() {
        List<Boleto> falha = new ArrayList<>();
        switch(assunto_box.getValue()){
            case EmailController.ASSUNTO_BOLETO_MES_ATUAL:
                int cont = 0;
                for (Boleto b : entity) {
                    if (naoEnviados.contains(b)) {
                        continue;
                    }
                    try {
                        f.enviarEmailCobranca(b, balancete);
                        ++cont;
                        System.out.println(100 * (cont / entity.size()) + "%");
                    } catch (EmailException e) {
                        falha.add(b);
                    }
                }
                break;
            case EmailController.ASSUNTO_COBRANCA_BOLETO:
                Map<Morador, List<Boleto> > map = GeradorRelatorio.separarPorMorador(entity);
                for (Morador m : map.keySet()) {
                    List<Boleto> bls = map.get(m);
                    if (!naoEnviados.isEmpty()) {
                        for(Boleto b: naoEnviados){
                            try{
                                bls.remove(b);
                            }catch(Exception e){}
                        }
                    }
                    try {
                        f.enviarEmailBoletoVencido(bls);
                        System.out.println(m);
                    } catch (EmailException e) {
                        if(!bls.isEmpty()){
                            falha.add(bls.get(0));
                        }
                    }
                }
                break;
        }
        return falha;
    }

    @Override
    public boolean isEntradaValida() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEntity(List<Boleto> entity) {
        this.entity = entity;
        atualizarTable();
    }

    @Override
    public String getTitulo() {
        return "Enviar Boletos";
    }

    private void atualizarTable() {
        this.table.setItems(FXCollections.observableArrayList(entity));
    }

    @FXML
    protected void configurarColunaNaoEnviar() {
        colNaoEnviar.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Boleto, Boleto>, ObservableValue<Boleto>>() {
            @Override
            public ObservableValue<Boleto> call(TableColumn.CellDataFeatures<Boleto, Boleto> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colNaoEnviar.setCellFactory(new Callback<TableColumn<Boleto, Boleto>, TableCell<Boleto, Boleto>>() {
            @Override
            public TableCell<Boleto, Boleto> call(TableColumn<Boleto, Boleto> btnCol) {
                return new TableCell<Boleto, Boleto>() {
                    @Override
                    public void updateItem(final Boleto obj, boolean empty) {
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
                                    //    entity.remove(obj);
                                } else {
                                    naoEnviados.remove(obj);
                                    //    entity.add(obj);
                                }
                            }
                        });
                    }
                };
            }
        });
    }
}
