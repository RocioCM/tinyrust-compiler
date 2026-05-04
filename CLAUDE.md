# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

TinyRust+ compiler — a Java implementation of a compiler for the TinyRust+ language (a Rust-inspired toy language with classes/inheritance). Output is MIPS assembly (intended for SPIM). Source files use the `.rs` extension. All identifiers, comments, error messages, and docs are in Spanish — keep that convention when editing.

## Build & Run

The project is plain Java (no Maven/Gradle). Default toolchain is JDK 17.

Build the JAR from the repo root:

```bash
javac $(find . -name "*.java")
jar cfm Main.jar Manifest.txt $(find . -name "*.class")
```

The legacy README still shows the older `jar cfvm etapaX.jar Manifest.txt *` form (per-stage JARs `etapa1`/`etapa2`/etc.). The current entry point ([main.java](main.java)) wires directly to `code_generator.Executor`, so a single `Main.jar` is the canonical build today.

Run:

```bash
java -jar Main.jar <input.rs> [<output.asm>]
```

The input file **must** end in `.rs`. If `<output.asm>` is omitted, the ASM is written next to the input as `<basename>.asm`. Two side-effect files are also written: `<basename>.ts.json` (symbol table dump) and `<basename>.ast.json` (AST dump). All three are gitignored.

Docker (no local JDK needed):

```bash
docker build -t tinyrust-compiler .
docker run --rm -v "$(pwd):/app/work" -w /app/work tinyrust-compiler "tests/code_generation/test (1).rs"
```

## Tests

There is **no test runner** — tests are `.rs` source files under [tests/](tests/) grouped by stage:

- [tests/lexic/](tests/lexic/)
- [tests/syntactic/](tests/syntactic/)
- [tests/semantic_declarations/](tests/semantic_declarations/)
- [tests/semantic_sentences/](tests/semantic_sentences/)
- [tests/code_generation/](tests/code_generation/)

Run a single test by passing its path to the JAR:

```bash
java -jar Main.jar "tests/code_generation/test (3).rs"
```

Filenames contain spaces and parentheses — quote them. Verifying behavior is manual: inspect stdout, the generated `.asm`, or run the ASM in SPIM.

## Architecture

The compiler is a four-pass pipeline. Each pass has its own package and an `Executor` class, but only the top-level [code_generator/Executor.java](code_generator/Executor.java) is wired to `main` today — it drives all earlier stages. The lexic/syntactic stage `Executor` classes are kept for stage-by-stage testing.

Pipeline (driven by [code_generator/Executor.java](code_generator/Executor.java)):

1. **Lexical** — [lexic_analyzer/LexicAnalyzer.java](lexic_analyzer/LexicAnalyzer.java) is a hand-rolled scanner. `nextToken()` / `hasNextToken()` are the public surface; reserved words and primitive type names are reclassified post-hoc by [lexic_analyzer/ReservedWords.java](lexic_analyzer/ReservedWords.java).
2. **Syntactic** — [syntactic_analyzer/SyntacticAnalyzer.java](syntactic_analyzer/SyntacticAnalyzer.java) is a recursive-descent predictive parser; one private method per grammar rule. `matchLexema`/`matchToken` consume; `isFirstL`/`isFirstT` peek. **Side effect:** during parsing it populates both the `SymbolTable` and the `AbstractSyntaxTree` passed into its constructor — there is no separate "build AST" pass.
3. **Semantic — declarations** — `SymbolTable.consolidate()` ([semantic_analyzer/symbol_table/SymbolTable.java](semantic_analyzer/symbol_table/SymbolTable.java)) walks the inheritance graph, rejects circular inheritance, then merges inherited attrs/methods into each `ClassEntry`. After this returns, `isConsolidated()` is true and the AST validation pass is allowed to run.
4. **Semantic — sentences** — `ast.validate(ts)` ([semantic_analyzer/ast/AbstractSyntaxTree.java](semantic_analyzer/ast/AbstractSyntaxTree.java)) type-checks every node. It must run **after** consolidation (the AST guards this with an `InternalError`).
5. **Code generation** — `ast.generateCode(ts)` returns a MIPS assembly string. [util/Code.java](util/Code.java) is the ASM builder helper (stack push/pop, frame pointer setup, syscall emitters, the `.data` constants block). The top-level emitter wires `main`, the shared `cleanup_method` epilogue, and runtime error trampolines (`error_zero_division`, `error_array_out_of_bounds`).

### Predefined classes

The language has built-in classes: `Object`, `IO`, `I32`, `Str`, `Char`, `Bool`, `Array`. They are injected in two places — keep them in sync:

- Symbol table: [semantic_analyzer/symbol_table/predefined_classes/](semantic_analyzer/symbol_table/predefined_classes/) (one file per class) — registered in `SymbolTable`'s constructor.
- AST: `AbstractSyntaxTree.addPredefinedClasses()` — registered when `setClasses()` is called by the parser. Method bodies here are raw ASM strings produced via `util.Code` helpers (e.g. `IO.out_i32` uses syscall 1, `IO.in_i32` uses syscall 5).

### Errors

Each stage throws its own exception hierarchy and the top-level `Executor` has a `catch` per stage to label the error. Order of `catch` blocks matters — `ASTError` (sentences) extends `SemanticalError` and is caught first.

- [error/lexic/](error/lexic/) — `LexicalError` and subclasses (BadIdentifier, InvalidCharacter, InvalidLiteral, UnclosedMultiLineComment, UnmatchedToken).
- [error/syntactic/](error/syntactic/) — `SyntacticalError` / `UnexpectedToken`.
- [error/semantic/declarations/](error/semantic/declarations/) — consolidation/declaration errors (circular inheritance, duplicate IDs, bad constructors, illegal `self`).
- [error/semantic/sentences/](error/semantic/sentences/) — `ASTError`, `NotFoundError`, `UnmatchedTypeError`, `InternalError` (the last is for compiler invariants — throw it when the compiler itself reaches an inconsistent state).

Analyzers do **not** attempt error recovery — the first error aborts the run.

### Output via Logger

[util/Logger.java](util/Logger.java) chooses stdout vs file based on whether `outputPath` was passed. `Logger.createFile` overwrites; `Logger.success`/`Logger.error` append. The current `code_generator.Executor` deliberately routes diagnostics to stdout (passes `null` as the output path) and only uses `outputPath` for the generated `.asm`.

### JSON output

[util/Json.java](util/Json.java) is a tiny manual JSON builder. Both `SymbolTable` and `AbstractSyntaxTree` (and their nested entries/nodes) implement `toJson()` and are emitted as `<basename>.ts.json` / `<basename>.ast.json` for inspection — useful when debugging a failing test.

## Conventions

- Tabs for indentation in `.java` files (matches existing source).
- Spanish for new identifiers, comments, and user-facing strings.
- Each pipeline stage is a package with a top-level class plus an `Executor`. When adding a new stage or sub-feature, follow that layout.
- Reference docs (PDFs, grammar, FIRST/FOLLOW sets) live in [docs/](docs/) — `grammar.txt`, `primeros.txt`, `siguientes.txt` are the authoritative grammar references when modifying the parser.
