package packagee.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
/**
 * Validador de propósito único para fechas.
 * Centraliza la lógica de validación de formato AAAA-MM-DD para ser
 *
 * @author Issa
 */
public class DateValidator {

    /**
     * Valida que una fecha tenga el formato AAAA-MM-DD y sea una fecha real.
     * Ejemplo válido: 1990-04-15, 2025-12-31
     */
    public static boolean validateDate(String date) {
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
