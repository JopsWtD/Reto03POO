# Sistema de revisión de piezas por lotes

Versión orientada a objetos del reto de control de calidad (S03 · Reto 03). El usuario indica cuántas piezas trae un lote, digita la medida de cada una y el sistema decide si se aprueba, se reprocesa o se rechaza. Al final genera un reporte con las estadísticas del lote.

## Reglas de calidad

| Estado | Rango (mm) |
|---|---|
| `APROBADA` | 9.8 a 10.2 |
| `REPROCESADA` | 9.5 a menos de 9.8, o más de 10.2 a 10.5 |
| `RECHAZADA` | Menos de 9.5 o más de 10.5 |

El tamaño del lote debe ser mayor que cero y cada medida debe ser positiva y finita. Si hay **3 rechazos consecutivos**, la línea se detiene y se genera el reporte con lo inspeccionado hasta ese momento.

## Ejecución

Requiere **JDK 17 o superior**.

```bash
javac -encoding UTF-8 *.java
java S03Reto03Calidad
```

Las medidas se pueden digitar con punto o con coma (`9.6` o `9,6`).

## Estructura

```text
.
├── S03Reto03Calidad.java            → Main
├── SistemaRevision.java             → Consola y coordinación del proceso
├── Revisor.java                     → Reglas de calidad: medida → estado
├── Lote.java                        → Piezas, rechazos consecutivos y estadísticas
├── Pieza.java                       → Medida y estado de una pieza
├── EstadoPieza.java                 → Enum de estados con su razón
├── Reporte.java                     → Interfaz de reporte
├── MedidaInvalidaException.java
├── TamanoLoteInvalidoException.java
└── LoteCompletoException.java
```

## Diagrama de clases

```mermaid
classDiagram
    direction TB

    class Reporte {
        <<interface>>
        +generarReporte() String
    }

    class EstadoPieza {
        <<enumeration>>
        APROBADA
        REPROCESADA
        RECHAZADA
        -String razon
        +getRazon() String
    }

    class Pieza {
        -double medida
        -EstadoPieza estado
        +Pieza(double medida, EstadoPieza estado)
        +getMedida() double
        +getEstado() EstadoPieza
        +generarReporte() String
    }

    class Lote {
        +int LIMITE_RECHAZOS_CONSECUTIVOS$
        -int tamano
        -List~Pieza~ piezas
        -int rechazadasConsecutivas
        +Lote(int tamano)
        +registrar(double medida, EstadoPieza estado) void
        +debeDetenerse() boolean
        +estaCompleto() boolean
        +getTamano() int
        +getInspeccionadas() int
        +getPiezas() List~Pieza~
        +contar(EstadoPieza estado) int
        +porcentajeAprobacion() double
        +promedioMedidas() double
        +medidaMaxima() double
        +medidaMinima() double
        +generarReporte() String
    }

    class Revisor {
        -double MIN_REVISION$
        -double MAX_REVISION$
        -double MIN_APROBADA$
        -double MAX_APROBADA$
        +clasificar(double medida) EstadoPieza
    }

    class SistemaRevision {
        -int ANCHO_RECUADRO$
        -Scanner scanner
        -Revisor revisor
        +SistemaRevision()
        +iniciar() void
        -mostrarBienvenida() void
        -crearLote() Lote
        -registrarPieza(Lote lote, int numero) EstadoPieza
        -mostrarAviso(EstadoPieza estado) void
        -imprimirRecuadro(String titulo, String[] lineas) void
        -centrar(String texto, int ancho, char relleno) String
    }

    class S03Reto03Calidad {
        +main(String[] args)$ void
    }

    Reporte <|.. Pieza
    Reporte <|.. Lote
    Lote "1" *-- "0..*" Pieza : contiene
    Pieza --> "1" EstadoPieza : estado
    SistemaRevision "1" *-- "1" Revisor : revisor
    SistemaRevision ..> Lote : crea y usa
    Revisor ..> EstadoPieza : devuelve
    S03Reto03Calidad ..> SistemaRevision : inicia
```

| Relación | Tipo | Motivo |
|---|---|---|
| `Pieza`, `Lote` → `Reporte` | Realización | Implementan la interfaz |
| `Lote` → `Pieza` | Composición | El lote crea sus piezas en `registrar()` |
| `Pieza` → `EstadoPieza` | Asociación | Atributo `estado` |
| `SistemaRevision` → `Revisor` | Composición | Lo crea en su constructor y nadie más lo tiene |
| `SistemaRevision` → `Lote` | Dependencia | Variable local, no atributo |
| `Revisor` → `EstadoPieza` | Dependencia | Solo lo devuelve |
| `S03Reto03Calidad` → `SistemaRevision` | Dependencia | Variable local en `main` |

## Excepciones

```mermaid
classDiagram
    direction TB

    class Exception {
        <<Java>>
    }
    class RuntimeException {
        <<Java>>
    }
    class MedidaInvalidaException {
        +MedidaInvalidaException()
    }
    class TamanoLoteInvalidoException {
        +TamanoLoteInvalidoException()
    }
    class LoteCompletoException {
        +LoteCompletoException()
    }

    Exception <|-- RuntimeException
    Exception <|-- MedidaInvalidaException
    Exception <|-- TamanoLoteInvalidoException
    RuntimeException <|-- LoteCompletoException

    Pieza ..> MedidaInvalidaException : lanza
    Lote ..> TamanoLoteInvalidoException : lanza
    Lote ..> LoteCompletoException : lanza
    SistemaRevision ..> MedidaInvalidaException : captura
    SistemaRevision ..> TamanoLoteInvalidoException : captura
```

| Excepción | Tipo | Cuándo ocurre | Quién la maneja |
|---|---|---|---|
| `MedidaInvalidaException` | Checked | Medida cero, negativa, `NaN` o infinita | `SistemaRevision`: vuelve a pedir la medida |
| `TamanoLoteInvalidoException` | Checked | Tamaño cero o negativo | `SistemaRevision`: vuelve a pedir el tamaño |
| `LoteCompletoException` | Unchecked | Registrar en un lote ya completo | Nadie: es un error de programación |
| `NumberFormatException` | Unchecked (Java) | El texto no es un número | `SistemaRevision` |

Las *checked* son errores del usuario, de los que el programa se recupera. La *unchecked* solo puede ocurrir por un error en el código, por eso no se captura.

## Flujo del programa

```mermaid
flowchart TD
    A([Inicio]) --> B[Mostrar bienvenida]
    B --> C[/Leer cantidad de piezas/]
    C --> D{¿Es un número entero?}
    D -- No --> E[ERROR: solo números enteros] --> C
    D -- Sí --> F{¿Es mayor que cero?}
    F -- No --> G[TamanoLoteInvalidoException] --> C
    F -- Sí --> H[Crear el lote]
    H --> I{¿El lote está completo?}
    I -- Sí --> R[Imprimir reporte final]
    I -- No --> J[/Leer medida de la pieza/]
    J --> K{¿Es un número?}
    K -- No --> L[ERROR: solo números decimales] --> J
    K -- Sí --> M[El revisor clasifica la medida]
    M --> N{¿Es positiva y finita?}
    N -- No --> O[MedidaInvalidaException] --> J
    N -- Sí --> P[Registrar la pieza y mostrar el aviso]
    P --> Q{¿3 rechazos consecutivos?}
    Q -- Sí --> S[Alerta: detener la línea] --> R
    Q -- No --> I
    R --> Z([Fin])
```

## Secuencia al registrar una pieza

```mermaid
sequenceDiagram
    actor Usuario
    participant SR as SistemaRevision
    participant R as Revisor
    participant L as Lote
    participant P as Pieza

    Usuario->>SR: digita una medida
    SR->>SR: Double.parseDouble(texto)
    SR->>R: clasificar(medida)
    R-->>SR: EstadoPieza
    SR->>L: registrar(medida, estado)
    L->>P: new Pieza(medida, estado)

    alt medida válida
        P-->>L: pieza creada
        L->>L: agrega la pieza y actualiza los rechazos consecutivos
        L-->>SR: registro exitoso
        SR->>Usuario: aviso con el estado y estado.getRazon()
    else medida cero, negativa, NaN o infinita
        P--xL: lanza MedidaInvalidaException
        L--xSR: la excepción se propaga
        SR->>Usuario: muestra el ERROR y vuelve a pedir la medida
    end
```

## Decisiones de diseño

- **Enum en vez de booleanos:** una sola variable de estado, y cada estado trae su razón.
- **Sin contadores:** los totales y estadísticas se calculan desde la lista de piezas, así nunca se desincronizan. Solo `rechazadasConsecutivas` es contador, porque depende de la secuencia.
- **Atributo `tamano`:** es el tamaño declarado; `piezas.size()` es lo inspeccionado. Si la línea se detiene, el reporte muestra, por ejemplo, "5 de 6".
- **`Pieza` inmutable:** atributos `final`, validados en el constructor y sin setters.
- **`Locale.ROOT`:** los decimales siempre salen con punto, sin importar el idioma del equipo.

## Ejemplo de reporte

```text
================REPORTE FINAL================
TAMAÑO DEL LOTE: 6
Pieza 1: 10.0 | APROBADA
Pieza 2: 9.6 | REPROCESADA
Pieza 3: 11.0 | RECHAZADA
Pieza 4: 12.0 | RECHAZADA
Pieza 5: 10.6 | RECHAZADA
---------------------------------------------
PIEZAS APROBADAS: 1
PIEZAS REPROCESADAS: 1
PIEZAS RECHAZADAS: 3
TOTAL DE PIEZAS INSPECCIONADAS: 5 de 6
PORCENTAJE DE APROBACIÓN: 20.00%
PROMEDIO DE MEDIDAS: 10.64
---------------------------------------------
MEDIDA MÁXIMA: 12.0
MEDIDA MÍNIMA: 9.6
=============================================
```
