package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.sistema.Sistema;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaAdmin extends TelaBaseDashboard {

    private Administrador adminLogado;

    public TelaAdmin(Stage palco, Sistema sistema, Administrador admin) {
        super(palco, sistema, admin);
        this.adminLogado = admin;
    }

    // --- DEFINIÇÕES VISUAIS ---

    @Override
    protected String getTituloJanela() {
        return "Sistema Acadêmico - Painel Administrativo";
    }

    @Override
    protected String getTituloTopo() {
        return "SGA - Portal do Admin";
    }

    @Override
    protected String getCorTopo() {
        return "-fx-background-color: #2C3E50;"; // Azul Escuro
    }

    // --- O MENU ESPECÍFICO DO ADMIN ---
    @Override
    protected Node criarMenuLateral() {
        Accordion accordion = new Accordion();
        accordion.setMinWidth(250);

        //GESTÃO DE PESSOAS
        VBox boxPessoas = new VBox(5);
        boxPessoas.setPadding(new Insets(10));
        
        Button btnCadAluno = criarBotaoMenu("Cadastrar Aluno");
        btnCadAluno.setOnAction(e -> {
            TelaCadastroAluno tela = new TelaCadastroAluno(sistema, adminLogado);
            layoutPrincipal.setCenter(tela);
        });
        
        Button btnCadFuncionario = criarBotaoMenu("Cadastrar Funcionário");
        btnCadFuncionario.setOnAction(e -> {
            TelaCadastroFuncionario tela = new TelaCadastroFuncionario(sistema, adminLogado);
            layoutPrincipal.setCenter(tela);
        });

        boxPessoas.getChildren().addAll(btnCadAluno, btnCadFuncionario);
        TitledPane panePessoas = new TitledPane("Gestão de Pessoas", boxPessoas);
        panePessoas.getStyleClass().add("panel-primary"); 

        //GESTÃO ACADÊMICA
        VBox boxAcademico = new VBox(5);
        boxAcademico.setPadding(new Insets(10));

        Button btnCadTurma = criarBotaoMenu("Cadastrar Turma");
        btnCadTurma.setOnAction(e -> {
            TelaCadastroTurma tela = new TelaCadastroTurma(sistema, adminLogado);
            layoutPrincipal.setCenter(tela);
        });

        Button btnCadDisc = criarBotaoMenu("Cadastrar Disciplina");
        btnCadDisc.setOnAction(e -> {
            TelaCadastroDisciplina tela = new TelaCadastroDisciplina(sistema, adminLogado);
            layoutPrincipal.setCenter(tela);
        });
        
        Button btnMatriculaExtra = criarBotaoMenu("Matrícula em Disciplina");
        btnMatriculaExtra.setOnAction(e -> {
            TelaMatriculaAvulsa tela = new TelaMatriculaAvulsa(sistema, adminLogado);
            layoutPrincipal.setCenter(tela);
        });
        
        boxAcademico.getChildren().addAll(btnCadTurma, btnCadDisc, btnMatriculaExtra);

        TitledPane paneAcademico = new TitledPane("Gestão Acadêmica", boxAcademico);
        paneAcademico.getStyleClass().add("panel-info");

        accordion.getPanes().addAll(panePessoas, paneAcademico);
        //accordion.setExpandedPane(panePessoas); 

        return accordion;
    }
}