package com.sistemaescolar.academico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Professor;

public class Turma implements Serializable {
    private String nome;
    private int anoLetivo;
    private List<Aluno> alunosDaTurma;
    private List<String> recados; // Para os avisos do professor

    // --- NOVOS ATRIBUTOS NECESSÁRIOS PARA A TELA ---
    private Disciplina disciplina;
    private Professor professor;

    public Turma(String nome, int anoLetivo) {
        this.nome = nome;
        this.anoLetivo = anoLetivo;
        this.alunosDaTurma = new ArrayList<>();
        this.recados = new ArrayList<>();
    }

    // --- GETTERS E SETTERS NOVOS (O que faltava!) ---
    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public List<String> getRecados() {
        return recados;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    // --- MÉTODOS EXISTENTES ---
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getAnoLetivo() {
        return anoLetivo;
    }

    public void setAnoLetivo(int anoLetivo) {
        this.anoLetivo = anoLetivo;
    }

    public List<Aluno> getAlunosDaTurma() {
        return alunosDaTurma;
    }

    public void adicionarAluno(Aluno aluno) {
        this.alunosDaTurma.add(aluno);
    }
    
    public void adicionarRecado(String msg) {
        this.recados.add(msg);
    }
}