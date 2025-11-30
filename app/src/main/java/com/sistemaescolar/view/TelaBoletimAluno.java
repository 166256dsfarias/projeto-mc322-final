package com.sistemaescolar.view;

import com.sistemaescolar.academico.Nota;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class TelaBoletimAluno extends TelaBaseRelatorioAluno<Nota> {

    public TelaBoletimAluno(Sistema sistema, Aluno aluno) {
        super("Meu Boletim de Notas", sistema, aluno);
    }

    @Override
    protected TableView<Nota> criarTabela() {
        TableView<Nota> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Nota, String> colDesc = new TableColumn<>("Avaliação");
        colDesc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescricao()));

        TableColumn<Nota, String> colNota = new TableColumn<>("Nota");
        colNota.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.1f", data.getValue().getValor())));
        colNota.setStyle("-fx-alignment: CENTER-RIGHT;");

        tabela.getColumns().addAll(colDesc, colNota);
        return tabela;
    }

    @Override
    protected List<Nota> carregarDados(Turma turma) {
        List<Nota> notasDaTurma = new ArrayList<>();
        // Filtra no boletim geral do aluno apenas as notas desta turma/disciplina
        for (Nota n : alunoLogado.getBoletim()) {
            if (n.getDisciplina() != null && turma.getDisciplina() != null &&
                n.getDisciplina().getCodigo().equals(turma.getDisciplina().getCodigo())) {
                notasDaTurma.add(n);
            }
        }
        return notasDaTurma;
    }

    @Override
    protected String calcularResumo(List<Nota> dados) {
        if (dados.isEmpty()) return "Média Final: -";

        double soma = 0;
        for (Nota n : dados) {
            soma += n.getValor();
        }
        
        // Média Aritmética Simples
        double media = soma / dados.size();
        
        String cor = (media >= 6.0) ? "green" : "red"; // Regra de negócio visual
        lblResumo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + cor + ";");
        
        return String.format("Média: %.1f", media);
    }
}