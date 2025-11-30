package com.sistemaescolar.view;

import com.sistemaescolar.academico.Frequencia;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class TelaFrequenciaAluno extends TelaBaseRelatorioAluno<Frequencia> {

    public TelaFrequenciaAluno(Sistema sistema, Aluno aluno) {
        super("Meu Histórico de Frequência", sistema, aluno);
    }

    @Override
    protected TableView<Frequencia> criarTabela() {
        TableView<Frequencia> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Frequencia, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(data -> new SimpleStringProperty(GuiUtils.formatarData(data.getValue().getDataAula())));

        TableColumn<Frequencia, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(data -> {
            boolean presente = data.getValue().isPresente();
            return new SimpleStringProperty(presente ? "PRESENTE" : "FALTA");
        });
        
        // Pinta a célula de verde ou vermelho
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<Frequencia, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("PRESENTE")) {
                        setTextFill(javafx.scene.paint.Color.GREEN);
                    } else {
                        setTextFill(javafx.scene.paint.Color.RED);
                        setStyle("-fx-font-weight: bold;");
                    }
                }
            }
        });

        tabela.getColumns().addAll(colData, colStatus);
        return tabela;
    }

    @Override
    protected List<Frequencia> carregarDados(Turma turma) {
        List<Frequencia> freqDaTurma = new ArrayList<>();
        for (Frequencia f : alunoLogado.getHistoricoDeFalta()) {
            if (f.getDisciplina() != null && turma.getDisciplina() != null &&
                f.getDisciplina().getCodigo().equals(turma.getDisciplina().getCodigo())) {
                freqDaTurma.add(f);
            }
        }
        return freqDaTurma;
    }

    @Override
    protected String calcularResumo(List<Frequencia> dados) {
        if (dados.isEmpty()) return "Presença: -";

        long totalAulas = dados.size();
        long presencas = dados.stream().filter(Frequencia::isPresente).count();
        
        double porcentagem = (double) presencas / totalAulas * 100.0;

        String cor = (porcentagem >= 75.0) ? "green" : "red"; // Alerta de reprovação por falta
        lblResumo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + cor + ";");

        return String.format("Presença: %.0f%% (%d de %d aulas)", porcentagem, presencas, totalAulas);
    }
}