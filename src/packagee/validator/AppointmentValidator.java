package packagee.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validador para las propiedades de una Cita Médica. Valida fechas, horas y que
 * los minutos sean cuartos de hora exactos.
 *
 * @author Issa
 */
public class AppointmentValidator {

    /**
     * Valida que la fecha tenga el formato AAAA-MM-DD y sea una fecha real.
     * Delega en DateValidator para mantener el principio de responsabilidad única.
     */
    public static boolean validateDate(String date) {
        return DateValidator.validateDate(date);
    }

    /**
     * Valida que la hora tenga el formato hh:mm en 24 horas. 
     */
    public static boolean validateTimeFormat(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    /**
     * Valida que los minutos de la hora sean exactamente cuartos de hora: 00,
     * 15, 30 o 45. Se debe llamar DESPUÉS de validateTimeFormat.
     */
    public static boolean validateQuarterMinutes(String time) {
        if (time == null || time.length() < 5) {
            return false;
        }
        try {
            int minutes = Integer.parseInt(time.substring(3, 5));
            return minutes == 0 || minutes == 15 || minutes == 30 || minutes == 45;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valida la hora completa: formato correcto Y minutos en cuartos de hora.
     */
    public static boolean validateTime(String time) {
        return validateTimeFormat(time) && validateQuarterMinutes(time);
    }
}
