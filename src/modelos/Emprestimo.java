package modelos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Emprestimo(int id, Livro livro, Usuario usuario, LocalDate dataEmprestimo,
                         LocalDate dataPrevista, LocalDate dataDevolucao) {

    public boolean ativo() {
        return dataDevolucao == null;
    }

    public boolean estaAtrasado(LocalDate dataReferencia) {
        return ativo() && dataReferencia.isAfter(dataPrevista);
    }

    public long diasDeAtraso(LocalDate dataReferencia) {
        return estaAtrasado(dataReferencia) ? ChronoUnit.DAYS.between(dataPrevista, dataReferencia) : 0;
    }

    public Emprestimo devolvidoEm(LocalDate novaDataDevolucao) {
        return new Emprestimo(id, livro, usuario, dataEmprestimo, dataPrevista, novaDataDevolucao);
    }
}
