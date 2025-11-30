package com.sistemaescolar.sistema;

import java.time.Year;

import com.sistemaescolar.academico.Turma;
import com.sistemaescolar.pessoas.Aluno;
import com.sistemaescolar.pessoas.Funcionario;

public class GeradorDeCredenciais {
    private static int sequencialAluno = 1;
    private static int sequencialProfessor = 1;
    private static int sequencialAdministrador = 1;
    private static String prefixoProf = "PROF";
    private static String prefixoAdm = "ADM";

    // ---Sincroniza os contadores com os dados carregados ---
    public static void inicializarContadores(Sistema sistema) {
        // Acha o maior sequencial de Aluno (Ex: 20250005 -> pega o 5)
        for (Aluno a : sistema.getAlunos()) {
            try {
                // Pega os ultimos 4 digitos
                int seq = Integer.parseInt(a.getMatricula().substring(4));
                if (seq >= sequencialAluno) sequencialAluno = seq + 1;
            } catch (Exception e) { /* ignora formatos estranhos */ }
        }

        // Acha maior sequencial de Professor (Ex: PROF003 -> pega o 3)
        for (Funcionario p : sistema.getProfessores()) {
            atualizarSeqFuncionario(p.getID(), prefixoProf, true);
        }

        // Acha maior sequencial de Admin
        for (Funcionario a : sistema.getAdministradores()) {
            atualizarSeqFuncionario(a.getID(), prefixoAdm, false);
        }
    }

    private static void atualizarSeqFuncionario(String id, String prefixo, boolean isProf) {
        try {
            if (id.startsWith(prefixo)) {
                int seq = Integer.parseInt(id.replace(prefixo, ""));
                if (isProf) {
                    if (seq >= sequencialProfessor) sequencialProfessor = seq + 1;
                } else {
                    if (seq >= sequencialAdministrador) sequencialAdministrador = seq + 1;
                }
            }
        } catch (Exception e) {}
    }

    public static String gerarMatricula(Turma turma) {
        if (turma == null) {
            throw new IllegalArgumentException("Erro crítico: Tentativa de gerar matrícula sem Turma vinculada.");
        }

        String prefixo = turma.getNome().replace(" ", "").toUpperCase();
        int ano = turma.getAnoLetivo();
        
        // Formato: TURMA + ANO + SEQUENCIAL (ex: 3A20250001)
        String matricula = String.format("%s%d%04d", prefixo, ano, sequencialAluno);
        
        sequencialAluno++;
        return matricula;
    }

    public static String gerarIDfuncional(String cargo) {
        String id = "";
        if (cargo.equalsIgnoreCase("Professor")) {
            id = String.format("%s%03d", prefixoProf, sequencialProfessor);
            sequencialProfessor++;
        } else if (cargo.equalsIgnoreCase("Administrador")) {
            id = String.format("%s%03d", prefixoAdm, sequencialAdministrador);
            sequencialAdministrador++;
        }
        return id;
    }
    
    // Validação de senha 
    public static boolean validarSenha(String senha) {
        return senha.length() >= 8; 
    }
}