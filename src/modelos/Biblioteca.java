package modelos;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Biblioteca {
    private final Map<Integer, Livro> livros = new LinkedHashMap<>();
    private final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private final Map<Integer, Emprestimo> emprestimos = new LinkedHashMap<>();
    private int proximoLivro = 1;
    private int proximoUsuario = 1;
    private int proximoEmprestimo = 1;

    public synchronized Livro cadastrarLivro(String titulo, String autor, String categoria) {
        Livro livro = new Livro(proximoLivro++, titulo, autor, categoria);
        livros.put(livro.id(), livro);
        return livro;
    }

    public synchronized Usuario cadastrarUsuario(String nome, String matricula) {
        if (usuarios.values().stream().anyMatch(u -> u.matricula().equals(matricula))) {
            throw new IllegalArgumentException("Matrícula já cadastrada: " + matricula);
        }
        Usuario usuario = new Usuario(proximoUsuario++, nome, matricula);
        usuarios.put(usuario.id(), usuario);
        return usuario;
    }

    public synchronized List<Livro> consultarLivros() {
        return List.copyOf(livros.values());
    }

    public synchronized List<Usuario> consultarUsuarios() {
        return List.copyOf(usuarios.values());
    }

    public synchronized List<Emprestimo> consultarEmprestimos() {
        return List.copyOf(emprestimos.values());
    }

    public synchronized List<Emprestimo> emprestimosAtivos() {
        return emprestimos.values().stream().filter(Emprestimo::ativo).toList();
    }

    public synchronized Livro buscarLivro(int idLivro) {
        Livro livro = livros.get(idLivro);
        if (livro == null) {
            throw new IllegalArgumentException("Livro inexistente: " + idLivro);
        }
        return livro;
    }

    public synchronized Usuario buscarUsuario(int idUsuario) {
        Usuario usuario = usuarios.get(idUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário inexistente: " + idUsuario);
        }
        return usuario;
    }

    public synchronized boolean livroDisponivel(int idLivro) {
        return emprestimosAtivos().stream().noneMatch(e -> e.livro().id() == idLivro);
    }

    public synchronized Emprestimo registrarEmprestimo(int idLivro, int idUsuario, LocalDate dataEmprestimo, int diasPrazo) {
        Livro livro = buscarLivro(idLivro);
        Usuario usuario = buscarUsuario(idUsuario);
        if (!livroDisponivel(idLivro)) {
            throw new IllegalStateException("Livro indisponível: " + livro.titulo());
        }
        Emprestimo emprestimo = new Emprestimo(proximoEmprestimo++, livro, usuario,
                dataEmprestimo, dataEmprestimo.plusDays(diasPrazo), null);
        emprestimos.put(emprestimo.id(), emprestimo);
        return emprestimo;
    }

    public Emprestimo registrarEmprestimo(int idLivro, int idUsuario, LocalDate dataEmprestimo) {
        return registrarEmprestimo(idLivro, idUsuario, dataEmprestimo, 7);
    }

    public synchronized Emprestimo registrarDevolucao(int idEmprestimo, LocalDate dataDevolucao) {
        Emprestimo atual = emprestimos.get(idEmprestimo);
        if (atual == null) {
            throw new IllegalArgumentException("Empréstimo inexistente: " + idEmprestimo);
        }
        if (!atual.ativo()) {
            throw new IllegalStateException("Empréstimo já devolvido: " + idEmprestimo);
        }
        Emprestimo devolvido = atual.devolvidoEm(dataDevolucao);
        emprestimos.put(idEmprestimo, devolvido);
        return devolvido;
    }
}
