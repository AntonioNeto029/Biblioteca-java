import funcional.Comparacao;
import funcional.Estatisticas;
import funcional.Filtros;
import funcional.Multas;
import java.time.LocalDate;
import java.util.List;
import modelos.Biblioteca;
import modelos.Emprestimo;

public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        List.of(
                new String[]{"Clean Code", "Robert Martin", "Programação"},
                new String[]{"Java Efetivo", "Joshua Bloch", "Programação"},
                new String[]{"Dom Casmurro", "Machado de Assis", "Romance"},
                new String[]{"O Cortiço", "Aluísio Azevedo", "Romance"},
                new String[]{"Cálculo Vol. 1", "James Stewart", "Matemática"},
                new String[]{"Sapiens", "Yuval Harari", "História"})
                .forEach(d -> biblioteca.cadastrarLivro(d[0], d[1], d[2]));
        var ana = biblioteca.cadastrarUsuario("Ana Souza", "2024001");
        var bruno = biblioteca.cadastrarUsuario("Bruno Lima", "2024002");
        var carla = biblioteca.cadastrarUsuario("Carla Dias", "2024003");

        biblioteca.registrarEmprestimo(1, ana.id(), LocalDate.of(2026, 10, 20));
        biblioteca.registrarEmprestimo(3, bruno.id(), LocalDate.of(2026, 10, 25));
        biblioteca.registrarEmprestimo(5, ana.id(), LocalDate.of(2026, 11, 5));
        Emprestimo e4 = biblioteca.registrarEmprestimo(6, carla.id(), LocalDate.of(2026, 10, 1));

        System.out.println("Disponível livro 1: " + biblioteca.livroDisponivel(1));
        try {
            biblioteca.registrarEmprestimo(1, carla.id(), LocalDate.of(2026, 11, 1));
        } catch (IllegalStateException erro) {
            System.out.println("Erro esperado: " + erro.getMessage());
        }

        biblioteca.registrarDevolucao(e4.id(), LocalDate.of(2026, 10, 5));
        System.out.println("Disponível livro 6 após devolução: " + biblioteca.livroDisponivel(6));

        LocalDate referencia = LocalDate.of(2026, 11, 10);
        List<Emprestimo> emprestimos = biblioteca.consultarEmprestimos();

        System.out.println("\nLivros de Programação:");
        Filtros.filtrarLivrosPorCategoria(biblioteca.consultarLivros(), "Programação").forEach(System.out::println);

        System.out.println("\nEmpréstimos atrasados em " + referencia);
        Filtros.filtrarEmprestimosAtrasados(emprestimos, referencia).forEach(System.out::println);

        System.out.println("\nRelatório de multas:");
        System.out.println(Multas.formatarRelatorio(Multas.gerarRelatorioMultas(emprestimos, referencia)));

        System.out.println("\nEstatísticas:");
        System.out.println(Estatisticas.gerarEstatisticas(biblioteca.consultarLivros(), emprestimos, referencia));

        System.out.println("\nImperativo: " + Comparacao.totalMultasImperativo(emprestimos, referencia, Multas.VALOR_MULTA_DIARIA));
        System.out.println("Funcional : " + Comparacao.totalMultasFuncional(emprestimos, referencia, Multas.VALOR_MULTA_DIARIA));
    }
}
