package com.sistemaescolar.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import com.sistemaescolar.exceptions.CredenciaisInvalidasException;
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Aluno;    
import com.sistemaescolar.pessoas.Pessoa;    
import com.sistemaescolar.pessoas.Professor; 
import com.sistemaescolar.sistema.Main;

import org.kordamp.bootstrapfx.BootstrapFX;
import java.util.prefs.Preferences;

public class TelaLogin extends Application {

    private Preferences prefs = Preferences.userNodeForPackage(TelaLogin.class);
    private static final String PREF_USER = "saved_user";
    private static final String PREF_PASS = "saved_pass";
    private static final String PREF_REMEMBER = "is_remember";

    @Override
    public void start(Stage palco) {
        palco.setTitle("Sistema Acadêmico - Login");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(40));

        Label titulo = new Label("Bem-vindo");
        titulo.getStyleClass().addAll("h3", "text-primary"); 
        grid.add(titulo, 0, 0, 2, 1);

        // --- USUÁRIO ---
        Label lblUser = new Label("Matrícula/ID:");
        grid.add(lblUser, 0, 1);
        TextField txtUser = new TextField();
        grid.add(txtUser, 1, 1);

        // --- SENHA ---
        Label lblPass = new Label("Senha:");
        grid.add(lblPass, 0, 2);

        PasswordField txtPassHidden = new PasswordField();
        TextField txtPassVisible = new TextField();
        
        txtPassVisible.setVisible(false);
        txtPassVisible.setManaged(false);
        txtPassVisible.textProperty().bindBidirectional(txtPassHidden.textProperty());

        ToggleButton btnEye = new ToggleButton("👁");
        btnEye.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        
        btnEye.setOnAction(e -> {
            if (btnEye.isSelected()) {
                txtPassVisible.setVisible(true);
                txtPassVisible.setManaged(true);
                txtPassHidden.setVisible(false);
                txtPassHidden.setManaged(false);
            } else {
                txtPassVisible.setVisible(false);
                txtPassVisible.setManaged(false);
                txtPassHidden.setVisible(true);
                txtPassHidden.setManaged(true);
            }
        });

        StackPane stackCampos = new StackPane(txtPassHidden, txtPassVisible);
        HBox boxSenha = new HBox(5, stackCampos, btnEye);
        boxSenha.setAlignment(Pos.CENTER_LEFT);
        
        grid.add(boxSenha, 1, 2);

        // --- CHECKBOX ---
        CheckBox chkLembrar = new CheckBox("Manter conectado");
        grid.add(chkLembrar, 1, 3);

        // Carregar prefs
        if (prefs.getBoolean(PREF_REMEMBER, false)) {
            txtUser.setText(prefs.get(PREF_USER, ""));
            txtPassHidden.setText(prefs.get(PREF_PASS, ""));
            chkLembrar.setSelected(true);
        }

        Button btnEntrar = new Button("Entrar");
        btnEntrar.getStyleClass().addAll("btn", "btn-primary", "btn-lg"); 
        btnEntrar.setMaxWidth(Double.MAX_VALUE); 
        
        grid.add(btnEntrar, 0, 4, 2, 1);

        btnEntrar.setOnAction(e -> {
            String usuario = txtUser.getText();
            String senha = txtPassHidden.getText();

            try {
                // Tenta logar
                Pessoa logado = Main.getSistema().fazerLogin(usuario, senha);
                Main.setUsuarioLogado(logado);

                // Salva Prefs
                if (chkLembrar.isSelected()) {
                    prefs.put(PREF_USER, usuario);
                    prefs.put(PREF_PASS, senha);
                    prefs.putBoolean(PREF_REMEMBER, true);
                } else {
                    prefs.remove(PREF_USER);
                    prefs.remove(PREF_PASS);
                    prefs.putBoolean(PREF_REMEMBER, false);
                }

                // Redirecionamentos (Polimorfismo)
                if (logado instanceof Administrador) {
                    new TelaAdmin(palco, Main.getSistema(), (Administrador) logado).mostrar();
                    
                } else if (logado instanceof Professor) { 
                    new TelaProfessor(palco, Main.getSistema(), (Professor) logado).mostrar();
                    
                } else if (logado instanceof Aluno) {
                    new TelaAluno(palco, Main.getSistema(), (Aluno) logado).mostrar();
                    
                } else {
                    palco.close(); 
                }            

            } catch (CredenciaisInvalidasException ex) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Erro de Acesso");
                alerta.setHeaderText(null);
                alerta.setContentText(ex.getMessage());
                alerta.showAndWait();
            }
        });

        Scene cena = new Scene(grid, 450, 350);
        cena.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        palco.setScene(cena);
        palco.show();
    }
}