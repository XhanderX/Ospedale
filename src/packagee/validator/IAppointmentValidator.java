package packagee.validator;

/**
 * Interfaz para la validación de citas médicas.
 * Abstracción que permite cumplir con DIP.
 * 
 * @author Issa
 */
public interface IAppointmentValidator {
    /**
     * Valida que la fecha tenga el formato AAAA-MM-DD y sea una fecha real.
     */
    boolean validateDate(String date);

    /**
     * Valida que la hora tenga el formato hh:mm de 24 horas.
     */
    boolean validateTimeFormat(String time);

    /**
     * Valida que los minutos de la hora sean exactamente cuartos de hora: 00, 15, 30 o 45.
     */
    boolean validateQuarterMinutes(String time);

    /**
     * Valida la hora completa: formato correcto Y minutos en cuartos de hora.
     */
    boolean validateTime(String time);
}
