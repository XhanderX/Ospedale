package packagee.validator;

/**
 * Interfaz para la validación de propiedades específicas de doctores.
 * Abstracción que permite cumplir con DIP.
 * 
 * @author Issa
 */
public interface IDoctorValidator {
    /**
     * Valida que el número de licencia médica tenga el formato L-XXXXXXXXXX MTL.
     */
    boolean validateLicenceNumber(String licenceNumber);

    /**
     * Valida que la oficina asignada tenga el formato O-XXX.
     */
    boolean validateAssignedOffice(String assignedOffice);
}
