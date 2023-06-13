# TinyRust+

[WIP] - Esta documentación es un trabajo en progreso, por lo cual está incompleta.

En este repositorio se encuentra la implementación del compilador del lenguaje de programación TinyRust+.

## Compilado y ejecución

Para compilar el proyecto, deben ejecutarse los siguientes comandos desde el directorio raiz del proyecto:

```bash
javac *.java
jar cfvm etapaX.jar Manifest.txt *
```

Para ejecutar el proyecto debe ejecutarse el siguiente comando:

```bash
java -jar etapaX.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]
```

Es obligatorio especificar la ruta del archivo fuente que TinyRust+ compilará. En el directorio /tests se pueden hallar archivos de prueba para alimentar el comando. Comando de ejemplo:

```bash
java -jar etapa2.jar ./tests/syntactic/"test (3).rs" ./example.o
```

Especificar un archivo de salida es opcional. En caso de ser especificado, es escribirá la salida del compilador en dicho archivo. Si el archivo existe previamente será sobreescrito, caso contrario será creado por el compilador. En caso de no especificarse este parámetro, la salida se mostrará por pantalla.

## Estructura

La clase de entrada del proyecto se encuentra en el archivo [main.java](./main.java). Esta clase se encarga de validar los argumentos especificados para la ejecución e inicializa el archivo de salida en caso de haber sido especificado. En caso de haber recibido una cantidad válida de parámetros, inicializa e invoca a un Executor.

### Executor

Para cada etapa del análisis, se incluye una clase Executor distinta que será la encargada de instanciar el analizador de esa etapa de compilado de TinyRust+ (ya sea análisis léxico, sintáctico o semántico). Esta clase permite ejecutar el análisis hasta esa etapa únicamente (incluyendo etapas inferiores, dado que son parte necesaria del análisis). Por ejemplo, el Executor del análisis sintáctico aprobará archivos que son correctos léxica y sintácticamente, pero no necesariamente lo son a nivel semántico.
Cada clase Executor provee al analizador una ruta de archivo de entrada y se encarga de mostrar la salida en el formato y medio correcto. En caso de que el analizador arrojara una excepción durante su ejecución, el ejecutor automáticamente procede a mostrar el error hallado y finalizar la ejecución.

### Logger

La clase Logger provee métodos estáticos para hacer output de información. Executor se vale del Logger para datos y mensajes de éxito o los errores antes de finalizar su ejecución. Esta clase provee distintos métodos para mostrar con distinto formato los mensajes de éxito y falla en la ejecución de cada etapa de análisis: léxico, sintáctico y semántico.

El Logger es encargado de determinar si debe enviar la salida a un archivo (si se especificó en el comando) o si debe mostrarla por pantalla.

### LexicAnalyzer

La clase LexicAnalyzer implementa la lógica propia del Analizador Léxico de TinyRust+, esto incluye: abrir y consumir el archivo de entrada y retornar tokens de este archivo a demanda.

Al instanciarse la clase, se abre el archivo de entrada y queda listo para comenzar a consumirse al identificar tokens. LexicAnalyzer implementa el método público nextToken que consume parte del archivo de entrada hasta identificar un token y devolverlo. Al llegar hasta el final del archivo, si este método se vuelve a invocar, devolverá un Token especial de tipo "EOF" representando que ya se ha consumido la totalidad del archivo.

Esta clase cuenta con otro método público hasNextToken que se puede llamar para verificar si ya se ha llegado al final del archivo.

La clase cuenta con distintos métodos privados para: leer caracteres del archivo de entrada con y sin consumirlos; validar si un caracter pertenece al alfabeto de entrada de TinyRust+; validar si un caracter pertenece a un grupo en particular; o validar si el contenido consumido del archivo de entrada coincide con cierto token en particular.

La clase LexicAnalyzer utiliza también funcionalidades implementadas en las clases LexicalError, Token y ReservedWords.

### SyntacticAnalyzer

La clase SyntacticAnalyzer implementa la lógica propia de un analizador sintáctico descendente predictivo recursivo de TinyRust+.

Al instanciarse la clase, SyntacticAnalyzer inicializa una instancia de LexicAnalyzer pasandole la ruta del archivo de entrada, para luego solicitarle sucesivos tokens del archivo. SyntacticAnalyzer implementa el método público `run` que comienza el proceso de análisis sintactico del archivo utilizando la gramática de TinyRust+. Durante el análisis sintáctico, se irán consumiendo tokens del analizador léxico y se verificará que el archivo de entrada sea derivable a partir del símbolo inicial de la gramática, llamando recursivamente métodos de la clase que implementan cada regla de la gramática.

Si el método `run` se invoca sucesivas veces, no funcionará de la forma esperada, dado que en la primer ejecución del método, el archivo de entrada puede haber sido consumido parcial o totalmente por el analizador léxico asociado a esa instancia de la clase, por lo que el análisis no comenzará desde el principio del archivo.

La clase cuenta con distintos métodos privados:

- Posee un método privado para cada regla de la gramática de TinyRust+ utilizada, implementando su lógica.
- Posee dos métodos para consumir un token que sea de cierto tipo o posea cierto lexema. Estos métodos (matchLexema y matchToken) lanzarán una excepción si el token leído no coincide con el tipo o lexema esperado, ya que esto indicaría que no cumple con las reglas de la gramática esperadas.
- Posee dos métodos para validar si un token es de cierto tipo o posee cierto lexema. Estos métodos (isFirstL e isFirstT) devuelven un valor booleando indicando si el token leído coincide o no con el tipo o lexema esperado. La diferencia con los métodos anteriores es que estos últimos leen el token sin consumirlo.

#### Errores

Durante el análisis de un archivo de entrada, existen distintos errores que pueden aparecer en cada etapa del proceso debido al contenido del archivo. Cada analizador (léxico, sintáctico o semántico) detendrá su ejecución luego de lanzar un error, sin intentar recuperarse.

Todos los errores lanzados por LexicAnalyzer heredan de la clase LexicalError. Estos errores posibles son:

- Bad Identifier: si se encuentra un identificador con caracteres inválidos.
- Invalid Character: si el archivo de entrada contiene un caracter que no pertenece al alfabeto de entrada de TinyRust+.
- Unclosed MultiLine Comment: si se encuentra una apertura de comentario multilinea y se llega al final del archivo antes de encontrar el cierre del comentario.
- Unmatched Token: si se encuentra una secuencia de caracteres que no coincide con ningún token de TinyRust+. Por ejemplo: `&`, `|`, `@var`, etc.
- Invalid Literal: si se encuentran literales cadena o caracter mal formados. Por ejemplo, un literal caracter con más de un caracter, una cadena con un caracter nulo o un literal caracter o cadena sin cerrar.

Los errores lanzados por la clase SyntacticAnalyzer heredan de la clase SyntacticalError. El único error que esta clase arroja es Unexpected Token, cuando se encuentra un token que no coincide con ninguna regla gramatical de TinyRust+ (teniendo en cuenta la secuencia previa de tokens). Este error incluye un completo mensaje especificando el token que se encontró y el o los tokens que se esperaba encontrar en ese contexto.

#### Token

La clase Token representa cualquier token del lenguaje TinyRust+. Registra el número de linea y columna de comienzo del token, identificador de tipo de token y su lexema.

#### ReservedWords

La clase ReservedWords implementa un método estático que recibe un Token de tipo identificador y valida (y lo actualiza si corresponde) si el identificador es en realidad una palabra reservada. También implementa una funcionalidad similar para validar si un token de tipo identificador de clase es en realidad un tipo primitivo de datos de TinyRust+.
