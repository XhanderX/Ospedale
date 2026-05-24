package packagee.validator;

/**
 * Validador para las propiedades específicas de un Paciente.
 * Extiende las validaciones base de UserValidator.
 *
 * @author Issa
 */
public class PatientValidator extends UserValidator {

    /**
     * Valida que el teléfono tenga exactamente 10 dígitos numéricos.
     */
    public static boolean validatePhone(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^\\d{10}$");
    }

    /**
     * Valida que el email tenga un formato válido (ejemplo@dominio.com).
     */
    public static boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Valida que la fecha de nacimiento tenga el formato AAAA-MM-DD y sea válida.
     * Delega en DateValidator para mantener el principio de responsabilidad única.
     */
    public static boolean validateBirthdate(String birthdate) {
        return DateValidator.validateDate(birthdate);
    }
}
