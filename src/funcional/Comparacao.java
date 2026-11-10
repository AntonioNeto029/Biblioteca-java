package funcional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import modelos.Emprestimo;

public final class Comparacao {
    private Comparacao() {
    }

    public static BigDecimal totalMultasImperativo(List<Emprestimo> emprestimos, LocalDate dataReferencia, BigDecimal valorDiario) {
        BigDecimal total = new BigDecimal("0.00");
        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.ativo() && dataReferencia.isAfter(emprestimo.dataPrevista())) {
                long dias = ChronoUnit.DAYS.between(emprestimo.dataPrevista(), dataReferencia);
                total = total.add(Multas.calcularMulta(dias, valorDiario));
            }
        }
        return total;
    }

    public static BigDecimal totalMultasFuncional(List<Emprestimo> emprestimos, LocalDate dataReferencia, BigDecimal valorDiario) {
        return Filtros.filtrarEmprestimosAtrasados(emprestimos, dataReferencia).stream()
                .map(e -> Multas.calcularMulta(e.diasDeAtraso(dataReferencia), valorDiario))
                .reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
}
