package com.cd.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cd.exception.EntidadeNaoExistenteException;
import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.find.FindCondominio;
import com.cd.sis.controller.find.FindMorador;
import com.cd.util.JavaFXUtil;
import com.cd.util.MaskFieldUtil;
import com.cd.util.OperacaoStringUtil;
import org.controlsfx.dialog.Dialogs;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class CondominioDialogController extends DialogController<Condominio> {

    @FXML
    private TextField nome;
    @FXML
    private TextField cnpj;
    @FXML
    private TextField vencimento;

    @FXML
    private TextField endereco;
    @FXML
    private TextField taxa;

    @FXML
    private ComboBox<Morador> sindico;

    @FXML
    private TextField agencia;
    @FXML
    private TextField conta;
    @FXML
    private ComboBox<String> banco;

    public CondominioDialogController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.upperCase(nome);
        MaskFieldUtil.upperCase(endereco);
        MaskFieldUtil.cnpjField(cnpj);
        MaskFieldUtil.diaField(vencimento);
        MaskFieldUtil.agenciaField(agencia);
        MaskFieldUtil.contaField(conta);
        MaskFieldUtil.monetaryField(taxa);

        JavaFXUtil.nextFielOnAction(nome, cnpj);
        JavaFXUtil.nextFielOnAction(cnpj, vencimento);
        JavaFXUtil.nextFielOnAction(vencimento, endereco);
        JavaFXUtil.nextFielOnAction(endereco, taxa);
        JavaFXUtil.nextFielOnAction(taxa, agencia);
        JavaFXUtil.nextFielOnAction(agencia, conta);
        JavaFXUtil.nextFielOnAction(conta, okButton);

        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });

        banco.setItems(FXCollections.observableArrayList("Banco do Brasil", "Bradesco", "Caixa", "Itaú", "Santander", "Otros"));
        banco.setValue("Caixa");

        if (tipe == DialogController.EDIT_MODAL) {
            sindico.setItems(FXCollections.observableArrayList(FindMorador.moradoresDoCondominio(entity)));
            sindico.setValue(entity.getSindico());
        }
    }

    @Override
    public boolean isEntradaValida() {
        String msgErro = "";
        if (tipe == EDIT_MODAL) {// validar edicao
            try {
                if (nome.getText().isEmpty()) {
                    msgErro += "Nome é obrigatório\n";
                }
                Condominio o;
                try {
                    o = FindCondominio.condominioComNome(nome.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Nome já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }
                if (!(cnpj.getText().replace(".", "").replace("-", "")).isEmpty()) {
                    try {
                        o = FindCondominio.buscarCondominioPorCNPJ(cnpj.getText().replace(".", "").replace("-", ""));
                        if (o.getId() != entity.getId()) {// codigo de outro produto
                            msgErro += "CNPJ já cadastrado\n";
                        }
                    } catch (NonUniqueResultException nu) {
                        msgErro += "CNPJ já cadastrado\n";
                    }
                }
            } catch (Exception e) {
                if (msgErro.isEmpty()) {
                    return true;
                }
            }
        } else {// validar criacao
            try {
                if (nome.getText().isEmpty()) {
                    msgErro += "Nome é obrigatório\n";
                }
                Condominio o;
                try {
                    o = FindCondominio.condominioComNome(nome.getText());
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Nome já cadastrado\n";
                    }
                } catch (NoResultException ne) {
                }

                if (!(cnpj.getText().replace(".", "").replace("-", "")).isEmpty()) {
                    try {
                        o = FindCondominio.buscarCondominioPorCNPJ(cnpj.getText().replace(".", "").replace("-", ""));
                        if (o.getId() != entity.getId()) {// codigo de outro produto
                            msgErro += "CNPJ já cadastrado\n";
                        }
                    } catch (NonUniqueResultException nu) {
                        msgErro += "CNPJ já cadastrado\n";
                    }
                }
            } catch (Exception e) {
                if (msgErro.isEmpty()) {
                    return true;
                }
            }
        }
        if (!msgErro.isEmpty()) {
            Dialogs.create()
                    .title("Campos Inválidos")
                    .masthead("Por favor, corrija os campos inválidos")
                    .message(msgErro)
                    .showError();

        }
        return msgErro.isEmpty();
    }

    @Override
    @FXML
    public void okClicado() {
        if (isEntradaValida() && tipe != VIEW_MODAL) {
            if (tipe == EDIT_MODAL) {
                entity.setNome(nome.getText());
                entity.setCnpj(cnpj.getText());
                entity.setEndereco(endereco.getText());
                entity.setConta(conta.getText());
                entity.setAgencia(agencia.getText());
                entity.setBanco(banco.getValue());
                entity.setTaxaCondominial(OperacaoStringUtil.converterStringValor(taxa.getText()));
                entity.setDiaDoVencimento(OperacaoStringUtil.converterStringValorInt(vencimento.getText()));
                entity.setSindico(sindico.getValue());
                try {
                    //merge
                    f.edit(entity);
                } catch (Exception ex) {
                    Logger.getLogger(CondominioDialogController.class.getName()).log(Level.SEVERE, null, ex);
                    Dialogs.create().title("Erro ao Atualizar Condomínio")
                            .showException(ex);
                }
                dialogStage.close();
            } else if (tipe == CREATE_MODAL) {
                entity.setNome(nome.getText());
                entity.setCnpj(cnpj.getText());
                entity.setEndereco(endereco.getText());
                entity.setConta(conta.getText());
                entity.setAgencia(agencia.getText());
                entity.setBanco(banco.getValue());
                entity.setTaxaCondominial(OperacaoStringUtil.converterStringValor(taxa.getText()));
                entity.setDiaDoVencimento(OperacaoStringUtil.converterStringValorInt(vencimento.getText()));
                entity.setSindico(sindico.getValue());
                try {
                    //create
                    f.create(entity);
                    Dialogs.create()
                            .title("Condominio Criado com Sucesso")
                            .masthead("Nome: " + entity.getNome() + "\n"
                                    + "CNPJ: " + entity.getCnpj() + "\n"
                                    + "Endereço: " + entity.getEndereco())
                            .showInformation();
                } catch (Exception ex) {
                    Logger.getLogger(CondominioDialogController.class.getName()).log(Level.SEVERE, null, ex);
                    Dialogs.create()
                            .title("Erro ao Cadastrar Condomínio")
                            .showException(ex);
                }
                dialogStage.close();
            }
        }
        okClicked = true;
    }

    @Override
    public void setEntity(Condominio entity) {
        this.entity = entity;
        switch (tipe) {
            case EDIT_MODAL:
                sindico.setItems(FXCollections.observableArrayList(FindMorador.moradoresDoCondominio(entity)));
                sindico.setValue(entity.getSindico());
                
                entity.setSindico(sindico.getValue());

                page.setText("Editar Condominio");
                nome.setText(entity.getNome());
                cnpj.setText(entity.getCnpj());
                endereco.setText(entity.getEndereco());
                conta.setText(entity.getConta());
                agencia.setText(entity.getAgencia());
                taxa.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getTaxaCondominial()));
                banco.setValue(entity.getBanco());
                sindico.setValue(entity.getSindico());
                vencimento.setText(entity.getDiaDoVencimento() + "");
                break;
            case CREATE_MODAL:
                super.entity = new Condominio();
                page.setText("Criar Condominio");
                break;
            case VIEW_MODAL:
                page.setText("Condominio ID: " + entity.getId());
                nome.setText(entity.getNome());
                cnpj.setText(entity.getCnpj());
                endereco.setText(entity.getEndereco());
                conta.setText(entity.getConta());
                agencia.setText(entity.getAgencia());
                taxa.setText(OperacaoStringUtil.formatarStringValorMoeda(entity.getTaxaCondominial()));
                banco.setValue(entity.getBanco());
                sindico.setValue(entity.getSindico());
                vencimento.setText(entity.getDiaDoVencimento() + "");

                nome.setEditable(false);
                cnpj.setEditable(false);
                endereco.setEditable(false);
                conta.setEditable(false);
                agencia.setEditable(false);
                taxa.setEditable(false);
                banco.setEditable(false);
                sindico.setEditable(false);
                vencimento.setEditable(false);

                cancelButton.setVisible(false);
                break;
        }
    }

    @FXML
    public void excluirClicado() {

        Dialogs dialog = Dialogs.create().title("Excluir Condominio")
                .masthead("Tem certeza que deseja excluir todas as informações do Condominio " + entity.getNome() + "?")
                .message("Todos os Condôminos e Boletos desse Condominio serão apagadas\n"
                        + "Essa operação não pode ser revertida!");
        dialog.actions(Dialog.Actions.CANCEL, Dialog.Actions.YES);
        dialog.style(DialogStyle.UNDECORATED);
        Action act = dialog.showError();
        if (act.equals(Dialog.Actions.CANCEL)) {
            return;
        }

        try {
            //REMOVER
        } catch (Exception ee) {
            ee.printStackTrace();
            Dialogs.create().showException(ee);
            return;
        }
        dialog = Dialogs.create().title("Condominio excuido")
                .masthead("Condominio excuido com sucesso!")
                .message("Cadastro do Condominio " + entity.getNome() + " excuido com sucesso!");
        dialog.style(DialogStyle.UNDECORATED);
        dialog.showError();
        this.cancelarClicado();
    }

    @Override
    public String getTitulo() {
        switch (tipe) {
            case EDIT_MODAL:
                return "Editar Condominio";
            case CREATE_MODAL:
                return "Criar Condominio";
            default:
                return "Condominio";
        }
    }

}
