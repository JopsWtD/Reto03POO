import java.util.Locale;
import java.util.Scanner;

public class SistemaRevision {
    private static final int ANCHO_RECUADRO = 43;

    private final Scanner scanner;
    private final Revisor revisor;

    public SistemaRevision() {
        this.scanner = new Scanner(System.in);
        this.revisor = new Revisor();
    }

    public void iniciar() {
        try {
            mostrarBienvenida();

            Lote lote = crearLote();

            for (int i = 1; !lote.estaCompleto(); i++) {
                EstadoPieza estado = registrarPieza(lote, i);
                mostrarAviso(estado);

                if (lote.debeDetenerse()) {
                    imprimirRecuadro("ALERTA",
                            "DETENER LA LÍNEA DE PRODUCCIÓN",
                            "-",
                            "SE LLEGÓ A " + Lote.LIMITE_RECHAZOS_CONSECUTIVOS + " RECHAZOS CONSECUTIVOS");
                    break;
                }
            }

            System.out.println(lote.generarReporte());
        } finally {
            scanner.close();
        }
    }

    private void mostrarBienvenida() {
        imprimirRecuadro("BIENVENIDO",
                "AL SISTEMA DE REVISIÓN DE",
                "PIEZAS POR LOTES",
                "-",
                "A CONTINUACIÓN:",
                "Podrás digitar la cantidad de piezas",
                "que contiene el lote que quieres",
                "revisar.");
    }

    private Lote crearLote() {
        while (true) {
            System.out.println("Digite la cantidad de piezas del lote a revisar: ");
            String texto = scanner.nextLine();

            try {
                int cantidad = Integer.parseInt(texto.strip());
                return new Lote(cantidad);
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Solo puede digitar números enteros... Intente nuevamente.\n" +
                        "Valor insertado: " + texto);
            } catch (TamanoLoteInvalidoException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private EstadoPieza registrarPieza(Lote lote, int numero) {
        while (true) {
            System.out.println("Digite la medida (en milímetros) de la pieza " + numero + ": ");
            String texto = scanner.nextLine();

            try {
                double medida = Double.parseDouble(texto.strip().replace(',', '.'));
                EstadoPieza estado = revisor.clasificar(medida);
                lote.registrar(medida, estado);
                System.out.println("VALOR OBTENIDO: " + medida);
                return estado;
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Solo se puede digitar números decimales... Intente nuevamente.\n" +
                        "Valor insertado: " + texto);
            } catch (MedidaInvalidaException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private void mostrarAviso(EstadoPieza estado) {
        imprimirRecuadro("AVISO",
                "ESTA PIEZA FUE " + estado,
                "-",
                "RAZÓN:",
                estado.getRazon().toUpperCase(Locale.ROOT));
        System.out.println();
    }

    private void imprimirRecuadro(String titulo, String... lineas) {
        System.out.println(centrar(titulo, ANCHO_RECUADRO + 2, '='));
        for (String linea : lineas) {
            if (linea.equals("-")) {
                System.out.println("|" + "-".repeat(ANCHO_RECUADRO) + "|");
            } else {
                System.out.println("|" + centrar(linea, ANCHO_RECUADRO, ' ') + "|");
            }
        }
        System.out.println("=".repeat(ANCHO_RECUADRO + 2));
    }

    private String centrar(String texto, int ancho, char relleno) {
        int sobrante = ancho - texto.length();
        if (sobrante <= 0) {
            return texto;
        }
        int izquierda = sobrante / 2;
        int derecha = sobrante - izquierda;
        String caracter = String.valueOf(relleno);
        return caracter.repeat(izquierda) + texto + caracter.repeat(derecha);
    }
}