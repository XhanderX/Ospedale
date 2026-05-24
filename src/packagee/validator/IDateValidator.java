package packagee.validator;

/**
 * Interfaz para la validación de fechas.
 * Abstracción que permite cumplir con DIP.
 * 
 * @author Issa
 */
public interface IDateValidator {
    /**
     * Valida que una fecha tenga el formato AAAA-MM-DD y sea válida.
     */
    boolean validateDate(String date);
}
