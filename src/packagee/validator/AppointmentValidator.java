package packagee.validator;

/**
 * Validador para las propiedades de una Cita Médica. Valida fechas, horas y que
 * los minutos sean cuartos de hora exactos.
 * Implementa IAppointmentValidator para cumplir con DIP.
 *
 * @author Issa
 */
public class AppointmentValidator implements IAppointmentValidator {

    private final IDateValidator dateValidator;

    /**
     * Constructor con Inyección de Dependencia de IDateValidator.
     */
    public AppointmentValidator(IDateValidator dateValidator) {
        this.dateValidator = dateValidator;
    }

    /**
     * Valida que la fecha tenga el formato AAAA-MM-DD y sea una fecha real.
     * Delega en la abstracción IDateValidator inyectada.
     */
    @Override
    public boolean validateDate(String date) {
        return dateValidator.validateDate(date);
    }

    /**
     * Valida que la hora tenga el formato hh:mm en 24 horas. 
     */
    @Override
    public boolean validateTimeFormat(String time) {
        if (time == null) {
            return false;
        }
        return time.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
    }

    /**
     * Valida que los minutos de la hora sean exactamente cuartos de hora: 00,
     * 15, 30 o 45. Se debe llamar DESPUÉS de validateTimeFormat.
     */
    @Override
    public boolean validateQuarterMinutes(String time) {
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
    @Override
    public boolean validateTime(String time) {
        return validateTimeFormat(time) && validateQuarterMinutes(time);
    }
}
