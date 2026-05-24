package packagee.validator;

/**
 * Interfaz para la validación de hospitalizaciones.
 * Abstracción que permite cumplir con DIP.
 * 
 * @author Issa
 */
public interface IHospitalizationValidator {
    /**
     * Valida que la fecha estimada tenga el formato AAAA-MM-DD y sea válida.
     */
    boolean validateEstimateDate(String date);

    /**
     * Valida que el motivo de la hospitalización no esté vacío.
     */
    boolean validateReason(String reason);
}
