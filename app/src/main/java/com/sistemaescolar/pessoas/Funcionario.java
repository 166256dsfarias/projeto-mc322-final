package com.sistemaescolar.pessoas;

public abstract class Funcionario extends Pessoa{
    private String cargo;
    private float salario;
    private String ID;
    public Funcionario(String nome, String cpf, String email, String senha, String cargo, float salario, String ID) {
        super(nome, cpf, email, senha);
        this.cargo = cargo;
        this.salario = salario;
        this.ID = ID;
    }
    public String getCargo() {
        return cargo;
    }
    public float getSalario() {
        return salario;
    }
    public String getID() {
        return ID;
    }
    
}
