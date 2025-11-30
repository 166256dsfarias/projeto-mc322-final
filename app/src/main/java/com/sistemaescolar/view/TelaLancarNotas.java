package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.exceptions.NotaInvalidaException;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class TelaLancarNotas extends TelaBaseGestaoNotas {

    private TextField txtDescricao;
    private Button btnIniciar;

    public TelaLancarNotas(Sistema sistema, Professor professor) {
        super(sistema, professor, "Lançamento de Notas");

        Label lblDesc = new Label("Descrição da Avaliação (ex: Prova 1):");
        txtDescricao = new TextField();
        txtDescricao.setPromptText("Selecione uma turma acima...");
        txtDescricao.setDisable(true);

        btnIniciar = new Button("Carregar Alunos");
        btnIniciar.getStyleClass().addAll("btn", "btn-primary");
        btnIniciar.setDisable(true);
        btnIniciar.setOnAction(e -> carregarAlunosParaLancamento());

        this.areaConteudoEspecifico.getChildren().addAll(lblDesc, txtDescricao, btnIniciar);
    }

    @Override
    protected void onTurmaSelecionada(Turma t) {
        txtDescricao.setDisable(false);
        txtDescricao.requestFocus();
        btnIniciar.setDisable(false);
        areaTabela.setVisible(false);
    }

    private void carregarAlunosParaLancamento() {
        if (txtDescricao.getText().isEmpty()) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Digite uma descrição.");
            return;
        }
        if (turmaSelecionada.getAlunosDaTurma().isEmpty()) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Vazia", "Esta turma não tem alunos.");
            return;
        }

        List<AlunoNotaRow> linhas = new ArrayList<>();
        for (Aluno a : turmaSelecionada.getAlunosDaTurma()) {
            linhas.add(new AlunoNotaRow(a));
        }

        tabelaNotas.setItems(FXCollections.observableArrayList(linhas));
        areaTabela.setVisible(true);
        
        comboTurmas.setDisable(true);
        txtDescricao.setDisable(true);
        btnIniciar.setDisable(true);
    }

    @Override
    protected void onSalvar() {
        String descricao = txtDescricao.getText();
        int salvos = 0;
        StringBuilder erros = new StringBuilder();

        for (AlunoNotaRow linha : tabelaNotas.getItems()) {
            // Não precisa checar null/empty, a base já garantiu!
            String valorStr = linha.getCampoNota().getText();
            
            try {
                double valor = Double.parseDouble(valorStr.replace(",", "."));
                professorLogado.lancarNota(descricao, linha.getAluno(), valor, turmaSelecionada.getDisciplina());
                salvos++;
                
                linha.getCampoNota().setStyle("-fx-border-color: green; -fx-background-color: #e8f5e9;");
                linha.getCampoNota().setDisable(true);
            } catch (Exception e) {
                linha.getCampoNota().setStyle("-fx-border-color: red;");
                if (e instanceof NotaInvalidaException) {
                    erros.append(linha.getAluno().getNome()).append(": ").append(e.getMessage()).append("\n");
                }
            }
        }

        if (salvos > 0) {
            sistema.salvarDados();
            GuiUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", salvos + " notas lançadas!");
            
            areaTabela.setVisible(false);
            comboTurmas.setDisable(false);
            comboTurmas.getSelectionModel().clearSelection();
            txtDescricao.setDisable(false);
            txtDescricao.clear();
            btnIniciar.setDisable(false);
        }
        
        if (erros.length() > 0) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Erros", erros.toString());
        }
    }
}