package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

public abstract class TelaBaseFrequencia extends VBox {

    protected Sistema sistema;
    protected Professor professorLogado;
    
    protected ComboBox<Turma> comboTurmas;
    protected DatePicker dpDataAula;
    protected Button btnCarregar;

    protected VBox areaLancamento;
    protected TableView<AlunoFrequenciaRow> tabelaFrequencia;
    protected Button btnSalvarChamada;

    protected Turma turmaSelecionada;

    public TelaBaseFrequencia(String titulo, Sistema sistema, Professor professor) {
        this.sistema = sistema;
        this.professorLogado = professor;

        this.setPadding(new Insets(30));
        this.setSpacing(15);
        this.getStyleClass().add("vbox");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 22));
        lblTitulo.getStyleClass().add("text-primary");
        
        GridPane formTopo = criarFormularioTopo();
        areaLancamento = criarAreaLancamento();
        
        this.getChildren().addAll(lblTitulo, formTopo, areaLancamento);
    }
    
    protected abstract List<AlunoFrequenciaRow> carregarDadosDaTurma(Turma turma, LocalDate data);
    protected abstract void salvarFrequencia(List<AlunoFrequenciaRow> dados);

    private GridPane criarFormularioTopo() {
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        
        form.add(new Label("Turma:"), 0, 0);
        comboTurmas = new ComboBox<>();
        comboTurmas.setPrefWidth(300);
        
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
        
        List<Turma> turmasDoProf = sistema.getTurmas().stream()
                .filter(t -> t.getProfessor() != null && t.getProfessor().getID().equals(professorLogado.getID()))
                .toList();
        comboTurmas.getItems().addAll(turmasDoProf);
        comboTurmas.setPromptText("Selecione uma turma...");
        
        form.add(comboTurmas, 1, 0);
        
        form.add(new Label("Data da Aula:"), 2, 0);
        dpDataAula = new DatePicker(LocalDate.now());
        dpDataAula.setPrefWidth(150);
        form.add(dpDataAula, 3, 0);
        
        btnCarregar = new Button("Carregar Alunos");
        btnCarregar.getStyleClass().addAll("btn", "btn-primary");
        btnCarregar.setOnAction(e -> onCarregar());
        form.add(btnCarregar, 4, 0);
        
        return form;
    }
    
    private VBox criarAreaLancamento() {
        VBox box = new VBox(10);
        box.setVisible(false);
        box.setAlignment(Pos.TOP_LEFT);
        
        tabelaFrequencia = criarTabelaFrequencia();
        btnSalvarChamada = new Button("SALVAR FREQUÊNCIA");
        btnSalvarChamada.getStyleClass().addAll("btn", "btn-success", "btn-lg");
        btnSalvarChamada.setOnAction(e -> onSalvar());

        box.getChildren().addAll(new Label("Marcar Presença:"), tabelaFrequencia, btnSalvarChamada);
        return box;
    }
    
    private TableView<AlunoFrequenciaRow> criarTabelaFrequencia() {
        TableView<AlunoFrequenciaRow> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setPrefHeight(300);

        TableColumn<AlunoFrequenciaRow, String> colMatricula = new TableColumn<>("Matrícula");
        colMatricula.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAluno().getMatricula()));
        colMatricula.setMinWidth(100); 
        colMatricula.setMaxWidth(150);
        
        TableColumn<AlunoFrequenciaRow, String> colNome = new TableColumn<>("Nome do Aluno");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAluno().getNome()));
        
        TableColumn<AlunoFrequenciaRow, Boolean> colPresenca = new TableColumn<>("Presença");
        colPresenca.setCellValueFactory(data -> data.getValue().getPresente().selectedProperty());
        
        colPresenca.setCellFactory(col -> new TableCell<AlunoFrequenciaRow, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(getTableView().getItems().get(getIndex()).getPresente());
                }
            }
        });
        colPresenca.setMinWidth(80);
        colPresenca.setMaxWidth(100);

        tabela.getColumns().addAll(colMatricula, colNome, colPresenca);
        return tabela;
    }
    
    private void onCarregar() {
        turmaSelecionada = comboTurmas.getValue();
        LocalDate dataSelecionada = dpDataAula.getValue();
        
        if (turmaSelecionada == null || dataSelecionada == null) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Seleção", "Selecione a Turma e a Data da Aula.");
            return;
        }

        try {
            List<AlunoFrequenciaRow> dados = carregarDadosDaTurma(turmaSelecionada, dataSelecionada);
            
            // Evita o UnsupportedOperationException ao tentar limpar depois
            tabelaFrequencia.setItems(FXCollections.observableArrayList(dados));
            areaLancamento.setVisible(true);

        } catch (Exception e) {
            GuiUtils.mostrarAlerta(Alert.AlertType.ERROR, "Erro", e.getMessage());
            areaLancamento.setVisible(false);
        }
    }

    private void onSalvar() {
        // Validar Data
        if (dpDataAula.getValue() == null) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione a data da aula.");
            return;
        }

        try {
            salvarFrequencia(tabelaFrequencia.getItems());
            sistema.salvarDados();
            
            GuiUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Frequência registrada/atualizada com sucesso!");
            
            // Em vez de limpar a lista (que pode dar erro se for imutável), trocamos por uma nova
            tabelaFrequencia.setItems(FXCollections.observableArrayList());
            areaLancamento.setVisible(false);
            
            if (comboTurmas.getSelectionModel() != null) {
                comboTurmas.getSelectionModel().clearSelection();
            }
            turmaSelecionada = null;
            
        } catch (Exception e) {
            e.printStackTrace();
            String msg = (e.getMessage() != null) ? e.getMessage() : "Erro desconhecido ao salvar.";
            GuiUtils.mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", msg);
        }
    }
}