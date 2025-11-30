package com.sistemaescolar.view;

import com.sistemaescolar.academico.Disciplina; // Define o tipo T para esta tela
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.util.List;

public class TelaCadastroDisciplina extends TelaCadastroBase<Disciplina> {

    private TextField txtCodigo;
    private TextField txtNome;

    public TelaCadastroDisciplina(Sistema sistema, Administrador admin) {
        super("Cadastrar Nova Disciplina", sistema, admin);
    }

    // --- IMPLEMENTAÇÃO DO FORMULÁRIO ---

    @Override
    protected void adicionarCamposAoFormulario(GridPane form) {
        form.add(GuiUtils.criarLabelObrigatorio("Código da Disciplina:"), 0, 0);
        txtCodigo = new TextField();
        txtCodigo.setPromptText("Ex: MAT001");
        form.add(txtCodigo, 1, 0);

        form.add(GuiUtils.criarLabelObrigatorio("Nome da Disciplina:"), 0, 1);
        txtNome = new TextField();
        txtNome.setPromptText("Ex: Matemática Básica");
        txtNome.setPrefWidth(300);
        form.add(txtNome, 1, 1);
    }

    @Override
    protected void onSalvar() throws Exception {
        String codigo = txtCodigo.getText();
        String nome = txtNome.getText();

        if (codigo.isEmpty() || nome.isEmpty()) {
            throw new IllegalArgumentException("Preencha o código e o nome.");
        }

        // 'adminLogado' e 'sistema' vêm da classe pai
        adminLogado.cadastrarDisciplina(sistema, codigo, nome);
        sistema.salvarDados(); // Persistência
    }

    @Override
    protected void limparCamposEspecificos() {
        txtCodigo.clear();
        txtNome.clear();
    }

    // --- IMPLEMENTAÇÃO DA TABELA  ---
    
    /**
     * Define as colunas e as regras de exibição para a Tabela de Disciplinas.
     */
    @Override
    protected TableView<Disciplina> criarTabelaRegistros() {
        TableView<Disciplina> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Disciplina, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colCodigo.setPrefWidth(100);

        TableColumn<Disciplina, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        
        tabela.getColumns().addAll(colCodigo, colNome);
        return tabela;
    }

    /**
     * Retorna a lista de dados a ser exibida na tabela.
     */
    @Override
    protected List<Disciplina> carregarDadosExistentes() {
        return sistema.getDisciplinas();
    }
}