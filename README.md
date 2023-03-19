# TinyRust+

En este repositorio se encuentra la implementación del compilador del lenguaje de programación TinyRust+.

## Compilado y ejecución

Para compilar el proyecto, deben ejecutarse los siguientes comandos desde el directorio raiz del proyecto:

```bash
javac *.java
jar cfvm etapa1.jar Manifest.txt *
```

Para ejecutar el proyecto debe ejecutarse el siguiente comando:

```bash
java -jar etapa1.jar <ARCHIVO_FUENTE> [<ARCHIVO_SALIDA>]
```

Es obligatorio especificar la ruta del archivo fuente que TinyRust+ compilará. En el directorio /tests se pueden hallar archivos de prueba para alimentar el comando. Comando de ejemplo:

```bash
java -jar etapa1.jar ./tests/"test (1).rs" ./output/example.o
```

Especificar un archivo de salida es opcional. En caso de ser especificado, es escribirá la salida del compilador en dicho archivo. Si el archivo existe previamente será sobreescrito, caso contrario será creado por el compilador. En caso de no especificarse este parámetro, la salida se mostrará por pantalla.

## Estructura

La clase de entrada del proyecto se encuentra en el archivo [main.java](./main.java). Esta clase se encarga de validar los argumentos especificados para la ejecución e inicializa el archivo de salida en caso de haber sido especificado. En caso de haber recibido una cantidad válida de parámetros, inicializa e invoca a un Executor.

### Executor

En esta primera etapa, la clase [Executor](./lexic_analyzer/Executor.java) será la encargada de instanciar el analizador léxico de TinyRust+, proveerle una ruta de archivo de entrada y solicitarle tokens de este archivo. El ejecutor guardará en una lista los tokens recibidos del analizador léxico y los mostrará en una tabla si se consume sin errores la totalidad del archivo. En caso de que el analizador léxico arrojara una excepción durante su ejecución, el ejecutor automáticamente deja de solicitarle tokens al analizador y procede a mostrar el error hallado y finalizar la ejecución.

#### Logger

La clase Logger provee métodos estáticos para hacer output de información. La clase Executor se vale del Logger para mostrar la tabla de tokens o los errores antes de finalizar su ejecución. Esta clase provee dos métodos distintos para mostrar con distinto formato los mensajes de éxito y falla en la ejecución del analizador léxico.

El Logger es encargado de determinar si debe enviar la salida a un archivo (si se especificó en el comando) o si debe mostrarla por pantalla.

### LexicAnalyzer

La clase LexicAnalyzer implementa la lógica propia del Analizador Léxico de TinyRust+, esto incluye: abrir y consumir el archivo de entrada y retornar tokens de este archivo a demanda.

Al instanciarse la clase, se abre el archivo de entrada y queda listo para comenzar a consumirse al identificar tokens. LexicAnalyzer implementa el método público nextToken que consume parte del archivo de entrada hasta identificar un token y devolverlo. Al llegar hasta el final del archivo, si este método se vuelve a invocar, devolverá un Token especial de tipo "EOF" representando que ya se ha consumido la totalidad del archivo.

Esta clase cuenta con otro método público hasNextToken que se puede llamar para verificar si ya se ha llegado al final del archivo.

La clase cuenta con distintos métodos privados para: leer caracteres del archivo de entrada con y sin consumirlos; validar si un caracter pertenece al alfabeto de entrada de TinyRust+; validar si un caracter pertenece a un grupo en particular; o validar si el contenido consumido del archivo de entrada coincide con cierto token en particular.

La clase LexicAnalyzer utiliza también funcionalidades implementadas en las clases LexicalError, Token y ReservedWords.

#### Errores

Durante la identificación de un token, existen ciertos errores que el analizador léxico puede lanzar debido al contenido presente en el archivo de entrada. Luego de lanzar un error, el analizador detiene su ejecución. Todos los errores lanzados por LexicAnalyzer heredan de la clase LexicalError. Estos errores posibles son:

- Bad Identifier: si se encuentra un identificador con caracteres inválidos.
- Invalid Character: si el archivo de entrada contiene un caracter que no pertenece al alfabeto de entrada de TinyRust+.
- Unclosed MultiLine Comment: si se encuentra una apertura de comentario multilinea y se llega al final del archivo antes de encontrar el cierre del comentario.
- Unmatched Token: si se encuentra una secuencia de caracteres que no coincide con ningún token de TinyRust+. Por ejemplo: `&`, `|`, `@var`, etc.
- Invalid Literal: si se encuentran literales cadena o caracter mal formados. Por ejemplo, un literal caracter con más de un caracter, una cadena con un caracter nulo o un literal caracter o cadena sin cerrar.

#### Token

La clase Token representa cualquier token del lenguaje TinyRust+. Registra el número de linea y columna de comienzo del token, identificador de tipo de token y su lexema.

#### ReservedWords

La clase ReservedWords implementa un método estático que recibe un Token de tipo identificador y valida (y lo actualiza si corresponde) si el identificador es en realidad una palabra reservada.
