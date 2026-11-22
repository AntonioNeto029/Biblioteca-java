# Biblioteca Java

Sistema de gerenciamento de biblioteca universitária que integra **Programação Orientada a Objetos** e **Programação Funcional**.

Atividade prática da disciplina **Linguagens de Programação**.

## Integrantes

- Antonio Neto
- Biel Aguiar Santos
- Pedro Ernesto de Almeida Kaelin Vogado
- Thiago Rocha

## Linguagem

- **Java 17** ou superior (usa `record` e `Stream.toList()`)
- Sem bibliotecas externas e sem banco de dados: os dados ficam em memória

## Como executar

Primeiro confira se o Java 17 ou superior está instalado:

```bash
java -version
```

### macOS e Linux

Se o Java não estiver instalado no macOS, instale pelo [Homebrew](https://brew.sh):

```bash
brew install openjdk@17
sudo ln -sfn "$(brew --prefix)/opt/openjdk@17/libexec/openjdk.jdk" /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

No Terminal:

```bash
git clone https://github.com/AntonioNeto029/Biblioteca-java.git
cd Biblioteca-java
javac -d out $(find src -name "*.java")
java -cp out Main
java -cp out testes.TesteBiblioteca
```

- `javac` compila todos os arquivos de `src` para a pasta `out`
- `java -cp out Main` roda a demonstração do sistema
- `java -cp out testes.TesteBiblioteca` roda os testes

### Windows (PowerShell)

Se o Java não estiver instalado, instale pelo `winget` (já vem no Windows 10 e 11):

```powershell
winget install EclipseAdoptium.Temurin.17.JDK
```

Outra opção é baixar o instalador `.msi` do JDK 17 em [adoptium.net](https://adoptium.net) e, durante a instalação, marcar **Set JAVA_HOME** e **Add to PATH**. Depois de instalar, feche e abra o PowerShell de novo e confira com `java -version`.

Dentro da pasta do repositório:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out Main
java -cp out testes.TesteBiblioteca
```

## Estrutura

```
src/
├── Main.java                  demonstração de todas as funcionalidades
├── modelos/
│   ├── Livro.java             record imutável: id, título, autor, categoria
│   ├── Usuario.java           record imutável: id, nome, matrícula
│   ├── Emprestimo.java        record imutável: livro, usuário e datas
│   └── Biblioteca.java        gerencia cadastros, empréstimos e devoluções
├── funcional/
│   ├── Filtros.java           filtragem de livros e de empréstimos atrasados
│   ├── Multas.java            cálculo de multa e relatório de multas
│   ├── LinhaMulta.java        linha do relatório
│   ├── RelatorioMultas.java   itens do relatório e total
│   ├── Estatisticas.java      estatísticas da biblioteca
│   └── Comparacao.java        versão imperativa x versão funcional
└── testes/
    └── TesteBiblioteca.java   testes das regras de negócio
```

## Funcionalidades

### Gerenciamento (`Biblioteca`)

- Cadastrar e consultar livros e usuários (matrícula não pode repetir)
- Registrar empréstimo com prazo padrão de 7 dias
- Impedir que um livro já emprestado seja emprestado de novo
- Registrar devolução, deixando o livro disponível outra vez
- Consultar empréstimos (todos ou só os ativos)

### Processamento funcional

| Funcionalidade | Onde | Operações |
|---|---|---|
| Filtrar livros por categoria | `Filtros.filtrarLivrosPorCategoria` | `filter` |
| Calcular multa (R$ 2,00 por dia) | `Multas.calcularMulta` | função pura |
| Selecionar empréstimos ativos em atraso | `Filtros.filtrarEmprestimosAtrasados` | `filter` |
| Relatório de multas | `Multas.gerarRelatorioMultas` | `filter`, `map`, `reduce` |
| Livros por categoria / empréstimos por usuário | `Estatisticas.contarPor` | `reduce` |
| Empréstimos ativos e ativos em atraso | `Estatisticas` | `filter` |
| Valor total das multas | `Estatisticas.valorTotalMultas` | `reduce` (via relatório) |

## Regras de negócio

- Cada empréstimo tem um único livro e um único usuário; um usuário pode ter vários empréstimos.
- Cada livro é um exemplar único e não pode estar emprestado para duas pessoas ao mesmo tempo.
- Um empréstimo está **atrasado** quando ainda não foi devolvido e a data de referência é posterior à data prevista. No próprio dia do prazo não há atraso.
- A multa é de **R$ 2,00 por dia de atraso**. Empréstimos devolvidos ou dentro do prazo não geram multa.
- O relatório de multas recebe a data de referência como parâmetro.

## Conceitos aplicados

### Orientação a Objetos

- **Encapsulamento:** os mapas de livros, usuários e empréstimos são `private final` dentro de `Biblioteca`. Só é possível alterá-los pelos métodos de cadastro, empréstimo e devolução, que validam as regras.
- **Relacionamentos:** `Emprestimo` referencia um `Livro` e um `Usuario`; `Biblioteca` agrega todos os objetos.
- **Responsabilidades:** `modelos` guarda as entidades e o estado; `funcional` guarda os cálculos, sem estado.

### Programação Funcional

- **Funções puras:** `calcularMulta`, `filtrarLivrosPorCategoria`, `filtrarEmprestimosAtrasados`, `gerarRelatorioMultas` e as funções de `Estatisticas`. Dependem só dos parâmetros e não alteram nada fora delas.
- **Imutabilidade:** as entidades são `record` (campos `final`). A devolução não altera o empréstimo: `devolvidoEm` cria um novo objeto. As consultas devolvem cópias imutáveis (`List.copyOf`, `toList()`, `Map.copyOf`).
- **Separação de efeitos:** as operações que alteram estado ficam em `Biblioteca`; as funções de `funcional` só leem dados e devolvem novas estruturas.

### Comparação entre paradigmas

`Comparacao.java` calcula o total de multas de dois jeitos:

- `totalMultasImperativo`: laço `for`, `if` e variável acumuladora `total` que muda a cada volta.
- `totalMultasFuncional`: `filter` → `map` → `reduce`, sem variável mutável.

As duas versões produzem o mesmo resultado (R$ 46.00 na demonstração), o que é conferido nos testes.

## Exemplo de saída

```
Relatório de multas:
#1 | Clean Code | Ana Souza | 14 dia(s) | R$ 28.00
#2 | Dom Casmurro | Bruno Lima | 9 dia(s) | R$ 18.00
TOTAL: R$ 46.00

Imperativo: 46.00
Funcional : 46.00
```
