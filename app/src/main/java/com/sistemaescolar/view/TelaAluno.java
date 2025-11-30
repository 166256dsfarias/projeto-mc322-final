package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.sistema.Sistema;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaAluno extends TelaBaseDashboard {

    private Aluno alunoLogado;

    public TelaAluno(Stage palco, Sistema sistema, Aluno aluno) {
        super(palco, sistema, aluno);
        this.alunoLogado = aluno;
    }

    // --- IDENTIDADE VISUAL ---

    @Override
    protected String getTituloJanela() {
        return "Sistema Acadêmico - Painel do Aluno - " + alunoLogado.getNome();
    }

    @Override
    protected String getTituloTopo() {
        return "SGA - Portal do Aluno";
    }

    @Override
    protected String getCorTopo() {
        return "-fx-background-color: #8E44AD;"; // Roxo
    }

    // --- MENU LATERAL ---

    @Override
    protected Node criarMenuLateral() {
        Accordion accordion = new Accordion();
        accordion.setMinWidth(250);

        //ACADÊMICO
        VBox boxAcademico = new VBox(5);
        boxAcademico.setPadding(new Insets(10));

        Button btnBoletim = criarBotaoMenu("Meu Boletim");
        btnBoletim.setOnAction(e -> {
            layoutPrincipal.setCenter(new TelaBoletimAluno(sistema, (Aluno) usuarioLogado));
        });

        Button btnFreq = criarBotaoMenu("Minha Frequência");
        btnFreq.setOnAction(e -> {
            layoutPrincipal.setCenter(new TelaFrequenciaAluno(sistema, (Aluno) usuarioLogado));
        });

        boxAcademico.getChildren().addAll(btnBoletim, btnFreq);
        TitledPane paneAcademico = new TitledPane("Acadêmico", boxAcademico);
        paneAcademico.getStyleClass().add("panel-info"); // Azul Claro

        //COMUNICAÇÃO
        VBox boxComunicacao = new VBox(5);
        boxComunicacao.setPadding(new Insets(10));

        Button btnAvisos = criarBotaoMenu("Mural de Avisos");
        btnAvisos.setOnAction(e -> {
            layoutPrincipal.setCenter(new TelaMuralAvisos(sistema, alunoLogado));
        });

        boxComunicacao.getChildren().add(btnAvisos);
        TitledPane paneComunicacao = new TitledPane("Comunicação", boxComunicacao);
        paneComunicacao.getStyleClass().add("panel-warning"); // Amarelo/Laranja

        accordion.getPanes().addAll(paneAcademico, paneComunicacao);
        //accordion.setExpandedPane(paneAcademico);

        return accordion;
    }

}