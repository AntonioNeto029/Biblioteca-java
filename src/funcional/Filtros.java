package funcional;

import java.time.LocalDate;
import java.util.List;
import modelos.Emprestimo;
import modelos.Livro;

public final class Filtros {
    private Filtros() {
    }

    public static List<Livro> filtrarLivrosPorCategoria(List<Livro> livros, String categoria) {
        return livros.stream().filter(livro -> livro.categoria().equals(categoria)).toList();
    }

    public static List<Emprestimo> filtrarEmprestimosAtrasados(List<Emprestimo> emprestimos, LocalDate dataReferencia) {
        return emprestimos.stream().filter(e -> e.estaAtrasado(dataReferencia)).toList();
    }
}
