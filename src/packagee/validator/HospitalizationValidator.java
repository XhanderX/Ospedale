package packagee.validator;

/**
 * Validador para las propiedades de una Hospitalización.
 * Implementa IHospitalizationValidator para cumplir con DIP.
 *
 * @author Issa
 */
public class HospitalizationValidator implements IHospitalizationValidator {

    private final IDateValidator dateValidator;

    /**
     * Constructor con Inyección de Dependencia de IDateValidator.
     */
    public HospitalizationValidator(IDateValidator dateValidator) {
        this.dateValidator = dateValidator;
    }

    /**
     * Valida que la fecha estimada tenga el formato AAAA-MM-DD y sea válida.
     * Delega en la abstracción IDateValidator inyectada.
     */
    @Override
    public boolean validateEstimateDate(String date) {
        return dateValidator.validateDate(date);
    }

    /**
     * Valida que el motivo de la hospitalización no esté vacío.
     */
    @Override
    public boolean validateReason(String reason) {
        return reason != null && !reason.trim().isEmpty();
    }
}
