package packagee.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validador de propósito único para fechas. Implementa IDateValidator para
 * cumplir con DIP.
 *
 * @author Issa
 */
public class DateValidator implements IDateValidator {

    /**
     * Valida que una fecha tenga el formato AAAA-MM-DD y sea una fecha real.
     * Ejemplo válido: 1990-04-15, 2025-12-31
     */
    @Override
    public boolean validateDate(String date) {
        if (date == null) {
            return false;
        }
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            // Intentamos convertir el texto a una fecha real
            LocalDate parsedDate = LocalDate.parse(date, formatter);

            if (parsedDate.getYear() < 1900) {
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
