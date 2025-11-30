package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.pessoas.Aluno;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors; 

public class TelaRealizarChamada extends TelaBaseFrequencia {

    public TelaRealizarChamada(Sistema sistema, Professor professor) {
        super("Realizar Chamada", sistema, professor);
        this.btnSalvarChamada.setText("REGISTRAR NOVA CHAMADA");
    }

    @Override
    protected List<AlunoFrequenciaRow> carregarDadosDaTurma(Turma turma, LocalDate data) {
        List<Aluno> alunos = turma.getAlunosDaTurma();

        boolean frequenciaJaExiste = alunos.stream()
            .anyMatch(aluno -> sistema.frequenciaExiste(aluno, turma.getDisciplina(), data));

        if (frequenciaJaExiste) {
            throw new IllegalArgumentException("Chamada para esta data (" + GuiUtils.formatarData(data) + ") já foi realizada. Use a tela 'Corrigir Frequência'.");
        }
        
        return alunos.stream()
                .map(AlunoFrequenciaRow::new)
                .collect(Collectors.toList()); // <--- CRIA UMA LISTA MUTÁVEL (EDITÁVEL)
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
}