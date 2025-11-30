package com.sistemaescolar.view;

import com.sistemaescolar.academico.Nota;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.exceptions.NotaInvalidaException;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TelaAtualizarNotas extends TelaBaseGestaoNotas {

    private ComboBox<String> cmbDescricoes;
    private Button btnCarregar;

    public TelaAtualizarNotas(Sistema sistema, Professor professor) {
        super(sistema, professor, "Atualização de Notas");

        Label lblDesc = new Label("2. Selecione a Avaliação para corrigir:");
        
        cmbDescricoes = new ComboBox<>();
        cmbDescricoes.setPromptText("Selecione uma turma primeiro...");
        cmbDescricoes.setPrefWidth(300);
        cmbDescricoes.setDisable(true);

        btnCarregar = new Button("Carregar Notas dos Alunos");
        btnCarregar.getStyleClass().addAll("btn", "btn-info");
        btnCarregar.setDisable(true);

        cmbDescricoes.setOnAction(e -> {
            if (cmbDescricoes.getValue() != null) btnCarregar.setDisable(false);
        });

        btnCarregar.setOnAction(e -> carregarNotasParaEdicao());

        this.areaConteudoEspecifico.getChildren().addAll(lblDesc, cmbDescricoes, btnCarregar);
    }

    @Override
    protected void onTurmaSelecionada(Turma t) {
        carregarDescricoesDaTurma(t);
        areaTabela.setVisible(false);
        btnCarregar.setDisable(true);
    }

    @Override
    protected void onSalvar() {
        salvarAlteracoes();
    }

    private void carregarDescricoesDaTurma(Turma t) {
        Set<String> descricoesUnicas = new HashSet<>();

        for (Aluno aluno : t.getAlunosDaTurma()) {
            for (Nota nota : aluno.getBoletim()) {
                if (nota.getDisciplina() != null && t.getDisciplina() != null &&
                    nota.getDisciplina().getCodigo().equals(t.getDisciplina().getCodigo())) {
                    descricoesUnicas.add(nota.getDescricao());
                }
            }
        }

        cmbDescricoes.setItems(FXCollections.observableArrayList(descricoesUnicas));
        
        if (descricoesUnicas.isEmpty()) {
            cmbDescricoes.setPromptText("Nenhuma nota lançada nesta turma.");
            cmbDescricoes.setDisable(true);
        } else {
            cmbDescricoes.setPromptText("Selecione a avaliação...");
            cmbDescricoes.setDisable(false);
        }
    }

    private void carregarNotasParaEdicao() {
        String descricaoAlvo = cmbDescricoes.getValue();
        if (descricaoAlvo == null) return;

        List<AlunoNotaRow> linhas = new ArrayList<>();

        for (Aluno aluno : turmaSelecionada.getAlunosDaTurma()) {
            double notaExistente = -1; 

            for (Nota n : aluno.getBoletim()) {
                if (n.getDescricao().equals(descricaoAlvo) && 
                    n.getDisciplina().getCodigo().equals(turmaSelecionada.getDisciplina().getCodigo())) {
                    notaExistente = n.getValor();
                    break;
                }
            }

            AlunoNotaRow row = new AlunoNotaRow(aluno);
            
            if (notaExistente >= 0) {
                row.setValorInicial(notaExistente);
            } else {
                row.getCampoNota().clear();
            }
            
            linhas.add(row);
        }

        tabelaNotas.setItems(FXCollections.observableArrayList(linhas));
        areaTabela.setVisible(true);
        
        comboTurmas.setDisable(true);
        cmbDescricoes.setDisable(true);
        btnCarregar.setDisable(true);
    }

    private void salvarAlteracoes() {
        String descricaoAlvo = cmbDescricoes.getValue();
        int atualizados = 0;
        StringBuilder erros = new StringBuilder();

        for (AlunoNotaRow linha : tabelaNotas.getItems()) {
            // A validação de vazio já foi feita na base
            String valorStr = linha.getCampoNota().getText();
            
            try {
                double novoValor = Double.parseDouble(valorStr.replace(",", "."));
                boolean encontrou = false;
                
                for (Nota n : linha.getAluno().getBoletim()) {
                    if (n.getDescricao().equals(descricaoAlvo) && 
                        n.getDisciplina().getCodigo().equals(turmaSelecionada.getDisciplina().getCodigo())) {
                        n.setValor(novoValor); 
                        encontrou = true;
                        break;
                    }
                }

                if (!encontrou) {
                    professorLogado.lancarNota(descricaoAlvo, linha.getAluno(), novoValor, turmaSelecionada.getDisciplina());
                }

                atualizados++;
                linha.getCampoNota().setStyle("-fx-border-color: green; -fx-background-color: #e8f5e9;");

            } catch (Exception e) {
                linha.getCampoNota().setStyle("-fx-border-color: red;");
                if (e instanceof NotaInvalidaException) {
                    erros.append(linha.getAluno().getNome()).append(": ").append(e.getMessage()).append("\n");
                }
            }
        }

        if (atualizados > 0) {
            sistema.salvarDados();
            GuiUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", atualizados + " notas atualizadas!");
            
            areaTabela.setVisible(false);
            comboTurmas.setDisable(false);
            cmbDescricoes.setDisable(false);
            cmbDescricoes.getSelectionModel().clearSelection();
            btnCarregar.setDisable(true);
        }

        if (erros.length() > 0) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Erros", erros.toString());
        }
    }
}