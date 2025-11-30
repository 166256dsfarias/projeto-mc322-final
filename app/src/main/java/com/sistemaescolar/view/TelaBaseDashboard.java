package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Pessoa;
import com.sistemaescolar.sistema.Main;
import com.sistemaescolar.sistema.Sistema;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

public abstract class TelaBaseDashboard {

    protected Stage palco;
    protected Sistema sistema;
    protected Pessoa usuarioLogado;
    protected BorderPane layoutPrincipal;

    public TelaBaseDashboard(Stage palco, Sistema sistema, Pessoa usuario) {
        this.palco = palco;
        this.sistema = sistema;
        this.usuarioLogado = usuario;
    }

    public void mostrar() {
        layoutPrincipal = new BorderPane();
        
        //Topo
        HBox topo = criarTopo();
        layoutPrincipal.setTop(topo);

        // Menu Lateral
        Node menu = criarMenuLateral();
        if (menu != null) {
            layoutPrincipal.setLeft(menu);
        }

        // Centro
        layoutPrincipal.setCenter(criarConteudoInicial());

        Scene cena = new Scene(layoutPrincipal, 900, 600);
        cena.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        palco.setTitle(getTituloJanela());
        palco.setScene(cena);
        palco.show();
    }

    // --- MÉTODOS ABSTRATOS (As filhas implementam o que é específico) ---
    protected abstract String getTituloJanela();
    protected abstract String getTituloTopo();
    protected abstract String getCorTopo(); 
    protected abstract Node criarMenuLateral();

    // --- MÉTODOS COMUNS ---

    private HBox criarTopo() {
        HBox topo = new HBox();
        topo.setPadding(new Insets(15, 20, 15, 20));
        topo.setAlignment(Pos.CENTER_LEFT);
        topo.setStyle(getCorTopo());

        Label lblTitulo = new Label(getTituloTopo());
        lblTitulo.setTextFill(javafx.scene.paint.Color.WHITE);
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        Label lblUsuario = new Label("Usuário: " + usuarioLogado.getNome() + "  |  ");
        lblUsuario.setTextFill(javafx.scene.paint.Color.WHITE);

        Button btnSair = new Button("Sair");
        btnSair.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
        btnSair.setOnAction(e -> fazerLogout());

        topo.getChildren().addAll(lblTitulo, espacador, lblUsuario, btnSair);
        return topo;
    }

    protected VBox criarConteudoInicial() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        Label lbl = new Label("Bem-vindo, " + usuarioLogado.getNome() + "!");
        lbl.getStyleClass().add("h2");
        Label lbl2 = new Label("Selecione uma opção no menu à esquerda.");
        lbl2.getStyleClass().add("lead");
        box.getChildren().addAll(lbl, lbl2);
        return box;
    }
    
    // Método utilitário para botões de menu
    protected Button criarBotaoMenu(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().addAll("btn", "btn-default");
        return btn;
    }

    protected void fazerLogout() {
        sistema.salvarDados(); // Garante persistência
        Main.setUsuarioLogado(null);
        try {
            new TelaLogin().start(palco);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}