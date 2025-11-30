package com.sistemaescolar.pessoas;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.exceptions.NotaInvalidaException;
import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.academico.Disciplina;
import com.sistemaescolar.academico.Frequencia;
import com.sistemaescolar.academico.Nota;

public class Professor extends Funcionario {
    private List<Disciplina> disciplinasLecionadas;

    public Professor(String nome, String cpf, String email, String senha, String cargo, float salario, String ID) {
        super(nome, cpf, email, senha, cargo, salario, ID);
        this.disciplinasLecionadas = new ArrayList<>();
    }

    public void lancarNota(String descricao, Aluno aluno, double valor, Disciplina disciplina) throws NotaInvalidaException {
        Nota novaNota = new Nota(descricao, valor, aluno, disciplina);
        aluno.registrarNota(novaNota);
    }

    public boolean atualizarNota(String descricao, Aluno aluno, Disciplina disciplina, double novoValor) throws NotaInvalidaException {
        List<Nota> notasDoAluno = aluno.getBoletim();
        
        for (Nota nota : notasDoAluno) {
            // Compara por descrição e disciplina
            if (nota.getDescricao().equals(descricao) && 
                (nota.getDisciplina() != null && nota.getDisciplina().getCodigo().equals(disciplina.getCodigo()))) {
                
                nota.setValor(novoValor); // O setValor da Nota já valida se é <0 ou >10
                return true; 
            }
        }
        return false; // Não encontrou a nota (A View decide o que fazer)
    }

    public void enviarAviso(Turma turma, String mensagem) {
        turma.adicionarRecado(mensagem);
    }

    public void novaDisciplinaLecionada(Disciplina novaDisciplina) {
        disciplinasLecionadas.add(novaDisciplina);
    }

    public List<Disciplina> getDisciplinasLecionadas() {
        return disciplinasLecionadas;
    }
    
    public void fazerChamada(Turma turma, Disciplina disciplina, List<Aluno> presentes) {
        java.time.LocalDate data = java.time.LocalDate.now();
        
        for (Aluno aluno : turma.getAlunosDaTurma()) {
            // Se o aluno está na lista de presentes, marca true. Se não, false.
            boolean estevePresente = presentes.contains(aluno);
            lancarFrequencia(aluno, disciplina, estevePresente, data);
        }
    }

    public void lancarFrequencia(Aluno aluno, Disciplina disciplina, boolean presente, java.time.LocalDate data) {
        Frequencia novaFrequencia = new Frequencia(disciplina, aluno, presente, data);
        aluno.registrarFrequencia(novaFrequencia);
    }
    
    // Atualizar Frequência também perde o console
    public void atualizarFrequencia(java.time.LocalDate data, Aluno aluno, Disciplina disciplina, boolean novaPresenca) {
        // Lógica de busca e atualização 
        for (Frequencia f : aluno.getHistoricoDeFalta()) {
            if (f.getDataAula().equals(data) && f.getDisciplina().getCodigo().equals(disciplina.getCodigo())) {
                f.setPresente(novaPresenca);
                return;
            }
        }
    }

    public void registrarFrequencia(Aluno aluno, Disciplina disciplina, LocalDate dataAula, boolean presente) {
    // Tenta encontrar a frequencia existente 
    Frequencia freqExistente = aluno.getHistoricoDeFalta().stream()
        .filter(f -> f.getDisciplina().equals(disciplina) && f.getDataAula().equals(dataAula))
        .findFirst()
        .orElse(null);

        if (freqExistente != null) {
            // Caso de CORREÇÃO (atualiza o registro)
            freqExistente.setPresente(presente);
        } else {
            // Caso de NOVO REGISTRO (realizar Chamada)
            Frequencia novaFreq = new Frequencia(disciplina, aluno, presente, dataAula);
            aluno.registrarFrequencia(novaFreq);
        }
    }
}
