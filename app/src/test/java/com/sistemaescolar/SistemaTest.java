package com.sistemaescolar;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.sistema.GeradorDeCredenciais;
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Pessoa;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.academico.Nota;
import com.sistemaescolar.academico.Disciplina;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.exceptions.CredenciaisInvalidasException;
import com.sistemaescolar.exceptions.NotaInvalidaException;

class SistemaTest {

    // Teste 1: Verifica se o login funciona com senha correta e falha com senha errada
    @Test
    void testeLogin() {
        Sistema sistema = new Sistema();
        // Cria um admin na memória (sem salvar no arquivo)
        Administrador admin = new Administrador("Admin Teste", "123", "email", "Senha123", "Adm", 1000, "TESTE01");
        sistema.cadastrarAdministrador(admin);

        // Tenta logar certo (Não deve dar erro)
        assertDoesNotThrow(() -> {
            Pessoa logado = sistema.fazerLogin("TESTE01", "Senha123");
            assertNotNull(logado);
            assertEquals("Admin Teste", logado.getNome());
        });

        // Tenta logar errado (Deve lançar CredenciaisInvalidasException)
        assertThrows(CredenciaisInvalidasException.class, () -> {
            sistema.fazerLogin("TESTE01", "SenhaErrada");
        });
    }

    // Teste 2: Verifica a regra de negócio da Nota (0.0 a 10.0)
    @Test
    void testeValidacaoNota() {
        // Preparação de dados falsos (Mocks)
        Disciplina d = new Disciplina("MAT", "Matemática");
        Turma t = new Turma("3A", 2025);
        Aluno a = new Aluno("João", "123", "email", "123", "202501", t);

        // Cenário A: Nota Válida (8.5) -> Deve passar
        assertDoesNotThrow(() -> {
            new Nota("Prova 1", 8.5, a, d);
        });

        // Cenário B: Nota Inválida (11.0) -> Deve dar erro (NotaInvalidaException)
        assertThrows(NotaInvalidaException.class, () -> {
            new Nota("Prova 2", 11.0, a, d);
        });

        // Cenário C: Nota Negativa (-1.0) -> Deve dar erro
        assertThrows(NotaInvalidaException.class, () -> {
            new Nota("Prova 3", -1.0, a, d);
        });
    }

    // Teste 3: Verifica se a matrícula é gerada usando o nome da turma
    @Test
    void testeGeracaoMatricula() {
        Turma t = new Turma("3A", 2025);
        
        // Gera matrícula
        String matricula = GeradorDeCredenciais.gerarMatricula(t);
        
        // Verifica se começa com o nome da turma e o ano (Regra que criamos)
        // Ex esperado: "3A2025..."
        assertNotNull(matricula);
        assertTrue(matricula.startsWith("3A2025"));
    }
}