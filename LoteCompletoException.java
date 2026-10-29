public class LoteCompletoException extends RuntimeException {
    public LoteCompletoException(int tamano) {
        super("El lote ya tiene registradas sus " + tamano + " piezas.");
    }
}