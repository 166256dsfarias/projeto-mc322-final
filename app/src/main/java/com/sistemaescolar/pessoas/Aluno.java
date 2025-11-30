package com.sistemaescolar.pessoas;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.lang.Comparable;

import com.sistemaescolar.academico.Frequencia;
import com.sistemaescolar.academico.Nota;
import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.sistema.GeradorDeCredenciais;
import com.sistemaescolar.sistema.Sistema;

public class Aluno extends Pessoa implements Comparable<Aluno> {
    private String matricula;
    private List<Nota> boletim;
    private List<Frequencia> historicoDeFalta;
    private Turma turmaAtual;

    public Aluno(String nome, String cpf, String email, String senha, String matricula, Turma turmaAtual) {
        super(nome, cpf, email, senha);
        this.matricula = matricula;
        this.boletim = new ArrayList<>();
        this.turmaAtual = turmaAtual;
        this.historicoDeFalta = new ArrayList<>();
    }

    public String getMatricula() {
        return matricula;
    }

    public List<Nota> getBoletim() {
        return boletim;
    }

    public Turma getTurmaAtual() {
        return turmaAtual;
    }

    public List<Frequencia> getHistoricoDeFalta() {
        return historicoDeFalta;
    }

    public void visualizarBoletim() {
        System.out.println("BOLETIM DE " + this.getNome().toUpperCase());
        if (this.turmaAtual != null) {
            System.out.println("Turma: " + this.turmaAtual.getNome() + " | Ano: " + this.turmaAtual.getAnoLetivo());
        } else {
            System.out.println("Turma: Não matriculado");
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("%-20s | %-15s | %s%n", "DISCIPLINA", "DESCRIÇÃO", "NOTA");
        System.out.println("--------------------------------------------------");

        if (boletim.isEmpty()) {
            System.out.println("   Nenhuma nota lançada.");
        } else {
            for (Nota nota : boletim) {
                System.out.printf("%-20s | %-15s | %.1f%n",
                        nota.getDisciplina().getNome(),
                        nota.getDescricao(),
                        nota.getValor());
            }
        }
        System.out.println("--------------------------------------------------");
    }

    public void visualizarHistoricoDeFalta() {
        System.out.println("HISTORICO DE FALTAS DE " + this.getNome().toUpperCase());
        if (this.turmaAtual != null) {
            System.out.println("Turma: " + this.turmaAtual.getNome() + " | Ano: " + this.turmaAtual.getAnoLetivo());
        } else {
            System.out.println("Turma: Não matriculado");
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("%-20s | %-12s | %s%n", "DISCIPLINA", "DATA", "PRESENÇA");
        System.out.println("--------------------------------------------------");
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (historicoDeFalta.isEmpty()) {
            System.out.println("   Nenhum registro de frequência.");
        } else {
            for (Frequencia frequencia : historicoDeFalta) {
                System.out.printf("%-20s | %-12s | %s%n",
                        frequencia.getDisciplina().getNome(),
                        frequencia.getDataAula().format(formatador),
                        frequencia.presenteOuAusente());
            }
        }
        System.out.println("--------------------------------------------------");
    }

    public void registrarNota(Nota novaNota) {
        boletim.add(novaNota);
    }

    public void registrarFrequencia(Frequencia frequencia) {
        this.historicoDeFalta.add(frequencia);
    }

    @Override
    public int compareTo(Aluno outroAluno) {
        // Ordena por nome alfabeticamente 
        return this.getNome().compareToIgnoreCase(outroAluno.getNome());
    }

}