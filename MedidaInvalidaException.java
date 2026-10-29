public class MedidaInvalidaException extends Exception {
    public MedidaInvalidaException() {
        super("La medida de una pieza debe ser un número positivo y finito.");
    }
}