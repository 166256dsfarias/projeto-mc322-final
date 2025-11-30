package com.sistemaescolar.pessoas;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.academico.Disciplina;
import com.sistemaescolar.sistema.Sistema;
import com.sistemaescolar.sistema.GeradorDeCredenciais;

public class Administrador extends Funcionario {
    
    public Administrador(String nome, String cpf, String email, String senha, String cargo, float salario, String ID) {
        super(nome, cpf, email, senha, cargo, salario, ID);
    }

    // --- MÉTODOS DE CADASTRO DE PESSOAS ---

    public void cadastrarAluno(Sistema sistema, String nome, String cpf, String email, Turma turmaAtual) {
        
        // Validação de Regra de Negócio: Aluno OBRIGATORIAMENTE tem turma
        if (turmaAtual == null) {
            throw new IllegalArgumentException("Não é possível cadastrar aluno sem turma.");
        }

        // Gera a matrícula usando a turma
        String matricula = GeradorDeCredenciais.gerarMatricula(turmaAtual);
        String senha = "Mudar@123";
        
        // Cria o aluno
        Aluno novoAluno = new Aluno(nome, cpf, email, senha, matricula, turmaAtual);
        
        // Vincula na Turma (Bidirecional)
        turmaAtual.getAlunosDaTurma().add(novoAluno);
        
        // Salva no Sistema
        sistema.getAlunos().add(novoAluno);
        
        System.out.println("Aluno cadastrado: " + nome + " (" + matricula + ")");
    }

    public void cadastrarProfessor(Sistema sistema, String nome, String cpf, String email, float salario) {
        String senha = "Mudar@123";
        String cargo = "Professor";
        String ID = GeradorDeCredenciais.gerarIDfuncional(cargo);
        Professor novoProfessor = new Professor(nome, cpf, email, senha, cargo, salario, ID);
        sistema.getProfessores().add(novoProfessor);
        System.out.println("Professor cadastrado! ID gerado: " + ID);
    }

    public void cadastrarAdministrador(Sistema sistema, String nome, String cpf, String email, float salario) {
        String senha = "Mudar@123";
        String cargo = "Administrador";
        String ID = GeradorDeCredenciais.gerarIDfuncional(cargo);
        Administrador novoAdministrador = new Administrador(nome, cpf, email, senha, cargo, salario, ID);
        sistema.getAdministradores().add(novoAdministrador);
    }

    // --- MÉTODOS ACADÊMICOS  ---

    public void cadastrarDisciplina(Sistema sistema, String codigo, String nome) {
        Disciplina novaDisciplina = new Disciplina(codigo, nome);
        sistema.getDisciplinas().add(novaDisciplina);
    }

    // Atualizar Disciplina não mexe mais em professor
    public void atualizarDisciplina(Sistema sistema, String codigoAntigo, String novoCodigo, String novoNome) {
        Disciplina disciplinaEncontrada = sistema.buscarDisciplina(codigoAntigo);
        if (disciplinaEncontrada != null) {
            disciplinaEncontrada.setCodigo(novoCodigo);
            disciplinaEncontrada.setNome(novoNome);
        } else {
            System.out.println("Erro: Disciplina nao encontrada");
        }
    }

    // Cadastrar Turma agora faz a amarração completa
    public void cadastrarTurma(Sistema sistema, String nome, int anoLetivo, Disciplina disciplina, Professor professor) {
        Turma novaTurma = new Turma(nome, anoLetivo);
        
        // Configura os relacionamentos na Turma
        novaTurma.setDisciplina(disciplina);
        novaTurma.setProfessor(professor);
        
        // Configura o relacionamento no Professor 
        if (professor != null && disciplina != null) {
            professor.novaDisciplinaLecionada(disciplina);
        }
        
        sistema.getTurmas().add(novaTurma);
    }
    
    // Sobrecarga para compatibilidade, cria turma vazia se necessário
    public void cadastrarTurma(Sistema sistema, String nome, int anoLetivo) {
        cadastrarTurma(sistema, nome, anoLetivo, null, null);
    }

    public void atualizarTurma(Sistema sistema, String nomeAntigo, int antigoAnoLetivo, String novoNome, int novoAnoLetivo) {
        Turma turmaEncontrada = sistema.buscarTurma(nomeAntigo, antigoAnoLetivo);
        if (turmaEncontrada != null) {
            turmaEncontrada.setAnoLetivo(novoAnoLetivo);
            turmaEncontrada.setNome(novoNome);
        } else {
            System.out.println("Erro: Turma nao encontrada");
        }
    }

    public void matricularAlunoEmTurma(Aluno aluno, Turma turmaDestino) {
        if (aluno == null || turmaDestino == null) {
            throw new IllegalArgumentException("Aluno e Turma são obrigatórios.");
        }

        // Verifica se já está matriculado para não duplicar
        if (turmaDestino.getAlunosDaTurma().contains(aluno)) {
            throw new IllegalArgumentException("O aluno já está matriculado nesta turma.");
        }

        // Adiciona o aluno na lista da turma 
        turmaDestino.getAlunosDaTurma().add(aluno);
        
        System.out.println("Aluno " + aluno.getNome() + " matriculado na turma " + turmaDestino.getNome());
    }
    
    // Stubs para métodos não implementados (para evitar erro de compilação)
    public void atualizarUsuario(Pessoa usuario) {}
    public void deletarUsuario(Pessoa usuario) {}
    public void deletarDisciplina(Disciplina disciplina) {}
    public void deletarTurma(Turma turma) {}
}