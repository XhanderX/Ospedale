package packagee.response;

/**
 * Códigos de estado requeridos para la rama 2 (feature/controllers-business-rules)
 * y la rúbrica de evaluación.
 * listado de todas las posibles respuestas lógicas
 * @author Issa
 */
public enum StatusCode {
    SUCCESS,
    INVALID_CREDENTIALS,
    INVALID_DATA,
    DUPLICATE_USER,
    DOCTOR_UNAVAILABLE,
    APPOINTMENT_NOT_FOUND,
    HOSPITALIZATION_NOT_FOUND,
    INVALID_STATE_TRANSITION,
    NOT_IMPLEMENTED // Código temporal para integración de almacenamiento
}
