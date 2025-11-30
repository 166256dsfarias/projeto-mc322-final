package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Funcionario; // Usamos Funcionario como tipo genérico
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.List;

public class TelaCadastroFuncionario extends TelaCadastroBase<Funcionario> {

    private TextField txtNome;
    private TextField txtCpf;
    private TextField txtEmail;
    private TextField txtSalario;
    private ComboBox<String> comboTipo;

    public TelaCadastroFuncionario(Sistema sistema, Administrador admin) {
        super("Cadastrar Novo Funcionário", sistema, admin);
    }

    // --- IMPLEMENTAÇÃO DO FORMULÁRIO ---

    @Override
    protected void adicionarCamposAoFormulario(GridPane form) {
        form.add(GuiUtils.criarLabelObrigatorio("Tipo de Cargo:"), 0, 0);
        comboTipo = new ComboBox<>();
        comboTipo.getItems().addAll("Professor", "Administrador");
        comboTipo.setPromptText("Selecione o cargo...");
        comboTipo.setPrefWidth(300);
        form.add(comboTipo, 1, 0);

        form.add(GuiUtils.criarLabelObrigatorio("Nome Completo:"), 0, 1);
        txtNome = new TextField();
        txtNome.setPromptText("Ex: Fulano da Silva");
        txtNome.setPrefWidth(300);
        form.add(txtNome, 1, 1);

        form.add(GuiUtils.criarLabelObrigatorio("CPF:"), 0, 2);
        txtCpf = new TextField();
        txtCpf.setPromptText("000.000.000-00");
        GuiUtils.configurarMascaraCPF(txtCpf);
        form.add(txtCpf, 1, 2);

        form.add(new Label("E-mail:"), 0, 3);
        txtEmail = new TextField();
        txtEmail.setPromptText("funcionario@escola.com");
        form.add(txtEmail, 1, 3);

        form.add(GuiUtils.criarLabelObrigatorio("Salário (R$):"), 0, 4);
        txtSalario = new TextField();
        txtSalario.setPromptText("R$ 0,00");
        GuiUtils.configurarMascaraMoeda(txtSalario);
        form.add(txtSalario, 1, 4);
    }

    @Override
    protected void onSalvar() throws Exception {
        String tipo = comboTipo.getValue();
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String salarioStr = txtSalario.getText();
        String email = txtEmail.getText();

        // Validações de UI
        if (tipo == null) throw new IllegalArgumentException("Selecione o cargo.");
        if (nome.trim().isEmpty()) throw new IllegalArgumentException("Nome obrigatório.");
        
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) throw new IllegalArgumentException("CPF inválido.");
        
        String apenasNumeros = salarioStr.replaceAll("[^0-9]", "");
        if (apenasNumeros.isEmpty()) throw new IllegalArgumentException("Salário inválido.");
        
        double valorSalario = Double.parseDouble(apenasNumeros) / 100.0;
        if (valorSalario <= 0) throw new IllegalArgumentException("Salário deve ser maior que zero.");
        
        float salario = (float) valorSalario;

        // Chamada ao Administrador 
        if (tipo.equals("Professor")) {
            adminLogado.cadastrarProfessor(sistema, nome, cpf, email, salario);
        } else { 
            adminLogado.cadastrarAdministrador(sistema, nome, cpf, email, salario);
        }
        
        sistema.salvarDados();
    }

    @Override
    protected void limparCamposEspecificos() {
        txtNome.clear();
        txtCpf.clear();
        txtEmail.clear();
        txtSalario.clear();
        comboTipo.getSelectionModel().clearSelection();
    }

    // --- IMPLEMENTAÇÃO DA TABELA  ---
    
    /**
     * Define as colunas e as regras de exibição para a Tabela de Funcionários.
     */
    @Override
    protected TableView<Funcionario> criarTabelaRegistros() {
        TableView<Funcionario> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Funcionario, String> colCargo = new TableColumn<>("Cargo");
        colCargo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCargo()));
        
        TableColumn<Funcionario, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getID()));
        
        TableColumn<Funcionario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        
        TableColumn<Funcionario, String> colCPF = new TableColumn<>("CPF");
        colCPF.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCpf()));

        tabela.getColumns().addAll(colCargo, colID, colNome, colCPF);
        return tabela;
    }

    /**
     * Retorna a lista de dados a ser exibida na tabela.
     * Consolida Admins e Professores.
     */
    @Override
    protected List<Funcionario> carregarDadosExistentes() {
        List<Funcionario> todosFuncionarios = new ArrayList<>();
        // Adiciona Admin e Professor (ambos são Funcionario)
        todosFuncionarios.addAll(sistema.getProfessores());
        todosFuncionarios.addAll(sistema.getAdministradores());
        return todosFuncionarios;
    }
}