package packagee.validator;

/**
 * Validador para las propiedades de una Hospitalización.
 *
 * @author Issa
 */
public class HospitalizationValidator {

    /**
     * Valida que la fecha estimada tenga el formato AAAA-MM-DD y sea válida.
     * Delega en DateValidator para mantener el principio de responsabilidad única.
     */
    public static boolean validateEstimateDate(String date) {
        return DateValidator.validateDate(date);
    }

    /**
     * Valida que el motivo de la hospitalización no esté vacío.
     */
    public static boolean validateReason(String reason) {
        return reason != null && !reason.trim().isEmpty();
    }
}
