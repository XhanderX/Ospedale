package packagee.validator;

/**
 * Validador para las propiedades específicas de un Doctor.
 * Extiende las validaciones base de UserValidator.
 *
 * @author Issa
 */
public class DoctorValidator extends UserValidator {

    /**
     * Valida que el número de licencia médica tenga el formato: L-XXXXXXXXXX MTL
     * donde X son exactamente 10 dígitos numéricos.
     */
    public static boolean validateLicenceNumber(String licenceNumber) {
        if (licenceNumber == null) {
            return false;
        }
        return licenceNumber.matches("^L-\\d{10} MTL$");
    }

    /**
     * Valida que la oficina asignada tenga el formato: O-XXX
     * donde X son exactamente 3 dígitos numéricos.
     */
    public static boolean validateAssignedOffice(String assignedOffice) {
        if (assignedOffice == null) {
            return false;
        }
        return assignedOffice.matches("^O-\\d{3}$");
    }
}
