package packagee.validator;

/**
 * Validador para las propiedades específicas de un Doctor.
 * Implementa IDoctorValidator para cumplir con DIP.
 *
 * @author Issa
 */
public class DoctorValidator implements IDoctorValidator {

    /**
     * Valida que el número de licencia médica tenga el formato: L-XXXXXXXXXX MTL
     * donde X son exactamente 10 dígitos numéricos.
     */
    @Override
    public boolean validateLicenceNumber(String licenceNumber) {
        if (licenceNumber == null) {
            return false;
        }
        return licenceNumber.matches("^L-\\d{10} MTL$");
    }

    /**
     * Valida que la oficina asignada tenga el formato: O-XXX
     * donde X son exactamente 3 dígitos numéricos.
     */
    @Override
    public boolean validateAssignedOffice(String assignedOffice) {
        if (assignedOffice == null) {
            return false;
        }
        return assignedOffice.matches("^O-\\d{3}$");
    }
}
