package com.sistemaescolar.academico;

import java.io.Serializable;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.exceptions.NotaInvalidaException; // Importante

public class Nota implements Serializable {
    private String descricao;
    private double valor;
    private Aluno aluno;
    private Disciplina disciplina;

    // Lançamento exceção se o valor for ruim
    public Nota(String descricao, double valor, Aluno aluno, Disciplina disciplina) throws NotaInvalidaException {
        this.descricao = descricao;
        this.aluno = aluno;
        this.disciplina = disciplina;
        setValor(valor); // Usando o setter para validar
    }

    // Getters
    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }
    public Aluno getAluno() { return aluno; }
    public Disciplina getDisciplina() { return disciplina; }

    // Setters
    public void setValor(double valor) throws NotaInvalidaException {
        if (valor < 0 || valor > 10) {
            throw new NotaInvalidaException("A nota deve ser entre 0.0 e 10.0.");
        }
        this.valor = valor;
    }
}