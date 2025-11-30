package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Aluno;
import javafx.scene.control.TextField;

public class AlunoNotaRow {
    
    private Aluno aluno;
    private TextField campoNota;

    public AlunoNotaRow(Aluno aluno) {
        this.aluno = aluno;
        this.campoNota = new TextField();
        this.campoNota.setPromptText("0.0");
        
        GuiUtils.configurarMascaraNota(this.campoNota);
    }

    // Método para preencher valor (usado na tela de Atualizar)
    public void setValorInicial(double valor) {
        this.campoNota.setText(String.format(java.util.Locale.US, "%.1f", valor));
    }

    public Aluno getAluno() {
        return aluno;
    }

    public TextField getCampoNota() {
        return campoNota;
    }
}