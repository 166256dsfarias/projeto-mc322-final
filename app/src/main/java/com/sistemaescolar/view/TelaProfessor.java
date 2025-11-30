package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.view.TelaRealizarChamada; 

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaProfessor extends TelaBaseDashboard {

    private Professor professorLogado;

    public TelaProfessor(Stage palco, Sistema sistema, Professor professor) {
        super(palco, sistema, professor);
        this.professorLogado = professor;
    }

    // --- DEFINIÇÕES VISUAIS ---

    @Override
    protected String getTituloJanela() {
        return "Sistema Acadêmico - Painel do Professor - " + professorLogado.getNome();
    }

    @Override
    protected String getTituloTopo() {
        return "SGA - Portal do Professor";
    }

    @Override
    protected String getCorTopo() {
        return "-fx-background-color: #16A085;"; // Verde Petróleo
    }

    // --- O MENU DO PROFESSOR ---
    @Override
    protected Node criarMenuLateral() {
        Accordion accordion = new Accordion();
        accordion.setMinWidth(250);

        //NOTAS
        VBox boxNotas = new VBox(5);
        boxNotas.setPadding(new Insets(10));

        Button btnLancarNota = criarBotaoMenu("Lançar Nota");
        btnLancarNota.setOnAction(e -> {
            TelaLancarNotas tela = new TelaLancarNotas(sistema, professorLogado);
            layoutPrincipal.setCenter(tela);
        });

        Button btnAtualizarNota = criarBotaoMenu("Atualizar Nota");
        btnAtualizarNota.setOnAction(e -> {
            TelaAtualizarNotas tela = new TelaAtualizarNotas(sistema, professorLogado);
            layoutPrincipal.setCenter(tela);
        });

        boxNotas.getChildren().addAll(btnLancarNota, btnAtualizarNota);
        TitledPane paneNotas = new TitledPane("Gestão de Notas", boxNotas);
        paneNotas.getStyleClass().add("panel-success");

        //FREQUÊNCIA
        VBox boxFreq = new VBox(5);
        boxFreq.setPadding(new Insets(10));

        Button btnChamada = criarBotaoMenu("Realizar Chamada");
        btnChamada.setOnAction(e -> {
            TelaRealizarChamada tela = new TelaRealizarChamada(sistema, professorLogado);
            layoutPrincipal.setCenter(tela);
        });

        Button btnAttFreq = criarBotaoMenu("Corrigir Frequência");
        btnAttFreq.setOnAction(e -> {
            layoutPrincipal.setCenter(new TelaCorrigirFrequencia(sistema, professorLogado));
        });

        boxFreq.getChildren().addAll(btnChamada, btnAttFreq);
        TitledPane paneFreq = new TitledPane("Frequência", boxFreq);
        paneFreq.getStyleClass().add("panel-info");

        //COMUNICAÇÃO
        VBox boxAvisos = new VBox(5);
        boxAvisos.setPadding(new Insets(10));

        Button btnAviso = criarBotaoMenu("Enviar Aviso à Turma");
        btnAviso.setOnAction(e -> {
            layoutPrincipal.setCenter(new TelaMuralAvisos(sistema, professorLogado));
        });

        boxAvisos.getChildren().addAll(btnAviso);
        TitledPane paneAvisos = new TitledPane("Comunicação", boxAvisos);
        paneAvisos.getStyleClass().add("panel-warning");

        accordion.getPanes().addAll(paneNotas, paneFreq, paneAvisos);
        //accordion.setExpandedPane(paneNotas);

        return accordion;
    }
    
}