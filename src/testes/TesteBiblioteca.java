package testes;

import funcional.Comparacao;
import funcional.Estatisticas;
import funcional.Filtros;
import funcional.Multas;
import funcional.RelatorioMultas;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import modelos.Biblioteca;
import modelos.Emprestimo;
import modelos.Livro;

public class TesteBiblioteca {
    private static final LocalDate REF = LocalDate.of(2026, 11, 10);
    private static final BigDecimal DOIS = Multas.VALOR_MULTA_DIARIA;
    private static int executados = 0;

    interface Teste {
        void rodar(Biblioteca b);
    }

    static Biblioteca nova() {
        Biblioteca b = new Biblioteca();
        b.cadastrarLivro("A", "X", "Romance");
        b.cadastrarLivro("B", "Y", "Ciência");
        b.cadastrarLivro("C", "Z", "Romance");
        b.cadastrarUsuario("Ana", "1");
        b.cadastrarUsuario("Bia", "2");
        return b;
    }

    static void verificar(boolean condicao, String nome) {
        if (!condicao) {
            throw new AssertionError("Falhou: " + nome);
        }
    }

    static void igual(Object esperado, Object atual, String nome) {
        verificar(esperado.equals(atual), nome + " esperado=" + esperado + " atual=" + atual);
    }

    static void lancaExcecao(Runnable acao, String nome) {
        try {
            acao.run();
        } catch (RuntimeException esperado) {
            return;
        }
        throw new AssertionError("Sem exceção: " + nome);
    }

    static void caso(String nome, Teste teste) {
        teste.rodar(nova());
        executados++;
        System.out.println("OK " + nome);
    }

    public static void main(String[] args) {
        caso("calcularMulta", b -> {
            igual(0, Multas.calcularMulta(0, DOIS).intValue(), "zero dias");
            igual(new BigDecimal("6.00"), Multas.calcularMulta(3, DOIS), "tres dias");
        });
        caso("matricula duplicada", b -> lancaExcecao(() -> b.cadastrarUsuario("Outra", "1"), "matricula"));
        caso("emprestimo bloqueia e devolucao libera", b -> {
            Emprestimo e = b.registrarEmprestimo(1, 1, LocalDate.of(2026, 11, 1));
            lancaExcecao(() -> b.registrarEmprestimo(1, 2, LocalDate.of(2026, 11, 2)), "indisponivel");
            b.registrarDevolucao(e.id(), LocalDate.of(2026, 11, 3));
            verificar(b.livroDisponivel(1), "disponivel");
            b.registrarEmprestimo(1, 2, LocalDate.of(2026, 11, 4));
        });
        caso("dia exato do prazo nao e atraso", b -> {
            Emprestimo e = b.registrarEmprestimo(1, 1, LocalDate.of(2026, 11, 3));
            verificar(!e.estaAtrasado(REF), "no prazo");
            verificar(e.estaAtrasado(REF.plusDays(1)), "atrasado");
        });
        caso("devolvido nao gera multa", b -> {
            Emprestimo e = b.registrarEmprestimo(1, 1, LocalDate.of(2026, 10, 1));
            b.registrarDevolucao(e.id(), LocalDate.of(2026, 10, 20));
            RelatorioMultas r = Multas.gerarRelatorioMultas(b.consultarEmprestimos(), REF);
            igual(new BigDecimal("0.00"), r.total(), "total");
            verificar(r.itens().isEmpty(), "sem itens");
        });
        caso("relatorio total", b -> {
            b.registrarEmprestimo(1, 1, LocalDate.of(2026, 11, 1));
            b.registrarEmprestimo(2, 2, LocalDate.of(2026, 11, 5));
            RelatorioMultas r = Multas.gerarRelatorioMultas(b.consultarEmprestimos(), REF);
            igual(new BigDecimal("4.00"), r.total(), "total");
            igual(1, r.itens().size(), "itens");
        });
        caso("filtro nao altera original", b -> {
            List<Livro> livros = new ArrayList<>(b.consultarLivros());
            List<Livro> copia = List.copyOf(livros);
            igual(2, Filtros.filtrarLivrosPorCategoria(livros, "Romance").size(), "filtrados");
            igual(copia, livros, "original");
        });
        caso("lista retornada e imutavel", b -> lancaExcecao(() -> b.consultarLivros().clear(), "clear"));
        caso("estatisticas", b -> {
            b.registrarEmprestimo(1, 1, LocalDate.of(2026, 10, 1));
            b.registrarEmprestimo(2, 1, LocalDate.of(2026, 11, 8));
            Estatisticas.Resumo s = Estatisticas.gerarEstatisticas(b.consultarLivros(), b.consultarEmprestimos(), REF);
            igual(Map.of("Romance", 2, "Ciência", 1), s.livrosPorCategoria(), "categorias");
            igual(Map.of("Ana", 2), s.emprestimosPorUsuario(), "por usuario");
            igual(2L, s.emprestimosAtivos(), "ativos");
            igual(1L, s.ativosEmAtraso(), "atrasados");
            igual(new BigDecimal("66.00"), s.valorTotalMultas(), "multas");
        });
        caso("imperativo igual funcional", b -> {
            b.registrarEmprestimo(1, 1, LocalDate.of(2026, 10, 1));
            b.registrarEmprestimo(2, 2, LocalDate.of(2026, 10, 15));
            List<Emprestimo> e = b.consultarEmprestimos();
            igual(Comparacao.totalMultasImperativo(e, REF, DOIS), Comparacao.totalMultasFuncional(e, REF, DOIS), "totais");
        });
        System.out.println(executados + " testes passaram");
    }
}
