package packagee.validator;

/**
 * Interfaz para la validación de propiedades específicas de pacientes.
 * Abstracción que permite cumplir con DIP.
 * 
 * @author Issa
 */
public interface IPatientValidator {
    /**
     * Valida que el teléfono tenga exactamente 10 dígitos numéricos.
     */
    boolean validatePhone(String phone);

    /**
     * Valida que el email tenga un formato válido.
     */
    boolean validateEmail(String email);

    /**
     * Valida que la fecha de nacimiento tenga el formato AAAA-MM-DD y sea válida.
     */
    boolean validateBirthdate(String birthdate);
}
