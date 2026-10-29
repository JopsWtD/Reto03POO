public class Revisor {
    private static final double MIN_REVISION = 9.5;
    private static final double MAX_REVISION = 10.5;
    private static final double MIN_APROBADA = 9.8;
    private static final double MAX_APROBADA = 10.2;

    public EstadoPieza clasificar(double medida) {
        if (medida < MIN_REVISION || medida > MAX_REVISION) {
            return EstadoPieza.RECHAZADA;
        }
        if (medida < MIN_APROBADA || medida > MAX_APROBADA) {
            return EstadoPieza.REPROCESADA;
        }
        return EstadoPieza.APROBADA;
    }
}