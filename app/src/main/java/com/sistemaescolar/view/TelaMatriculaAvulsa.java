package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.util.List;

public class TelaMatriculaAvulsa extends TelaCadastroBase<Aluno> {

    private ComboBox<Aluno> comboAluno;
    private ComboBox<Turma> comboTurma;

    public TelaMatriculaAvulsa(Sistema sistema, Administrador admin) {
        super("Matrícula em Disciplinas Extras", sistema, admin);
    }

    @Override
    protected void adicionarCamposAoFormulario(GridPane form) {
        // Seleção de Aluno
        form.add(GuiUtils.criarLabelObrigatorio("Selecione o Aluno:"), 0, 0);
        comboAluno = new ComboBox<>();
        comboAluno.setPrefWidth(300);
        comboAluno.setPromptText("Escolha o aluno...");
        
        // Conversor para mostrar "Nome (Matrícula)"
        comboAluno.setConverter(new StringConverter<Aluno>() {
            @Override
            public String toString(Aluno a) {
                return (a == null) ? "" : a.getNome() + " (" + a.getMatricula() + ")";
            }
            @Override
            public Aluno fromString(String string) { return null; }
        });
        comboAluno.getItems().addAll(sistema.getAlunos());
        form.add(comboAluno, 1, 0);

        // 2. Seleção de Turma (Destino)
        form.add(GuiUtils.criarLabelObrigatorio("Turma/Disciplina de Destino:"), 0, 1);
        comboTurma = new ComboBox<>();
        comboTurma.setPrefWidth(300);
        comboTurma.setPromptText("Escolha a turma...");
        
        // Conversor para mostrar "Turma - Disciplina"
        comboTurma.setConverter(new StringConverter<Turma>() {
            @Override
            public String toString(Turma t) {
                if (t == null) return "";
                String disc = (t.getDisciplina() != null) ? t.getDisciplina().getNome() : "S/ Disc";
                return t.getNome() + " - " + disc;
            }
            @Override
            public Turma fromString(String string) { return null; }
        });
        comboTurma.getItems().addAll(sistema.getTurmas());
        form.add(comboTurma, 1, 1);
    }

    @Override
    protected void onSalvar() throws Exception {
        Aluno aluno = comboAluno.getValue();
        Turma turma = comboTurma.getValue();

        if (aluno == null || turma == null) {
            throw new IllegalArgumentException("Selecione o Aluno e a Turma.");
        }

        // Chama o novo método do Admin
        adminLogado.matricularAlunoEmTurma(aluno, turma);
        sistema.salvarDados();
    }

    @Override
    protected void limparCamposEspecificos() {
        comboAluno.getSelectionModel().clearSelection();
        comboTurma.getSelectionModel().clearSelection();
    }

    // --- TABELA DE CONFERÊNCIA (Mostra todos os alunos para referência) ---
    @Override
    protected TableView<Aluno> criarTabelaRegistros() {
        TableView<Aluno> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Aluno, String> colMat = new TableColumn<>("Matrícula");
        colMat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMatricula()));
        colMat.setPrefWidth(120);

        TableColumn<Aluno, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNome()));

        // Coluna extra para ajudar a ver a turma principal
        TableColumn<Aluno, String> colTurmaOriginal = new TableColumn<>("Turma Original");
        colTurmaOriginal.setCellValueFactory(d -> {
            Turma t = d.getValue().getTurmaAtual();
            return new SimpleStringProperty(t != null ? t.getNome() : "-");
        });

        tabela.getColumns().addAll(colMat, colNome, colTurmaOriginal);
        return tabela;
    }

    @Override
    protected List<Aluno> carregarDadosExistentes() {
        return sistema.getAlunos();
    }
}