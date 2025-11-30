package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.sistema.Sistema;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public abstract class TelaCadastroBase<T> extends VBox {

    protected GridPane formulario;
    protected TableView<T> tabelaRegistros;
    protected Sistema sistema;
    protected Administrador adminLogado;

    // --- MÉTODOS ABSTRATOS ---
    protected abstract TableView<T> criarTabelaRegistros();
    protected abstract List<T> carregarDadosExistentes();
    protected abstract void adicionarCamposAoFormulario(GridPane form);
    protected abstract void onSalvar() throws Exception; 
    protected abstract void limparCamposEspecificos();

    public TelaCadastroBase(String tituloTela, Sistema sistema, Administrador admin) {
        this.sistema = sistema;
        this.adminLogado = admin;

        this.setPadding(new Insets(30));
        this.setSpacing(20); // Espaço entre o Título, o Form e a Tabela
        this.setAlignment(Pos.TOP_LEFT);

        // título Padronizado
        Label lblTitulo = new Label(tituloTela);
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblTitulo.getStyleClass().add("text-primary");

        // Formulário
        VBox boxForm = new VBox(15);
        formulario = new GridPane();
        formulario.setHgap(15);
        formulario.setVgap(15);
        
        adicionarCamposAoFormulario(formulario);

        Button btnSalvar = new Button("Salvar");
        btnSalvar.getStyleClass().addAll("btn", "btn-success", "btn-lg");
        btnSalvar.setOnAction(e -> executarSalvamentoSeguro());
        
        boxForm.getChildren().addAll(formulario, btnSalvar);
        
        // TABELA DE REGISTROS (Abaixo do Formulário)
        tabelaRegistros = criarTabelaRegistros();
        
        // Define altura para o limite de 5 linhas visíveis e ativa o scroll
        tabelaRegistros.setPrefHeight(170); 
        tabelaRegistros.setPrefWidth(600); // Garante que a tabela tenha largura

        VBox boxTabela = new VBox(5, new Label("Registros Existentes:"), tabelaRegistros);
        
        // FINAL LAYOUT: Empilha [Título, Formulário, Tabela]
        this.getChildren().addAll(lblTitulo, boxForm, boxTabela);
        
        carregarTabelaRegistros();
    }

    // --- LÓGICA DE CARREGAMENTO DE DADOS ---
    protected void carregarTabelaRegistros() {
        List<T> dados = carregarDadosExistentes();
        tabelaRegistros.setItems(FXCollections.observableArrayList(dados));
        tabelaRegistros.setPlaceholder(new Label("Nenhum registro encontrado."));
    }

    // --- LÓGICA DE SALVAMENTO SEGURA ---
    private void executarSalvamentoSeguro() {
        try {
            onSalvar(); 
            
            // Refresca a tabela após o salvamento
            carregarTabelaRegistros(); 
            
            GuiUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cadastro realizado com sucesso!");
            limparCamposEspecificos();
            
        } catch (IllegalArgumentException ex) {
            GuiUtils.mostrarAlerta(Alert.AlertType.WARNING, "Dados Inválidos", ex.getMessage());
        } catch (Exception ex) {
            GuiUtils.mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao cadastrar: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}