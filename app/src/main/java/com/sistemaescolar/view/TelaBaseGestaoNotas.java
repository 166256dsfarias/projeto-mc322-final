package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public abstract class TelaBaseGestaoNotas extends VBox {

    protected Sistema sistema;
    protected Professor professorLogado;
    protected Turma turmaSelecionada;

    protected ComboBox<Turma> comboTurmas;
    protected TableView<AlunoNotaRow> tabelaNotas;
    protected VBox areaConteudoEspecifico;
    protected VBox areaTabela;

    public TelaBaseGestaoNotas(Sistema sistema, Professor professor, String titulo) {
        this.sistema = sistema;
        this.professorLogado = professor;

        this.setPadding(new Insets(30));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_LEFT);

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblTitulo.getStyleClass().add("text-success");

        Label lblSelect = new Label("Selecione a Turma:");
        configurarComboTurmas();

        areaConteudoEspecifico = new VBox(10);
        
        areaTabela = new VBox(10);
        areaTabela.setVisible(false);
        
        tabelaNotas = criarTabelaNotas();
        
        Button btnSalvar = new Button("SALVAR DADOS");
        btnSalvar.getStyleClass().addAll("btn", "btn-success", "btn-lg");
        btnSalvar.setMaxWidth(Double.MAX_VALUE);
        
        // VALIDAÇÃO ANTES DE CHAMAR A FILHA
        btnSalvar.setOnAction(e -> {
            if (validarTodasNotasPreenchidas()) {
                onSalvar(); // Só chama se tudo estiver ok
            }
        });

        areaTabela.getChildren().addAll(new Label("Alunos:"), tabelaNotas, btnSalvar);
        this.getChildren().addAll(lblTitulo, lblSelect, comboTurmas, areaConteudoEspecifico, areaTabela);
    }

    protected abstract void onTurmaSelecionada(Turma t);
    protected abstract void onSalvar();

    private boolean validarTodasNotasPreenchidas() {
        boolean tudoOk = true;
        
        for (AlunoNotaRow linha : tabelaNotas.getItems()) {
            String valorStr = linha.getCampoNota().getText();
            
            if (valorStr == null || valorStr.trim().isEmpty()) {
                linha.getCampoNota().setStyle("-fx-border-color: red;");
                tudoOk = false;
            } else {
                // Restaura estilo se tiver corrigido
                linha.getCampoNota().setStyle("");
            }
        }

        if (!tudoOk) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", 
                "Existem alunos sem nota.\nRegra: Digite 0.0 se o aluno não tiver nota.");
        }
        return tudoOk;
    }

    private void configurarComboTurmas() {
        comboTurmas = new ComboBox<>();
        comboTurmas.setPrefWidth(400);
        
        comboTurmas.setConverter(new StringConverter<Turma>() {
            @Override
            public String toString(Turma t) {
                if (t == null) return null;
                String nomeDisc = (t.getDisciplina() != null) ? t.getDisciplina().getNome() : "S/ Disc";
                int qtd = (t.getAlunosDaTurma() != null) ? t.getAlunosDaTurma().size() : 0;
                return String.format("%s (%d) - %s - [%d alunos]", t.getNome(), t.getAnoLetivo(), nomeDisc, qtd);
            }
            @Override
            public Turma fromString(String string) { return null; }
        });

        comboTurmas.setOnAction(e -> {
            Turma t = comboTurmas.getValue();
            if (t != null) {
                this.turmaSelecionada = t;
                onTurmaSelecionada(t);
            }
        });

        carregarTurmasDoProfessor();
    }

    private void carregarTurmasDoProfessor() {
        List<Turma> turmasDoProf = new ArrayList<>();
        for (Turma t : sistema.getTurmas()) {
            if (t.getProfessor() == null) continue;
            boolean match = false;
            if (t.getProfessor().getID() != null && professorLogado.getID() != null) {
                if (t.getProfessor().getID().equals(professorLogado.getID())) match = true;
            } else {
                String cpfT = t.getProfessor().getCpf().replaceAll("[^0-9]", "");
                String cpfL = professorLogado.getCpf().replaceAll("[^0-9]", "");
                if (cpfT.equals(cpfL)) match = true;
            }
            if (match) turmasDoProf.add(t);
        }
        comboTurmas.setItems(FXCollections.observableArrayList(turmasDoProf));
    }

    private TableView<AlunoNotaRow> criarTabelaNotas() {
        TableView<AlunoNotaRow> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPrefHeight(300);

        TableColumn<AlunoNotaRow, String> colMat = new TableColumn<>("Matrícula");
        colMat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAluno().getMatricula()));
        colMat.setMinWidth(100); colMat.setMaxWidth(150);

        TableColumn<AlunoNotaRow, String> colNome = new TableColumn<>("Aluno (a)");
        colNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAluno().getNome()));

        TableColumn<AlunoNotaRow, TextField> colNota = new TableColumn<>("Nota (0.0 - 10.0)");
        colNota.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCampoNota()));
        colNota.setMaxWidth(150);

        tabela.getColumns().addAll(colMat, colNome, colNota);
        return tabela;
    }
}