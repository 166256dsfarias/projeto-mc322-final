package com.sistemaescolar.pessoas;

import java.io.Serializable;

import com.sistemaescolar.sistema.GeradorDeCredenciais;

public abstract class Pessoa implements Serializable {
    private String nome;
    private String cpf;
    private String email;
    private String senha;

    public Pessoa(String nome, String cpf, String email, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public boolean alterarSenha(String senhaAtual, String novaSenha) {
        if (!this.senha.equals(senhaAtual)) {
            System.out.println("Erro: Senha atual incorreta");
            return false;
        }

        if (!GeradorDeCredenciais.validarSenha(novaSenha)) {
            System.out.println("Erro: Senha invalida");
            return false;
        }
        senha = novaSenha;
        System.out.println("Senha alterada com sucesso!");
        return true;
    }

    public boolean verificarSenha(String senha) {
        return this.senha.equals(senha);
    }

    public boolean autenticar(String email, String senha) {
        return false;
    }
}
