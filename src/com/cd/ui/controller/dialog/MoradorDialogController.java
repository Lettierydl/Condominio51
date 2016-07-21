package com.cd.ui.controller.dialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cd.sis.bean.Condominio;
import com.cd.sis.bean.Morador;
import com.cd.sis.controller.find.FindCondominio;
import com.cd.sis.controller.find.FindMorador;
import static com.cd.ui.controller.dialog.DialogController.CREATE_MODAL;
import static com.cd.ui.controller.dialog.DialogController.EDIT_MODAL;
import static com.cd.ui.controller.dialog.DialogController.VIEW_MODAL;
import com.cd.util.JavaFXUtil;
import com.cd.util.MaskFieldUtil;
import org.controlsfx.dialog.Dialogs;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
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
public class MoradorDialogController extends DialogController<Morador> {
    
    static Condominio selecionado;
    
    @FXML
    private TextField nome;
    @FXML
    private TextField cpf;
    @FXML
    private TextField telefone;

    @FXML
    private TextField email;
    @FXML
    private TextField unidade;

    public MoradorDialogController() {
        super();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MaskFieldUtil.upperCase(nome);
        MaskFieldUtil.upperCase(email);
        MaskFieldUtil.cpfField(cpf);
        MaskFieldUtil.foneField(telefone);
        MaskFieldUtil.upperCase(unidade);

        JavaFXUtil.nextFielOnAction(nome, cpf);
        JavaFXUtil.nextFielOnAction(cpf, telefone);
        JavaFXUtil.nextFielOnAction(telefone, email);
        JavaFXUtil.nextFielOnAction(email, unidade);
        JavaFXUtil.nextFielOnAction(unidade, okButton);

        okButton.setOnKeyReleased((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                okClicado();
            }
        });
    }

    @Override
    public boolean isEntradaValida() {
        String msgErro = "";
        if (tipe == EDIT_MODAL) {// validar edicao
            try {
                if (nome.getText().isEmpty()) {
                    msgErro += "Nome é obrigatório\n";
                }
                Morador o;
                try {
                    o = FindMorador.moradoreComUnidade(unidade.getText(),
                            selecionado);
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Unidade já cadastrada para esse condomínio\n";
                    }
                } catch (NoResultException ne) {
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
                Morador o;
                try {
                    o = FindMorador.moradoreComUnidade(unidade.getText(),
                            selecionado);
                    if (o.getId() != entity.getId()) {// codigo de outro produto
                        msgErro += "Unidade já cadastrada para esse condomínio\n";
                    }
                } catch (NoResultException ne) {
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
                entity.setCpf(cpf.getText());
                entity.setTelefone(telefone.getText());
                entity.setEmail(email.getText());
                entity.setUnidade(unidade.getText());
                try {
                    //merge
                    f.edit(entity);
                } catch (Exception ex) {
                    Logger.getLogger(MoradorDialogController.class.getName()).log(Level.SEVERE, null, ex);
                    Dialogs.create().title("Erro ao Atualizar Condômino")
                            .showException(ex);
                }
                dialogStage.close();
            } else if (tipe == CREATE_MODAL) {
                entity.setNome(nome.getText());
                entity.setCpf(cpf.getText());
                entity.setTelefone(telefone.getText());
                entity.setEmail(email.getText());
                entity.setUnidade(unidade.getText());
                entity.setCondominio(selecionado);
                try {
                    //create
                    f.create(entity);
                    Condominio c = FindCondominio.buscarCondominioPorCNPJ(selecionado.getCnpj());
                    c.setUnidades(c.getUnidades()+1);
                    f.edit(c);
                    Dialogs.create()
                            .title("Condômino Criado com Sucesso")
                            .masthead("Nome: " + entity.getNome() + "\n"
                                    + "CPF: " + entity.getCpf() + "\n"
                                    + "Unidade: " + entity.getUnidade()+ "\n"
                                    + "Email: " + entity.getEmail()+ "\n"
                                    + "Condomínio: " + entity.getCondominio()
                            )
                            .showInformation();
                } catch (Exception ex) {
                    Logger.getLogger(MoradorDialogController.class.getName()).log(Level.SEVERE, null, ex);
                    Dialogs.create()
                            .title("Erro ao Cadastrar Condômino")
                            .showException(ex);
                }
                dialogStage.close();
            }
        }
        okClicked = true;
    }

    @Override
    public void setEntity(Morador entity) {
        this.entity = entity;
        switch (tipe) {
            case EDIT_MODAL:
                page.setText("Editar Condômino");
                nome.setText(entity.getNome());
                cpf.setText(entity.getCpf());
                email.setText(entity.getEmail());
                telefone.setText(entity.getTelefone());
                email.setText(entity.getEmail());
                unidade.setText(entity.getUnidade());
                break;
            case CREATE_MODAL:
                super.entity = new Morador();
                page.setText("Criar Condômino");
                break;
            case VIEW_MODAL:
                page.setText("Condômino ID: " + entity.getId());
                nome.setText(entity.getNome());
                cpf.setText(entity.getCpf());
                email.setText(entity.getEmail());
                telefone.setText(entity.getTelefone());
                email.setText(entity.getEmail());
                unidade.setText(entity.getUnidade());

                nome.setEditable(false);
                cpf.setEditable(false);
                email.setEditable(false);
                telefone.setEditable(false);
                unidade.setEditable(false);
                
                cancelButton.setVisible(false);
                break;
        }
    }

    @FXML
    public void excluirClicado() {

        Dialogs dialog = Dialogs.create().title("Excluir Condômino")
                .masthead("Tem certeza que deseja excluir todas as informações do Condômino " + entity.getNome() + "?")
                .message("Todos os Boletos desse Condômino serão apagados\n"
                        + "Essa operação não pode ser revertida!");
        dialog.actions(Dialog.Actions.CANCEL, Dialog.Actions.YES);
        dialog.style(DialogStyle.UNDECORATED);
        Action act = dialog.showError();
        if (act.equals(Dialog.Actions.CANCEL)) {
            return;
        }

        try {
            f.destroy(entity);
        } catch (Exception ee) {
            ee.printStackTrace();
            Dialogs.create().showException(ee);
            return;
        }
        dialog = Dialogs.create().title("Condômino excuido")
                .masthead("Condômino excuido com sucesso!")
                .message("Cadastro do Condômino " + entity.getNome() + " excuido com sucesso!");
        dialog.style(DialogStyle.UNDECORATED);
        dialog.showError();
        this.cancelarClicado();
    }

    @Override
    public String getTitulo() {
        switch (tipe) {
            case EDIT_MODAL:
                return "Editar Condômino";
            case CREATE_MODAL:
                return "Criar Condômino";
            default:
                return "Condômino";
        }
    }
    
    
    public static void setCondominio(Condominio c){
        selecionado = c;
    }
}
