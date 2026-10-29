public class TamanoLoteInvalidoException extends Exception {
    public TamanoLoteInvalidoException() {
        super("El lote de piezas a revisar no puede ser ni cero ni negativo.");
    }
}