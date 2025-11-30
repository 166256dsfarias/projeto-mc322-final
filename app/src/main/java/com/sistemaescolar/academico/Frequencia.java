package com.sistemaescolar.academico;

import com.sistemaescolar.pessoas.Aluno;

import java.io.Serializable;
import java.time.LocalDate;

import com.sistemaescolar.academico.Disciplina;

public class Frequencia implements Serializable {
    private Disciplina disciplina;
    private Aluno aluno;
    private boolean presente;
    private LocalDate dataAula;

    public Frequencia(Disciplina disciplina, Aluno aluno, boolean presente, LocalDate dataAula) {
        this.disciplina = disciplina;
        this.aluno = aluno;
        this.presente = presente;
        this.dataAula = dataAula;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public boolean isPresente() {
        return presente;
    }

    public String presenteOuAusente() {
        if (presente) {
            return "Presente";
        }
        return "Ausente";
    }

    public LocalDate getDataAula() {
        return dataAula;
    }

    public void setPresente(boolean presente) {
        this.presente = presente;
    }

}
