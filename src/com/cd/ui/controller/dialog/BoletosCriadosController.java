/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller.dialog;

import com.cd.exception.BoletoJaSalvo;
import com.cd.sis.bean.Boleto;
import com.cd.util.JavaFXUtil;
import com.cd.util.OperacaoStringUtil;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class BoletosCriadosController extends DialogController<List<Boleto>> {

    @FXML
    TableView<Boleto> table;

    @FXML
    private TableColumn colMes;
    @FXML
    private TableColumn colTaxa;
    @FXML
    private TableColumn colUnidade;
    @FXML
    private TableColumn colArquivo;
    @FXML
    private TableColumn colMorador;
    @FXML
    private TableColumn colExcuir;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colMes.setCellValueFactory(new PropertyValueFactory<>("mesReferencia"));
        colTaxa.setCellValueFactory(new PropertyValueFactory<>("taxa"));
        colUnidade.setCellValueFactory(new PropertyValueFactory<>("morador"));
        colArquivo.setCellValueFactory(new PropertyValueFactory<>("nomeAquivo"));
        colMorador.setCellValueFactory(new PropertyValueFactory<>("morador"));

        JavaFXUtil.colunValueMoedaFormat(colTaxa);
        JavaFXUtil.colunDataMesFormatDate(colMes);
        JavaFXUtil.colunUnidadeMoradorFormat(colUnidade);
        JavaFXUtil.colunMoradorFormat(colMorador);
        configurarColunaExcluir();
    }

    @Override
    protected void okClicado() {
        try {
            int qt = f.salvarBoletos(entity);
            int restantes = entity.get(0).getCondominio().getUnidadesPagaveis() - qt;
            String ms = "Um boleto salvo";
            if (restantes > 0) {
                ms ="Boletos salvos: " + OperacaoStringUtil.formatarStringQuantidadeInteger(qt);
                ms += "Faltam " + restantes + " Boletos a serem criados";
            }
            Dialogs di = Dialogs.create().title((entity.size()>1 ?"Boletos Salvos": "Boleto Salvo"))
                    .masthead( (entity.size()>1 ? " Boletos salvos": " Boleto salvo") + " com sucesso!")
                    .message(ms);
            di.actions(Actions.YES);
            di.style(DialogStyle.UNDECORATED);
            di.showInformation();
        } catch (BoletoJaSalvo ex2) {
            Dialogs di = Dialogs.create().title("Boleto Já Registrado");
            String ms = "Um Boleto já estava Registrado\n";
            if (ex2.getBoletos().size() > 1) {
                ms = ex2.getBoletos().size() + " Boletos já estavam Registrados\n";
            }
            di.masthead(ms);
            if (ex2.getBoletos().size() > 1) {
                ms = "Unidades:";
                for (Boleto b : ex2.getBoletos()) {
                    ms += " (" + b.getMorador().getUnidade() + ") ";
                }
            }else{
                ms = "Unidade ("+ex2.getBoletos().get(0).getMorador().getUnidade()+")";  
            }
            di.message(ms);
            
            di.showWarning();
        } catch (Exception ex) {
            Dialogs.create().title("Erro ao Salvar Boletos")
                    .showException(ex);
        }
        dialogStage.close();
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
        return "Boletos Criados";
    }

    private void atualizarTable() {
        this.table.setItems(FXCollections.observableArrayList(entity));
    }

    @FXML
    protected void configurarColunaExcluir() {
        colExcuir.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Boleto, Boleto>, ObservableValue<Boleto>>() {
            @Override
            public ObservableValue<Boleto> call(TableColumn.CellDataFeatures<Boleto, Boleto> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        colExcuir.setCellFactory(new Callback<TableColumn<Boleto, Boleto>, TableCell<Boleto, Boleto>>() {
            @Override
            public TableCell<Boleto, Boleto> call(TableColumn<Boleto, Boleto> btnCol) {
                return new TableCell<Boleto, Boleto>() {
                    @Override
                    public void updateItem(final Boleto obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final Button button = new Button("Excluir");
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Dialogs di = Dialogs.create().title("Excluir Boleto")
                                        .masthead("Deseja Excluir Boleto Gerado?")
                                        .message("Mês de Referência: " + OperacaoStringUtil.formatDataMesValor(obj.getMesReferencia())
                                                + "\nTaxa: " + OperacaoStringUtil.formatarStringValorMoeda(obj.getTaxa())
                                                + "\nUnidade: " + obj.getMorador().getUnidade()
                                                + "\nArquivo: " + obj.getNomeAquivo());
                                di.actions(Actions.YES, Actions.CANCEL);
                                di.style(DialogStyle.UNDECORATED);
                                Action conf = di.showConfirm();
                                if (conf == Actions.YES) {
                                    entity.remove(obj);
                                    atualizarTable();
                                }
                            }
                        });
                    }
                };
            }
        });
    }
}
