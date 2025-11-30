package com.sistemaescolar.view;

import com.sistemaescolar.academico.Frequencia;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors; 

public class TelaCorrigirFrequencia extends TelaBaseFrequencia {

    public TelaCorrigirFrequencia(Sistema sistema, Professor professor) {
        super("Corrigir Frequência Anterior", sistema, professor);
        this.btnSalvarChamada.setText("ATUALIZAR CHAMADA");
        this.btnSalvarChamada.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
    }

    @Override
    protected List<AlunoFrequenciaRow> carregarDadosDaTurma(Turma turma, LocalDate data) {
        List<Aluno> alunos = turma.getAlunosDaTurma();

        boolean existeChamada = alunos.stream()
                .anyMatch(a -> sistema.frequenciaExiste(a, turma.getDisciplina(), data));

        if (!existeChamada) {
            throw new IllegalArgumentException("Não foi encontrada nenhuma chamada realizada no dia " + 
                                             GuiUtils.formatarData(data) + " para esta turma.");
        }

        return alunos.stream().map(aluno -> {
            AlunoFrequenciaRow row = new AlunoFrequenciaRow(aluno);
            boolean estavaPresente = buscarStatusAnterior(aluno, turma, data);
            row.getPresente().setSelected(estavaPresente);
            return row;
        }).collect(Collectors.toList()); // <--- CRIA LISTA MUTÁVEL
    }

    @Override
    protected void salvarFrequencia(List<AlunoFrequenciaRow> dados) {
        Turma turma = turmaSelecionada;
        LocalDate data = dpDataAula.getValue();
        if (turma == null) throw new IllegalStateException("Turma não selecionada.");

        for (AlunoFrequenciaRow row : dados) {
            professorLogado.registrarFrequencia(row.getAluno(), turma.getDisciplina(), data, row.getPresente().isSelected());
        }
    }

    private boolean buscarStatusAnterior(Aluno aluno, Turma turma, LocalDate data) {
        if (aluno.getHistoricoDeFalta() == null) return false;
        for (Frequencia f : aluno.getHistoricoDeFalta()) {
            if (f.getDisciplina().getCodigo().equals(turma.getDisciplina().getCodigo()) && 
                f.getDataAula().equals(data)) {
                return f.isPresente();
            }
        }
        return false;
    }
}