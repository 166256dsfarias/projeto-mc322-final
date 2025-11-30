package com.sistemaescolar.sistema;

import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Pessoa; 
import com.sistemaescolar.view.TelaLogin;
import javafx.application.Application;

public class Main {

    private static Sistema sistema = Sistema.carregarDados();
    
    private static Pessoa usuarioLogado = null;

    public static Sistema getSistema() {
        return sistema;
    }

    // Ajustado para receber Pessoa
    public static void setUsuarioLogado(Pessoa u) {
        usuarioLogado = u;
    }

    public static void main(String[] args) {
        
        // Verifica se precisa criar o Admin padrão, ou seja, primeiro uso
        if (sistema.getAdministradores().isEmpty()) {
            System.out.println("Sistema vazio detectado. Criando Administrador Inicial...");
            
            Administrador adminInicial = new Administrador(
                "Admin", 
                "000.000.000-00", 
                "admin@escola.com", 
                "Mudar@123", 
                "Administrador", 
                5000.0f, 
                "ADM001"
            );
            
            sistema.cadastrarAdministrador(adminInicial);
            sistema.salvarDados(); // Salva o estado inicial
        }
        
        // Sincroniza os IDs automáticos com o que foi carregado
        GeradorDeCredenciais.inicializarContadores(sistema);

        System.out.println("Iniciando interface gráfica...");
        
        // Lança a aplicação JavaFX
        try {
            Application.launch(TelaLogin.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Salva ao fechar a janela
        sistema.salvarDados();
        System.out.println("Aplicação encerrada e dados salvos.");
    }

}