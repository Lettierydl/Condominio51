/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cd.ui.controller;

import com.cd.ControllerTelas;
import com.cd.Facede;
import com.cd.exception.BoletoJaSalvo;
import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.sis.bean.Boleto;
import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.find.FindBoleto;
import com.cd.sis.controller.find.FindMorador;
import com.cd.ui.controller.dialog.DialogController;
import com.cd.util.Arquivo;
import com.cd.util.JavaFXUtil;
import com.cd.util.MaskFieldUtil;
import com.cd.util.OperacaoStringUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class BoletoController implements Initializable {

    private Facede f;

    @FXML
    private ComboBox<Condominio> box_condominio;
    @FXML
    private TabPane tabPane;

    /*Cadastro*/
    @FXML
    private Label cLabelUnidade;
    @FXML
    private CheckBox cSelectDiferenciada;
    @FXML
    private TextField cTaxaPadrao;
    @FXML
    private DatePicker cMesReferencia;
    @FXML
    private ComboBox<Morador> cUnidade;
    @FXML
    private Button btCadastroIndividual;
    @FXML
    private Button btCadastroTodos;

    /*Pagamento*/
    private List<Boleto> boletoAPagar = new ArrayList<>();
    @FXML
    private Label pBoletosSelecionados;
    @FXML
    private ComboBox<Morador> pUnidade;

    @FXML
    private TableView<Boleto> pTableBoletos;
    @FXML
    private TableColumn<?, ?> unidadeColP;
    @FXML
    private TableColumn<?, ?> moradorColP;
    @FXML
    private TableColumn selecionarColP;
    @FXML
    private TableColumn<?, ?> valorColP;
    @FXML
    private TableColumn<?, ?> mesColP;

    /*Status*/
    @FXML
    private ComboBox<Morador> sUnidade;
    @FXML
    private ComboBox<String> sStatus;

    @FXML
    private TableView<Boleto> sTableStatus;
    @FXML
    private TableColumn arquivoColS;
    @FXML
    private TableColumn<?, ?> pagamentoColS;
    @FXML
    private TableColumn<?, ?> moradorColS;
    @FXML
    private TableColumn<?, ?> unidadeColS;
    @FXML
    private TableColumn<?, ?> valorColS;
    @FXML
    private TableColumn<?, ?> mesColS;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        f = Facede.getInstance();

        MaskFieldUtil.monetaryField(cTaxaPadrao);

        List<Condominio> li = f.buscarCondominios();
        box_condominio.setItems(FXCollections.observableArrayList(li));

        cMesReferencia.setValue(LocalDate.now());
        cMesReferencia.setConverter(MaskFieldUtil.converterDataMes());

        selectecDiferenciada(null);
        condiminioSelecionado(null);

        iniciarTabelaPagamentoBoletos();
        iniciarStatusBoletos();
    }

    @FXML
    public void condiminioSelecionado(ActionEvent event) {
        Condominio c = box_condominio.getValue();
        if (c == null) {
            tabPane.setVisible(false);
            return;
        }
        tabPane.setVisible(true);
        cTaxaPadrao.setText(OperacaoStringUtil.formatarStringValorMoeda(c.getTaxaCondominial()));
        List<Morador> moradorList = FindMorador.moradoresDoCondominio(c);
        cUnidade.getItems().clear();
        pUnidade.getItems().clear();
        sUnidade.getItems().clear();

        cUnidade.setItems(FXCollections.observableArrayList(moradorList));
        pUnidade.setItems(FXCollections.observableArrayList(moradorList));
        sUnidade.setItems(FXCollections.observableArrayList(moradorList));
        atualizarPagamentoTabelaBoleto();
        atualizarStatusBoletos();
    }

    @FXML
    public void selectecDiferenciada(ActionEvent event) {
        if (cSelectDiferenciada.isSelected()) {
            cUnidade.setVisible(true);
            cLabelUnidade.setVisible(true);
            btCadastroIndividual.setVisible(true);
        } else {
            cUnidade.setVisible(false);
            cLabelUnidade.setVisible(false);
            btCadastroIndividual.setVisible(false);
        }
    }

    @FXML
    public void cadastrarTodosBoletos(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivos");
        //fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setInitialDirectory(Arquivo.getArquivoPadrao(box_condominio.getValue()));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDFs", "*.pdf")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(box_condominio.getScene().getWindow());
        if (files == null || files.isEmpty()) {
            return;
        } else if (files.size() < box_condominio.getValue().getUnidadesPagaveis()) {
            Dialogs.create().title("Arquivos Insuficientes")
                    .masthead("Apenas " + files.size() + " arquivos foram encontrados!")
                    .message("Existem " + box_condominio.getValue().getUnidadesPagaveis()
                            + " unidades pagáveis no condomínio "
                            + box_condominio.getValue().getNome()
                            + "\nPor favor, selecione a quantidade certa de boletos")
                    .showError();
            cadastrarTodosBoletos(null);
            return;
        }/* else if (files.size() > box_condominio.getValue().getUnidadesPagaveis()) {
            Dialogs.create().title("Condôminos não cadastrados")
                    .masthead(files.size() + " arquivos foram encontrados!")
                    .message("Existem " + box_condominio.getValue().getUnidadesPagaveis()
                            + " unidades pagáveis no condomínio "
                            + box_condominio.getValue().getNome()
                            + "\nPor favor, selecione a quantidade certa de boletos")
                    .showInformation();
            cadastrarTodosBoletos(null);
            return;
        }*/
        List<Boleto> boletos;
        try {
            boletos = f.gerarBoletos(
                    box_condominio.getValue(),
                    files,
                    OperacaoStringUtil.converterStringValor(cTaxaPadrao.getText()),
                    JavaFXUtil.toDate(cMesReferencia).getTime());
        } catch (IOException ex) {
            Dialogs.create().title("Arquivos Bloqueados")
                    .masthead("Não foi possível ler os arquivos selecionados")
                    .message("Por favor verifique a causa do bloqueio do aquivo!")
                    .showError();
            return;
        }
        showDialog(boletos, "boletosCriados");
    }

    @FXML
    public void cadastroIndividual(ActionEvent event) {
        if (cUnidade.getValue() == null) {
            Dialogs.create().title("Seleção Necessária")
                    .masthead("Por favor Selecione a unidade para o cadastro")
                    .showWarning();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Arquivo");
        fileChooser.setInitialDirectory(Arquivo.getArquivoPadrao(box_condominio.getValue()));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(box_condominio.getScene().getWindow());
        Boleto bol;
        try {
            bol = f.criarBoleto(box_condominio.getValue(), cUnidade.getValue(),
                    OperacaoStringUtil.converterStringValor(cTaxaPadrao.getText()),
                    JavaFXUtil.toDate(cMesReferencia).getTime(), file);
        } catch (IOException ex) {
            Dialogs.create().title("Arquivos Bloqueados")
                    .masthead("Não foi possível ler os arquivos selecionados")
                    .message("Por favor verifique a causa do bloqueio do aquivo!")
                    .showError();
            return;
        }

        Dialogs di = Dialogs.create().title("Salvar Boleto")
                .masthead("Deseja Realmente Salvar o Boleto?")
                .message("Mês de Referência: " + OperacaoStringUtil.formatDataMesValor(bol.getMesReferencia())
                        + "Taxa: " + OperacaoStringUtil.formatarStringValorMoeda(bol.getTaxa())
                        + "Arquivo: " + bol.getNomeAquivo()
                        + "Unidade: " + bol.getMorador()
                );
        di.actions(Dialog.Actions.YES, Dialog.Actions.CANCEL);
        di.style(DialogStyle.UNDECORATED);
        if (di.showConfirm() == Actions.YES) {
            try {
                f.salvarBoleto(bol);
                Dialogs.create().title("Boleto Salvo")
                        .masthead("Boleto Salvo com Sucesso")
                        .message("Mês de Referência: " + OperacaoStringUtil.formatDataMesValor(bol.getMesReferencia())
                                + "Taxa: " + OperacaoStringUtil.formatarStringValorMoeda(bol.getTaxa())
                                + "Arquivo: " + bol.getNomeAquivo()
                                + "Unidade: " + bol.getMorador()
                        ).showInformation();
            } catch (BoletoJaSalvo ex2) {
                Dialogs di2 = Dialogs.create().title("Boleto Já Registrado");
                String ms = "Boleto já estava Registrado com essas configurações\n";
                di2.masthead(ms);
                ms = "Mês de Referência: " + OperacaoStringUtil.formatDataMesValor(bol.getMesReferencia())
                        + "Taxa: " + OperacaoStringUtil.formatarStringValorMoeda(bol.getTaxa())
                        + "Arquivo: " + bol.getNomeAquivo()
                        + "Unidade: " + bol.getMorador();
                di2.message(ms);
                di2.showWarning();
            } catch (Exception ex) {
                Logger.getLogger(BoletoController.class.getName()).log(Level.SEVERE, null, ex);
                Dialogs.create().showException(ex);
            }

        }
    }

    @FXML
    public void pagarBoletosSelecionados() {
        if (boletoAPagar.isEmpty()) {
            return;
        }

        String msg = "";
        for (Boleto b : boletoAPagar) {
            msg += "(" + b.getMorador()
                    + " " + OperacaoStringUtil.formatDataMesValor(b.getMesReferencia());
            msg += " - " + OperacaoStringUtil.formatarStringValorMoeda(b.getTaxa()) + ")\n";

        }
        Dialogs di = Dialogs.create().title("Pagamento de Boleto")
                .masthead("Deseja Realmente Pagar " + (boletoAPagar.size() > 1 ? "os Boletos?" : "o Boleto?"))
                .message(msg);
        di.actions(Dialog.Actions.YES, Dialog.Actions.CANCEL);
        di.style(DialogStyle.UNDECORATED);
        if (di.showConfirm() == Actions.YES) {
            try {
                double valPg = f.pagarBoletos(boletoAPagar);
                Dialogs.create().title("Pagamento de Boleto")
                        .masthead(boletoAPagar.size() + (boletoAPagar.size() > 1 ? " Boletos Pagos" : " Boleto Pago") + " com Sucesso!")
                        .message("Total Pago: " + OperacaoStringUtil.formatarStringValorMoeda(valPg))
                        .showInformation();
            } catch (EntidadeNaoExistenteException ex) {
                Dialogs.create().showException(ex);
                return;
            }
        }
        boletoAPagar.clear();
        iniciarTabelaPagamentoBoletos();
    }

    public boolean showDialog(List<Boleto> entity, String dialog) {
        try {
            // Carrega o arquivo fxml e cria um novo stage para a janela popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/dialog/"+dialog+".fxml"));
            GridPane page = (GridPane) loader.load();

            // Cria o palco dialogStage.
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ControllerTelas.stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Define a pessoa no controller.
            DialogController<List<Boleto>> controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEntity(entity);
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

    @FXML
    private void iniciarTabelaPagamentoBoletos() {
        moradorColP.setCellValueFactory(new PropertyValueFactory<>("morador"));
        unidadeColP.setCellValueFactory(new PropertyValueFactory<>("morador"));
        valorColP.setCellValueFactory(new PropertyValueFactory<>("taxa"));
        mesColP.setCellValueFactory(new PropertyValueFactory<>("mesReferencia"));

        JavaFXUtil.colunValueMoedaFormat(valorColP);
        JavaFXUtil.colunDataMesFormatDate(mesColP);
        JavaFXUtil.colunUnidadeMoradorFormat(unidadeColP);
        JavaFXUtil.colunMoradorFormat(moradorColP);
        configurarColunaSelecionarPagamento();
        atualizarPagamentoTabelaBoleto();
    }

    @FXML
    public void atualizarPagamentoTabelaBoleto() {
        try {
            pTableBoletos.getItems().clear();
            List<Boleto> bols;
            if (pUnidade.getValue() != null) {
                bols = FindBoleto.boletosNaoPagoDoMorador(pUnidade.getValue());
            } else {
                bols = FindBoleto.boletosNaoPagosDoCondominio(box_condominio.getValue());
            }
            ObservableList<Boleto> list = FXCollections.observableArrayList(bols);
            pTableBoletos.setItems(list);
        } catch (Exception e) {
        }
        pBoletosSelecionados.setText("" + boletoAPagar.size());
    }

    private void configurarColunaSelecionarPagamento() {
        selecionarColP.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Boleto, Boleto>, ObservableValue<Boleto>>() {
            @Override
            public ObservableValue<Boleto> call(TableColumn.CellDataFeatures<Boleto, Boleto> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        selecionarColP.setCellFactory(new Callback<TableColumn<Boleto, Boleto>, TableCell<Boleto, Boleto>>() {
            @Override
            public TableCell<Boleto, Boleto> call(TableColumn<Boleto, Boleto> btnCol) {
                return new TableCell<Boleto, Boleto>() {
                    @Override
                    public void updateItem(final Boleto obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        }
                        final CheckBox button = new CheckBox();
                        button.setSelected(false);
                        super.updateItem(obj, empty);
                        setGraphic(button);
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if (button.isSelected()) {
                                    boletoAPagar.add(obj);
                                } else {
                                    for (Boleto b : boletoAPagar) {
                                        if (b.equals(obj)) {
                                            boletoAPagar.remove(b);
                                            break;
                                        }
                                    }
                                }
                                pBoletosSelecionados.setText("" + boletoAPagar.size());
                            }
                        });
                    }
                };
            }
        });
    }
    
    @FXML
    public void iniciarStatusBoletos() {
        moradorColS.setCellValueFactory(new PropertyValueFactory<>("morador"));
        unidadeColS.setCellValueFactory(new PropertyValueFactory<>("morador"));
        valorColS.setCellValueFactory(new PropertyValueFactory<>("taxa"));
        mesColS.setCellValueFactory(new PropertyValueFactory<>("mesReferencia"));
        pagamentoColS.setCellValueFactory(new PropertyValueFactory<>("dataPago"));
        arquivoColS.setCellValueFactory(new PropertyValueFactory<>("nomeAquivo"));

        JavaFXUtil.colunValueMoedaFormat(valorColS);
        JavaFXUtil.colunDataMesFormatDate(mesColS);
        JavaFXUtil.colunDataFormatDate(pagamentoColS);
        JavaFXUtil.colunUnidadeMoradorFormat(unidadeColS);
        JavaFXUtil.colunMoradorFormat(moradorColS);
        configurarColunaArquivo();
        ObservableList<String> list = FXCollections.observableArrayList("Todos", "Abertos", "Pagos", "Mês Atual");
        sStatus.setItems(list);
        sStatus.setValue("Todos");
        atualizarStatusBoletos();
    }

    @FXML
    public void atualizarStatusBoletos() {
        sTableStatus.getItems().clear();
        configurarColunaArquivo();
        try {
            Condominio c = box_condominio.getValue();
            Morador m = sUnidade.getValue();
            final String status = sStatus.getValue();
            if(status == null){
                return;
            }
            List<Boleto> boletos = new ArrayList<>();
            switch (status) {
                case "Abertos":
                    if (m == null) {
                        boletos = FindBoleto.boletosNaoPagosDoCondominio(c);
                    } else {
                        boletos = FindBoleto.boletosNaoPagosDoMorador(m);
                    }
                    break;
                case "Pagos":
                    if (m == null) {
                        boletos = FindBoleto.boletosPagosDoCondominio(c);
                    } else {
                        boletos = FindBoleto.boletosPagosDoMorador(m);
                    }
                    break;
                case "Mês Atual":
                    if (m == null) {
                        boletos = FindBoleto.boletosMesAtualCondominio(c);
                    } else {
                        Boleto b = FindBoleto.boletoMesAtualMorador(m);
                        boletos.add(b);
                    }
                    break;
                default:// "Todos"
                    if (m == null) {
                        boletos = FindBoleto.boletosDoCondominio(c);
                    } else {
                        boletos = FindBoleto.boletosDoMorador(m);
                    }
                    break;
            }

            ObservableList<Boleto> list = FXCollections.observableArrayList(boletos);
            sTableStatus.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void gerarPDFBoletos() {
        Condominio c = box_condominio.getValue();
        Morador m = sUnidade.getValue();
        final String status = sStatus.getValue();
        String name = "Boletos " + c.getNome()
                +(m == null? "":m)
                +((status == null || status == "Todos")? "":status)
                + ".pdf";

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
            f.gerarPdfBoletos(sTableStatus.getItems().sorted(), c.getTituloDocumento(), name, file);
            JavaFXUtil.abrirArquivoDoSistema(file);
        } catch (IOException ex) {
            Logger.getLogger(BoletoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void enviarBoletosPorEmail(){
        showDialog(sTableStatus.getItems().sorted(), "emailsboletos");
    }
    
    private void configurarColunaArquivo() {
        arquivoColS.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Boleto, Boleto>, ObservableValue<Boleto>>() {
            @Override
            public ObservableValue<Boleto> call(TableColumn.CellDataFeatures<Boleto, Boleto> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });
        arquivoColS.setCellFactory(new Callback<TableColumn<Boleto, Boleto>, TableCell<Boleto, Boleto>>() {
            @Override
            public TableCell<Boleto, Boleto> call(TableColumn<Boleto, Boleto> btnCol) {
                return new TableCell<Boleto, Boleto>() {
                    @Override
                    public void updateItem(final Boleto obj, boolean empty) {
                        if (empty || obj == null) {
                            return;
                        } else {
                            final Hyperlink link = new Hyperlink("Abrir PDF");
                            super.updateItem(obj, empty);
                            setGraphic(link);
                            link.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    FileChooser fileChooser = new FileChooser();
                                    fileChooser.setTitle("Salvar PDF");
                                    //fileChooser.setInitialDirectory(Arquivo.desktop);
                                    fileChooser.setInitialDirectory(Arquivo.getArquivoPadrao(box_condominio.getValue()));

                                    String name = "Boleto_" + obj.getMorador()
                                            + new SimpleDateFormat("dd_MM_yyyy").format(obj.getMesReferencia())
                                            + ".pdf";

                                    fileChooser.setInitialFileName(name);
                                    fileChooser.getExtensionFilters().addAll(
                                            new FileChooser.ExtensionFilter("PDF", "*.pdf")
                                    );
                                    File file = fileChooser.showSaveDialog(box_condominio.getScene().getWindow());

                                    try {
                                        Arquivo.criarPDF(obj.getArquivo(), file);
                                        JavaFXUtil.abrirArquivoDoSistema(file);
                                    } catch (IOException e) {
                                        Dialogs.create().showException(e);
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
    }

}
