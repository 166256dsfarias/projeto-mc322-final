package com.sistemaescolar.academico;

import java.io.Serializable;

public class Disciplina implements Serializable {
    private String codigo;
    private String nome;

    public Disciplina(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    //Getters
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    
    //Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    @Override
    public String toString() {
        return nome + " (" + codigo + ")";
    }
}