package funcional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import modelos.Emprestimo;

public final class Multas {
    public static final BigDecimal VALOR_MULTA_DIARIA = new BigDecimal("2.00");

    private Multas() {
    }

    public static BigDecimal calcularMulta(long diasAtraso, BigDecimal valorDiario) {
        return valorDiario.multiply(BigDecimal.valueOf(Math.max(0, diasAtraso)));
    }

    public static RelatorioMultas gerarRelatorioMultas(List<Emprestimo> emprestimos, LocalDate dataReferencia) {
        return gerarRelatorioMultas(emprestimos, dataReferencia, VALOR_MULTA_DIARIA);
    }

    public static RelatorioMultas gerarRelatorioMultas(List<Emprestimo> emprestimos, LocalDate dataReferencia, BigDecimal valorDiario) {
        List<LinhaMulta> itens = Filtros.filtrarEmprestimosAtrasados(emprestimos, dataReferencia).stream()
                .map(e -> new LinhaMulta(e.id(), e.livro().titulo(), e.usuario().nome(),
                        e.diasDeAtraso(dataReferencia),
                        calcularMulta(e.diasDeAtraso(dataReferencia), valorDiario)))
                .toList();
        BigDecimal total = itens.stream().map(LinhaMulta::multa).reduce(new BigDecimal("0.00"), BigDecimal::add);
        return new RelatorioMultas(itens, total);
    }

    public static String formatarRelatorio(RelatorioMultas relatorio) {
        String linhas = relatorio.itens().stream()
                .map(l -> String.format("#%d | %s | %s | %d dia(s) | R$ %s",
                        l.idEmprestimo(), l.titulo(), l.usuario(), l.diasAtraso(), l.multa().toPlainString()))
                .collect(Collectors.joining("\n"));
        String total = "TOTAL: R$ " + relatorio.total().toPlainString();
        return linhas.isEmpty() ? total : linhas + "\n" + total;
    }
}
