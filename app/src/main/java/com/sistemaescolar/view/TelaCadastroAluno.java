package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Aluno; 
import com.sistemaescolar.sistema.Sistema;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.util.List;

public class TelaCadastroAluno extends TelaCadastroBase<Aluno> {

    private TextField txtNome;
    private TextField txtCpf;
    private TextField txtEmail;
    private ComboBox<Turma> comboTurma;

    public TelaCadastroAluno(Sistema sistema, Administrador admin) {
        super("Cadastrar Novo Aluno", sistema, admin);
    }

    // --- MÉTODOS DE FORMULÁRIO (ESPECÍFICOS DO ALUNO) ---
    
    @Override
    protected void adicionarCamposAoFormulario(GridPane form) {
        form.add(GuiUtils.criarLabelObrigatorio("Nome Completo:"), 0, 0);
        txtNome = new TextField();
        txtNome.setPromptText("Ex: João da Silva");
        txtNome.setPrefWidth(300);
        form.add(txtNome, 1, 0);

        form.add(GuiUtils.criarLabelObrigatorio("CPF:"), 0, 1);
        txtCpf = new TextField();
        txtCpf.setPromptText("000.000.000-00");
        GuiUtils.configurarMascaraCPF(txtCpf);
        form.add(txtCpf, 1, 1);

        form.add(new Label("E-mail:"), 0, 2);
        txtEmail = new TextField();
        txtEmail.setPromptText("aluno@email.com");
        form.add(txtEmail, 1, 2);

        form.add(GuiUtils.criarLabelObrigatorio("Vincular à Turma:"), 0, 3);
        comboTurma = new ComboBox<>();
        comboTurma.setPromptText("Selecione uma turma...");
        
        // DEBUG
        System.out.println("DEBUG TELA ALUNO: Carregando turmas...");
        if (sistema.getTurmas() == null) {
            System.out.println("ERRO CRÍTICO: A lista de turmas do sistema é NULL!");
        } else {
            System.out.println("Quantidade de Turmas encontradas: " + sistema.getTurmas().size());
            for (Turma t : sistema.getTurmas()) {
                System.out.println(" -> Turma disponível: " + t.getNome() + " (" + t.getAnoLetivo() + ")");
            }
        }
        
        comboTurma.getItems().addAll(sistema.getTurmas());
        
        comboTurma.setConverter(new StringConverter<Turma>() {
            @Override
            public String toString(Turma t) {
                return (t == null) ? "" : t.getNome() + " (" + t.getAnoLetivo() + ")";
            }
            @Override
            public Turma fromString(String string) { return null; }
        });
        form.add(comboTurma, 1, 3);
    }

    @Override
    protected void onSalvar() throws Exception {
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        Turma turmaSelecionada = comboTurma.getValue();

        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome obrigatório.");
        if (turmaSelecionada == null) throw new IllegalArgumentException("Selecione uma Turma.");
        
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) throw new IllegalArgumentException("CPF inválido.");

        adminLogado.cadastrarAluno(sistema, nome, cpf, email, turmaSelecionada);
        sistema.salvarDados();
    }

    @Override
    protected void limparCamposEspecificos() {
        txtNome.clear();
        txtCpf.clear();
        txtEmail.clear();
        comboTurma.getSelectionModel().clearSelection();
    }

    // --- MÉTODOS DE TABELA ---
    
    /**
     * Define as colunas e as regras de exibição para a Tabela de Alunos.
     * Deve ser implementado na classe filha.
     */
    @Override
    protected TableView<Aluno> criarTabelaRegistros() {
        TableView<Aluno> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Aluno, String> colMat = new TableColumn<>("Matrícula");
        colMat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMatricula()));
        colMat.setPrefWidth(120);

        TableColumn<Aluno, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        
        TableColumn<Aluno, String> colTurma = new TableColumn<>("Turma");
        colTurma.setCellValueFactory(data -> {
            Turma t = data.getValue().getTurmaAtual();
            return new SimpleStringProperty(t != null ? t.getNome() + " (" + t.getAnoLetivo() + ")" : "N/A");
        });

        tabela.getColumns().addAll(colMat, colNome, colTurma);
        return tabela;
    }

    /**
     * Retorna a lista de dados a ser exibida na tabela.
     * Deve ser implementado na classe filha.
     */
    @Override
    protected List<Aluno> carregarDadosExistentes() {
        return sistema.getAlunos();
    }
}