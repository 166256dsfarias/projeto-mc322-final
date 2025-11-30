package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.sistema.Sistema;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.util.ArrayList;
import java.util.List;

public abstract class TelaBaseRelatorioAluno<T> extends VBox {

    protected Sistema sistema;
    protected Aluno alunoLogado;
    
    protected ComboBox<Turma> comboTurmas;
    protected TableView<T> tabelaDados;
    protected Label lblResumo; //  mostra a Média ou % de Presença

    public TelaBaseRelatorioAluno(String titulo, Sistema sistema, Aluno aluno) {
        this.sistema = sistema;
        this.alunoLogado = aluno;

        this.setPadding(new Insets(30));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_LEFT);

        //Título
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 22));
        lblTitulo.getStyleClass().add("text-primary");

        // Seleção de Turma
        Label lblSelect = new Label("Selecione a Turma/Disciplina:");
        configurarComboTurmas();

        //Tabela (Abstrata)
        tabelaDados = criarTabela();
        tabelaDados.setPrefHeight(300);
        tabelaDados.setPlaceholder(new Label("Selecione uma turma para visualizar."));

        // Resumo (Agregador)
        lblResumo = new Label();
        lblResumo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        VBox boxResumo = new VBox(lblResumo);
        boxResumo.setAlignment(Pos.CENTER_RIGHT); 
        boxResumo.setPadding(new Insets(10));
        boxResumo.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");

        this.getChildren().addAll(lblTitulo, lblSelect, comboTurmas, tabelaDados, boxResumo);
    }

    // --- MÉTODOS ABSTRATOS ---
    protected abstract TableView<T> criarTabela();
    protected abstract List<T> carregarDados(Turma turma);
    protected abstract String calcularResumo(List<T> dados);

    // --- LÓGICA COMUM ---
    private void configurarComboTurmas() {
        comboTurmas = new ComboBox<>();
        comboTurmas.setPrefWidth(400);
        
        comboTurmas.setConverter(new StringConverter<Turma>() {
            @Override
            public String toString(Turma t) {
                if (t == null) return null;
                String disc = (t.getDisciplina() != null) ? t.getDisciplina().getNome() : "S/ Disc";
                return String.format("%s (%d) - %s", t.getNome(), t.getAnoLetivo(), disc);
            }
            @Override
            public Turma fromString(String string) { return null; }
        });

        // Filtra turmas onde o aluno está matriculado
        List<Turma> turmasDoAluno = new ArrayList<>();
        for (Turma t : sistema.getTurmas()) {
            if (t.getAlunosDaTurma().contains(alunoLogado)) {
                turmasDoAluno.add(t);
            }
        }
        
        comboTurmas.setItems(FXCollections.observableArrayList(turmasDoAluno));
        comboTurmas.setPromptText("Selecione...");

        // Ao selecionar, carrega dados e calcula resumo
        comboTurmas.setOnAction(e -> {
            Turma t = comboTurmas.getValue();
            if (t != null) {
                List<T> dados = carregarDados(t);
                tabelaDados.setItems(FXCollections.observableArrayList(dados));
                
                if (dados.isEmpty()) {
                    tabelaDados.setPlaceholder(new Label("Nenhum registro lançado nesta turma."));
                }
                
                // Atualiza o texto do agregador
                lblResumo.setText(calcularResumo(dados));
            }
        });
    }
}