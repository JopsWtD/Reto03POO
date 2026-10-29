public enum EstadoPieza {
    APROBADA("Dentro del rango requerido (9.8-10.2) mm"),
    REPROCESADA("Dentro de (9.5-<9.8) o (>10.2-10.5) mm"),
    RECHAZADA("Fuera del rango de revisión (9.5-10.5) mm");

    private final String razon;

    EstadoPieza(String razon) {
        this.razon = razon;
    }

    public String getRazon() {
        return razon;
    }
}