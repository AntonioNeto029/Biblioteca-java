package funcional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import modelos.Emprestimo;
import modelos.Livro;

public final class Estatisticas {
    private Estatisticas() {
    }

    public record Resumo(Map<String, Integer> livrosPorCategoria, Map<String, Integer> emprestimosPorUsuario,
                         long emprestimosAtivos, long ativosEmAtraso, BigDecimal valorTotalMultas) {
    }

    public static <T> Map<String, Integer> contarPor(List<T> itens, Function<T, String> chave) {
        return Map.copyOf(itens.stream().reduce(
                new LinkedHashMap<String, Integer>(),
                (acumulado, item) -> {
                    Map<String, Integer> novo = new LinkedHashMap<>(acumulado);
                    novo.merge(chave.apply(item), 1, Integer::sum);
                    return new LinkedHashMap<>(novo);
                },
                (a, b) -> {
                    Map<String, Integer> novo = new LinkedHashMap<>(a);
                    b.forEach((k, v) -> novo.merge(k, v, Integer::sum));
                    return new LinkedHashMap<>(novo);
                }));
    }

    public static Map<String, Integer> livrosPorCategoria(List<Livro> livros) {
        return contarPor(livros, Livro::categoria);
    }

    public static Map<String, Integer> emprestimosPorUsuario(List<Emprestimo> emprestimos) {
        return contarPor(emprestimos, e -> e.usuario().nome());
    }

    public static long quantidadeEmprestimosAtivos(List<Emprestimo> emprestimos) {
        return emprestimos.stream().filter(Emprestimo::ativo).count();
    }

    public static long quantidadeAtivosEmAtraso(List<Emprestimo> emprestimos, LocalDate dataReferencia) {
        return Filtros.filtrarEmprestimosAtrasados(emprestimos, dataReferencia).size();
    }

    public static BigDecimal valorTotalMultas(List<Emprestimo> emprestimos, LocalDate dataReferencia) {
        return Multas.gerarRelatorioMultas(emprestimos, dataReferencia).total();
    }

    public static Resumo gerarEstatisticas(List<Livro> livros, List<Emprestimo> emprestimos, LocalDate dataReferencia) {
        return new Resumo(livrosPorCategoria(livros), emprestimosPorUsuario(emprestimos),
                quantidadeEmprestimosAtivos(emprestimos), quantidadeAtivosEmAtraso(emprestimos, dataReferencia),
                valorTotalMultas(emprestimos, dataReferencia));
    }
}
