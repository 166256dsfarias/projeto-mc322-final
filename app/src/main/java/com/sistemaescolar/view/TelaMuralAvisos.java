package com.sistemaescolar.view;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Pessoa;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.sistema.Sistema;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelaMuralAvisos extends VBox {

    private Sistema sistema;
    private Pessoa usuarioLogado;
    private Turma turmaSelecionada;

    private ComboBox<Turma> comboTurmas;
    private TextArea txtMensagem;
    private Button btnEnviar;
    private ListView<String> listaHistorico;

    public TelaMuralAvisos(Sistema sistema, Pessoa usuario) {
        this.sistema = sistema;
        this.usuarioLogado = usuario;

        this.setPadding(new Insets(30));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_LEFT);

        // Título
        Label lblTitulo = new Label("Mural de Avisos");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblTitulo.getStyleClass().add("text-primary");

        // Seleção de Turma (Para TODOS)
        Label lblSelect = new Label("Selecione a Turma:");
        configurarComboTurmas();

        // Área de Envio (Apenas Professor)
        VBox areaEnvio = new VBox(10);
        
        if (usuario instanceof Professor) {
            Label lblMsg = new Label("Nova Mensagem:");
            txtMensagem = new TextArea();
            txtMensagem.setPromptText("Digite o aviso aqui...");
            txtMensagem.setPrefHeight(80);
            txtMensagem.setWrapText(true);
            txtMensagem.setDisable(true); 

            btnEnviar = new Button("ENVIAR AVISO");
            btnEnviar.getStyleClass().addAll("btn", "btn-primary");
            btnEnviar.setDisable(true);
            btnEnviar.setOnAction(e -> enviarAviso());

            areaEnvio.getChildren().addAll(lblMsg, txtMensagem, btnEnviar);
        }

        // Histórico
        Label lblHist = new Label("Últimos Avisos:");
        listaHistorico = new ListView<>();
        listaHistorico.setPrefHeight(300);
        listaHistorico.setPlaceholder(new Label("Selecione uma turma para ver os avisos."));

        // Adiciona tudo na tela
        this.getChildren().addAll(lblTitulo, lblSelect, comboTurmas, areaEnvio, lblHist, listaHistorico);
    }

    private void configurarComboTurmas() {
        comboTurmas = new ComboBox<>();
        comboTurmas.setPrefWidth(400);
        
        // Formatação do texto no Combo
        comboTurmas.setConverter(new StringConverter<Turma>() {
            @Override
            public String toString(Turma t) {
                if (t == null) return null;
                String disc = (t.getDisciplina() != null) ? t.getDisciplina().getNome() : "S/ Disc";
                return String.format("%s (%d) - %s", t.getNome(), t.getAnoLetivo(), disc);
            }
            @Override
            public Turma fromString(String string) { return null; }
        });

        // --- LÓGICA DE CARREGAMENTO INTELIGENTE ---
        List<Turma> turmasVisiveis = new ArrayList<>();

        if (usuarioLogado instanceof Professor) {
            // PROFESSOR Vê as turmas que ele dá aula
            Professor prof = (Professor) usuarioLogado;
            for (Turma t : sistema.getTurmas()) {
                if (t.getProfessor() != null && t.getProfessor().getID().equals(prof.getID())) {
                    turmasVisiveis.add(t);
                }
            }
        } else if (usuarioLogado instanceof Aluno) {
            // ALUNO Vê turmas onde ele está matriculado (Principal ou Avulsa)
            Aluno aluno = (Aluno) usuarioLogado;
            for (Turma t : sistema.getTurmas()) {
                // O contains usa o equals, que compara referências ou IDs
                if (t.getAlunosDaTurma().contains(aluno)) {
                    turmasVisiveis.add(t);
                }
            }
        }
        
        comboTurmas.setItems(FXCollections.observableArrayList(turmasVisiveis));
        comboTurmas.setPromptText("Selecione a Turma...");

        // Ação ao selecionar
        comboTurmas.setOnAction(e -> {
            Turma t = comboTurmas.getValue();
            if (t != null) {
                turmaSelecionada = t;
                carregarHistorico();
                
                // Se for professor, destrava o envio
                if (usuarioLogado instanceof Professor) {
                    txtMensagem.setDisable(false);
                    btnEnviar.setDisable(false);
                }
            }
        });
    }

    private void enviarAviso() {
        String texto = txtMensagem.getText().trim();
        if (texto.isEmpty()) return;

        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        String msgFinal = String.format("[%s] %s: %s", 
            agora.format(fmt), 
            usuarioLogado.getNome(), 
            texto
        );

        turmaSelecionada.adicionarRecado(msgFinal);
        sistema.salvarDados();

        GuiUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Enviado", "Aviso publicado!");
        txtMensagem.clear();
        carregarHistorico();
    }

    private void carregarHistorico() {
        if (turmaSelecionada != null && turmaSelecionada.getRecados() != null) {
            List<String> recados = new ArrayList<>(turmaSelecionada.getRecados());
            java.util.Collections.reverse(recados); // Mais recentes no topo
            listaHistorico.setItems(FXCollections.observableArrayList(recados));
            
            if (recados.isEmpty()) {
                listaHistorico.setPlaceholder(new Label("Nenhum aviso publicado nesta turma."));
            }
        }
    }
}