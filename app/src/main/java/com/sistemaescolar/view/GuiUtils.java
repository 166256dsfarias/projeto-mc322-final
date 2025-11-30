package com.sistemaescolar.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class GuiUtils {

    // --- MÉTODOS DE ALERTAS ---
    public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }

    // --- COMPONENTES VISUAIS ---
    public static HBox criarLabelObrigatorio(String texto) {
        Label lblTexto = new Label(texto);
        Label lblAsterisco = new Label(" *");
        lblAsterisco.setTextFill(Color.RED);
        lblAsterisco.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        HBox container = new HBox(lblTexto, lblAsterisco);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }

    // --- MÁSCARAS ---

    public static void configurarMascaraCPF(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String digits = newValue.replaceAll("[^0-9]", "");
            if (digits.length() > 11) digits = digits.substring(0, 11);
            
            String masked = digits;
            if (digits.length() > 9) masked = digits.substring(0, 3) + "." + digits.substring(3, 6) + "." + digits.substring(6, 9) + "-" + digits.substring(9);
            else if (digits.length() > 6) masked = digits.substring(0, 3) + "." + digits.substring(3, 6) + "." + digits.substring(6);
            else if (digits.length() > 3) masked = digits.substring(0, 3) + "." + digits.substring(3);
            
            if (!masked.equals(tf.getText())) {
                tf.setText(masked);
                tf.positionCaret(masked.length());
            }
        });
    }

    public static void configurarMascaraMoeda(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;
            String cleanString = newValue.replaceAll("[^0-9]", "");
            if (cleanString.isEmpty()) return;
            
            BigDecimal valor = new BigDecimal(cleanString).divide(new BigDecimal(100));
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            String formatado = nf.format(valor);
            
            if (!newValue.equals(formatado)) {
                Platform.runLater(() -> {
                    tf.setText(formatado);
                    tf.positionCaret(formatado.length());
                });
            }
        });
    }

    public static void configurarMascaraNota(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) return;

            // Limpa tudo que não for número (tira pontos, virgulas, letras)
            String digits = newValue.replaceAll("[^0-9]", "");
            
            // Se apagou tudo, deixa quieto
            if (digits.isEmpty()) return;

            try {
                // transforma em número
                long rawValue = Long.parseLong(digits);

                // Divide por 10 para criar UMA casa decimal (ex: 85 -> 8.5)
                double val = rawValue / 10.0;

                // Validação de Limite (0.0 a 10.0)
                if (val > 10.0) {
                    tf.setText(oldValue); 
                    return;
                }

                // Formata de volta para String com ponto (Padrão US)
                String formatted = String.format(java.util.Locale.US, "%.1f", val);

                // Atualiza o campo apenas se mudou
                if (!newValue.equals(formatted)) {
                    Platform.runLater(() -> {
                        tf.setText(formatted);
                        tf.positionCaret(formatted.length()); // Mantém cursor no final
                    });
                }

            } catch (NumberFormatException e) {
                // Ignora erros
            }
        });
    }
    
    public static void configurarMascaraNumerica(TextField tf) {
        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*")) {
                tf.setText(oldVal);
            }
        });
    }

    public static String formatarData(java.time.LocalDate data) {
        if (data == null) return "";
        return data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}