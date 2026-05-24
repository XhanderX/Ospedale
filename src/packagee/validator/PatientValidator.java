package packagee.validator;

/**
 * Validador para las propiedades específicas de un Paciente.
 * Implementa IPatientValidator para cumplir con DIP.
 *
 * @author Issa
 */
public class PatientValidator implements IPatientValidator {

    private final IDateValidator dateValidator;

    /**
     * Constructor con Inyección de Dependencia de IDateValidator.
     */
    public PatientValidator(IDateValidator dateValidator) {
        this.dateValidator = dateValidator;
    }

    /**
     * Valida que el teléfono tenga exactamente 10 dígitos numéricos.
     */
    @Override
    public boolean validatePhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^\\d{10}$");
    }

    /**
     * Valida que el email tenga un formato válido (ejemplo@dominio.com).
     */
    @Override
    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Valida que la fecha de nacimiento tenga el formato AAAA-MM-DD y sea válida.
     * Delega en la abstracción IDateValidator inyectada.
     */
    @Override
    public boolean validateBirthdate(String birthdate) {
        return dateValidator.validateDate(birthdate);
    }
}
