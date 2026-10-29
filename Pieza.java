public class Pieza implements Reporte {
    private final double medida;
    private final EstadoPieza estado;

    public Pieza(double medida, EstadoPieza estado) throws MedidaInvalidaException {
        if (medida <= 0 || !Double.isFinite(medida)) {
            throw new MedidaInvalidaException();
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la pieza es obligatorio.");
        }
        this.medida = medida;
        this.estado = estado;
    }

    public double getMedida() {
        return medida;
    }

    public EstadoPieza getEstado() {
        return estado;
    }

    @Override
    public String generarReporte() {
        return medida + " | " + estado;
    }
}