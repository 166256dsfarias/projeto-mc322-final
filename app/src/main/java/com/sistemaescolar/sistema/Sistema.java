package com.sistemaescolar.sistema;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.time.LocalDate;

import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Pessoa;
import com.sistemaescolar.pessoas.Administrador;
import com.sistemaescolar.pessoas.Professor;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.academico.Disciplina;
import com.sistemaescolar.exceptions.CredenciaisInvalidasException;

public class Sistema {
    private List<Turma> turmas;
    private List<Aluno> alunos;
    private List<Professor> professores;
    private List<Disciplina> disciplinas;
    private List<Administrador> administradores;
    private static final String ARQUIVO_DADOS = "dados_escola.bin";

    public Sistema() {
        turmas = new ArrayList<>();
        alunos = new ArrayList<>();
        professores = new ArrayList<>();
        disciplinas = new ArrayList<>();
        administradores = new ArrayList<>();
    }

    public void cadastrarTurma(Turma novaTurma) {
        turmas.add(novaTurma);
    }

    public void cadastrarAluno(Aluno novoAluno) {
        alunos.add(novoAluno);
    }

    public void cadastrarProfessor(Professor novoProfessor) {
        professores.add(novoProfessor);
    }

    public void cadastrarDisciplina(Disciplina novaDisciplina) {
        disciplinas.add(novaDisciplina);
    }

    public void cadastrarAdministrador(Administrador novoAdministrador) {
        administradores.add(novoAdministrador);
    }

    public List<Turma> getTurmas() {
        return turmas;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public Turma buscarTurma(String nome, int anoLetivo) {
        for (Turma turma : turmas) {
            if (turma.getNome().equalsIgnoreCase(nome) && turma.getAnoLetivo() == anoLetivo) {
                return turma;
            }
        }
        return null;
    }

    public Aluno buscarAluno(String matricula) {
        for (Aluno aluno : alunos) {
            if (aluno.getMatricula().equalsIgnoreCase(matricula)) {
                return aluno;
            }
        }
        return null;
    }

    public Professor buscarProfessor(String nome, String cpf) {
        for (Professor professor : professores) {
            if (professor.getNome().equalsIgnoreCase(nome) && professor.getCpf().equalsIgnoreCase(cpf)) {
                return professor;
            }
        }
        return null;
    }

    public Disciplina buscarDisciplina(String nome) {
        for (Disciplina disciplina : disciplinas) {
            if (disciplina.getNome().equalsIgnoreCase(nome)) {
                return disciplina;
            }
        }
        return null;
    }

    public Administrador buscarAdministrador(String nome) {
        for (Administrador administrador : administradores) {
            if (administrador.getNome().equalsIgnoreCase(nome)) {
                return administrador;
            }
        }
        return null;
    }

    public Pessoa fazerLogin (String usuario, String senha) throws CredenciaisInvalidasException {
        for (Administrador administrador : administradores) {
            if (administrador.getID().equalsIgnoreCase(usuario) && administrador.getSenha().equals(senha))
                return administrador;
        }
        for (Professor professor : professores) {
            if (professor.getID().equalsIgnoreCase(usuario) && professor.getSenha().equals(senha))
                return professor;
        }
        for (Aluno aluno : alunos) {
            if (aluno.getMatricula().equalsIgnoreCase(usuario) && aluno.getSenha().equals(senha))
                return aluno;
        }
        
        throw new CredenciaisInvalidasException("Erro: Usuário ou senha incorretos.");

    }

    // MÉTODO 1: SALVAR TUDO com Serialização
    public void salvarDados() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            // Escreve as listas inteiras no arquivo
            out.writeObject(this.alunos);
            out.writeObject(this.professores);
            out.writeObject(this.administradores);
            out.writeObject(this.disciplinas);
            out.writeObject(this.turmas);
            
            System.out.println("Dados salvos com sucesso em " + ARQUIVO_DADOS);
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static Sistema carregarDados() {
        Sistema sistemaRecuperado = new Sistema();
        File arquivo = new File(ARQUIVO_DADOS);

        if (!arquivo.exists()) {
            System.out.println("Nenhum dado salvo encontrado. Iniciando sistema vazio.");
            return sistemaRecuperado; // Retorna um sistema novo vazio
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            // A ordem de leitura DEVE SER EXATAMENTE a mesma da escrita
            sistemaRecuperado.alunos = (List<Aluno>) in.readObject();
            sistemaRecuperado.professores = (List<Professor>) in.readObject();
            sistemaRecuperado.administradores = (List<Administrador>) in.readObject();
            sistemaRecuperado.disciplinas = (List<Disciplina>) in.readObject();
            sistemaRecuperado.turmas = (List<Turma>) in.readObject();

            System.out.println("Dados carregados com sucesso!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }

        return sistemaRecuperado;
    }

    public boolean frequenciaExiste(Aluno aluno, Disciplina disciplina, LocalDate dataAula) {
        if (aluno.getHistoricoDeFalta() == null) {
            return false;
        }
        return aluno.getHistoricoDeFalta().stream()
                .anyMatch(f -> f.getDisciplina().equals(disciplina) && f.getDataAula().equals(dataAula));
    }

}
