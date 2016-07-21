package com.cd.ui.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.cd.*;
import com.cd.util.OperacaoStringUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Lettiery
 */
public class PrincipalController implements Initializable {

    @FXML
    private StackPane stack;

    @FXML
    private Pane home;

    @FXML
    private SplitMenuButton user;

    @FXML
    private Label version_system;

    private Facede f;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            f = Facede.getInstance();
            String login = f.getLogado().getLogin();
            user.setText(login);
        } catch (Exception e) {
            e.printStackTrace();
        }
        version_system.setText(Facede.version_system);
        adicionarAtalhos();
    }

    public void adicionarAtalhos() {
        home.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (t.isShiftDown() && t.getCode().equals(KeyCode.V)) {
                Main.trocarDeTela(ControllerTelas.TELA_VENDA);
            }
        });
    }

    @FXML
    public void irHome() {
        stack.getChildren().clear();
        stack.getChildren().addAll(home);
    }

    @FXML
    public void logoff() {
        f.logoff();
        Platform.exit();
    }

    @FXML
    public void trocarDeUsuario() {
        f.logoff();
        Main.trocarDeTela(ControllerTelas.TELA_LOGIN);
    }

    @FXML
    public void botaoMenu(ActionEvent e) {
        Button bt = (Button) e.getSource();
        String label = (bt.getId() != null ? bt.getId() : bt.getText()).toLowerCase();
        stack.getChildren().clear();
        label = OperacaoStringUtil.removerAcentos(label);
        stack.getChildren().add(getNode("fxml/" + label + ".fxml"));

    }

    private Node getNode(String node) {
        Node no = null;
        try {
            no = FXMLLoader.load(getClass().getResource(node));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return no;

    }

}
