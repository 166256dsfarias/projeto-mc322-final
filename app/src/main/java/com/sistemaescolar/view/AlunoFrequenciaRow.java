package com.sistemaescolar.view;

import com.sistemaescolar.pessoas.Aluno;
import javafx.scene.control.CheckBox;

/**
 * Classe auxiliar para representar a linha na Tabela de Frequência.
 * Contém o objeto Aluno e o CheckBox de Presença/Ausência.
 */
public class AlunoFrequenciaRow {
    private final Aluno aluno;
    private final CheckBox presente;

    public AlunoFrequenciaRow(Aluno aluno) {
        this.aluno = aluno;
        this.presente = new CheckBox();
        this.presente.setSelected(true); // Padrão: Aluno está PRESENTE
        this.presente.setText("Presente"); 
        
        // Listener para mudar o texto quando o estado muda (apenas visual)
        this.presente.selectedProperty().addListener((obs, oldVal, newVal) -> {
            this.presente.setText(newVal ? "Presente" : "Ausente");
        });
    }

    public Aluno getAluno() {
        return aluno;
    }

    public CheckBox getPresente() {
        return presente;
    }
    
    // Método de atalho para verificar o estado
    public boolean isAlunoPresente() {
        return presente.isSelected();
    }
}