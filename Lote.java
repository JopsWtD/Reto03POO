import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lote implements Reporte {
    public static final int LIMITE_RECHAZOS_CONSECUTIVOS = 3;

    private final int tamano;
    private final List<Pieza> piezas;
    private int rechazadasConsecutivas;

    public Lote(int tamano) throws TamanoLoteInvalidoException {
        if (tamano <= 0) {
            throw new TamanoLoteInvalidoException();
        }
        this.tamano = tamano;
        this.piezas = new ArrayList<>();
    }

    public void registrar(double medida, EstadoPieza estado) throws MedidaInvalidaException {
        if (estaCompleto()) {
            throw new LoteCompletoException();
        }

        piezas.add(new Pieza(medida, estado));

        if (estado == EstadoPieza.RECHAZADA) {
            rechazadasConsecutivas++;
        } else {
            rechazadasConsecutivas = 0;
        }
    }

    public boolean debeDetenerse() {
        return rechazadasConsecutivas >= LIMITE_RECHAZOS_CONSECUTIVOS;
    }

    public boolean estaCompleto() {
        return piezas.size() == tamano;
    }

    public int getTamano() {
        return tamano;
    }

    public int getInspeccionadas() {
        return piezas.size();
    }

    public List<Pieza> getPiezas() {
        return new ArrayList<>(piezas);
    }

    public int contar(EstadoPieza estado) {
        int total = 0;
        for (Pieza p : piezas) {
            if (p.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }

    public double porcentajeAprobacion() {
        if (piezas.isEmpty()) {
            return 0;
        }
        return (100.0 * contar(EstadoPieza.APROBADA)) / piezas.size();
    }

    public double promedioMedidas() {
        if (piezas.isEmpty()) {
            return 0;
        }
        double suma = 0;
        for (Pieza p : piezas) {
            suma += p.getMedida();
        }
        return suma / piezas.size();
    }

    public double medidaMaxima() {
        if (piezas.isEmpty()) {
            return 0;
        }
        double maxima = piezas.get(0).getMedida();
        for (Pieza p : piezas) {
            if (p.getMedida() > maxima) {
                maxima = p.getMedida();
            }
        }
        return maxima;
    }

    public double medidaMinima() {
        if (piezas.isEmpty()) {
            return 0;
        }
        double minima = piezas.get(0).getMedida();
        for (Pieza p : piezas) {
            if (p.getMedida() < minima) {
                minima = p.getMedida();
            }
        }
        return minima;
    }

    @Override
    public String generarReporte() {
        StringBuilder detalle = new StringBuilder();
        int i = 1;
        for (Pieza p : piezas) {
            detalle.append("Pieza ").append(i).append(": ").append(p.generarReporte()).append("\n");
            i++;
        }

        return String.format(Locale.ROOT, """
                        ================REPORTE FINAL================
                        TAMAÑO DEL LOTE: %d
                        %s---------------------------------------------
                        PIEZAS APROBADAS: %d
                        PIEZAS REPROCESADAS: %d
                        PIEZAS RECHAZADAS: %d
                        TOTAL DE PIEZAS INSPECCIONADAS: %d de %d
                        PORCENTAJE DE APROBACIÓN: %.2f%%
                        PROMEDIO DE MEDIDAS: %.2f
                        ---------------------------------------------
                        MEDIDA MÁXIMA: %s
                        MEDIDA MÍNIMA: %s
                        =============================================""",
                tamano, detalle,
                contar(EstadoPieza.APROBADA), contar(EstadoPieza.REPROCESADA), contar(EstadoPieza.RECHAZADA),
                getInspeccionadas(), tamano, porcentajeAprobacion(), promedioMedidas(),
                medidaMaxima(), medidaMinima());
    }
}