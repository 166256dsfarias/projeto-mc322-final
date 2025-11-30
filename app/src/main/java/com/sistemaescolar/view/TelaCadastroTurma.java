package com.sistemaescolar.view;

import com.sistemaescolar.academico.Disciplina;
import com.sistemaescolar.academico.Turma; // Tipo genérico T
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Professor;
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

public class TelaCadastroTurma extends TelaCadastroBase<Turma> {

    private TextField txtNome;
    private TextField txtAno;
    private ComboBox<Disciplina> comboDisciplina;
    private ComboBox<Professor> comboProfessor;

    public TelaCadastroTurma(Sistema sistema, Administrador admin) {
        super("Cadastrar Nova Turma", sistema, admin);
    }

    // --- IMPLEMENTAÇÃO DO FORMULÁRIO ---

    @Override
    protected void adicionarCamposAoFormulario(GridPane form) {
        form.add(GuiUtils.criarLabelObrigatorio("Código/Nome da Turma:"), 0, 0);
        txtNome = new TextField();
        txtNome.setPromptText("Ex: Turma A");
        form.add(txtNome, 1, 0);

        form.add(GuiUtils.criarLabelObrigatorio("Ano Letivo:"), 0, 1);
        txtAno = new TextField();
        txtAno.setPromptText("Ex: 2025");
        GuiUtils.configurarMascaraNumerica(txtAno); // Máscara Numérica
        form.add(txtAno, 1, 1);

        form.add(GuiUtils.criarLabelObrigatorio("Disciplina:"), 0, 2);
        comboDisciplina = new ComboBox<>();
        comboDisciplina.setPromptText("Selecione a disciplina...");
        comboDisciplina.getItems().addAll(sistema.getDisciplinas());
        comboDisciplina.setPrefWidth(300);
        
        // Conversor visual para Disciplina
        comboDisciplina.setConverter(new StringConverter<Disciplina>() {
            @Override
            public String toString(Disciplina d) {
                return (d == null) ? "" : d.getNome() + " (" + d.getCodigo() + ")";
            }
            @Override
            public Disciplina fromString(String string) { return null; }
        });
        form.add(comboDisciplina, 1, 2);

        form.add(GuiUtils.criarLabelObrigatorio("Professor Responsável:"), 0, 3);
        comboProfessor = new ComboBox<>();
        comboProfessor.setPromptText("Selecione o professor...");
        comboProfessor.getItems().addAll(sistema.getProfessores());
        comboProfessor.setPrefWidth(300);

        // Conversor visual para Professor
        comboProfessor.setConverter(new StringConverter<Professor>() {
            @Override
            public String toString(Professor p) {
                return (p == null) ? "" : p.getNome() + " (CPF: " + p.getCpf() + ")";
            }
            @Override
            public Professor fromString(String string) { return null; }
        });
        form.add(comboProfessor, 1, 3);
    }

    @Override
    protected void onSalvar() throws Exception {
        String nome = txtNome.getText();
        String anoStr = txtAno.getText();
        Disciplina disciplina = comboDisciplina.getValue();
        Professor professor = comboProfessor.getValue();

        // Validações
        if (nome.isEmpty() || anoStr.isEmpty() || disciplina == null || professor == null) {
            throw new IllegalArgumentException("Todos os campos são obrigatórios.");
        }

        int ano = Integer.parseInt(anoStr);

        // Chamada ao Administrador
        adminLogado.cadastrarTurma(sistema, nome, ano, disciplina, professor);
        sistema.salvarDados();
    }

    @Override
    protected void limparCamposEspecificos() {
        txtNome.clear();
        txtAno.clear();
        comboDisciplina.getSelectionModel().clearSelection();
        comboProfessor.getSelectionModel().clearSelection();
    }

    // --- IMPLEMENTAÇÃO DA TABELA  ---
    
    /**
     * Define as colunas e as regras de exibição para a Tabela de Turmas.
     */
    @Override
    protected TableView<Turma> criarTabelaRegistros() {
        TableView<Turma> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Turma, String> colNome = new TableColumn<>("Código");
        colNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNome()));
        
        TableColumn<Turma, String> colAno = new TableColumn<>("Ano");
        colAno.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAnoLetivo())));
        
        TableColumn<Turma, String> colDisc = new TableColumn<>("Disciplina");
        colDisc.setCellValueFactory(data -> {
            Disciplina d = data.getValue().getDisciplina();
            // Proteção contra NullPointer 
            return new SimpleStringProperty(d != null ? d.getNome() : "N/A");
        });
        
        TableColumn<Turma, String> colProf = new TableColumn<>("Professor");
        colProf.setCellValueFactory(data -> {
            Professor p = data.getValue().getProfessor();
            return new SimpleStringProperty(p != null ? p.getNome() : "N/A");
        });

        tabela.getColumns().addAll(colNome, colAno, colDisc, colProf);
        return tabela;
    }

    /**
     * Retorna a lista de dados a ser exibida na tabela.
     */
    @Override
    protected List<Turma> carregarDadosExistentes() {
        return sistema.getTurmas();
    }
}