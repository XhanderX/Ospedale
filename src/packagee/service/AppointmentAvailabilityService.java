package packagee.service;

/**
 * Implementación concreta del Servicio de Disponibilidad de Citas.
 * Gestiona el control de disponibilidad de franjas horarias y la asignación automática.
 * Implementa IAppointmentAvailabilityService para cumplir con DIP.
 *
 * @author Issa
 */
public class AppointmentAvailabilityService implements IAppointmentAvailabilityService {

    /**
     * Constructor del servicio.
     * En la integración futura, este constructor recibirá la interfaz de persistencia (storage)
     * para poder consultar las citas guardadas y los doctores registrados.
     */
    public AppointmentAvailabilityService() {
        // TODO: Inyectar almacenamiento (ej. IHospitalStorage storage) para consultar datos reales.
    }

    /**
     * Valida si un doctor específico tiene libre la franja de 15 minutos solicitada en una fecha dada.
     *
     * @param doctorId ID del doctor a verificar.
     * @param date Fecha de la cita (AAAA-MM-DD).
     * @param time Hora de la cita (hh:mm en formato 24h).
     * @return true si la franja está disponible, false si está ocupada.
     */
    @Override
    public boolean checkAvailability(long doctorId, String date, String time) {
        // TODO: Cargar la lista completa de citas desde packagee.storage.
        // A. Filtrar citas correspondientes a la fecha dada y al doctorId indicado.
        // B. Comprobar que no exista una cita en la misma hora exacta (con duración de 15 minutos).
        
        // Contrato temporal: por defecto, asumimos que siempre hay disponibilidad.
        return true;
    }

    /**
     * Asigna automáticamente un doctor disponible de una determinada especialidad en la fecha y hora indicadas.
     *
     * @param specialty Especialidad médica requerida.
     * @param date Fecha solicitada.
     * @param time Hora solicitada.
     * @return ID del doctor asignado, o null si ningún doctor de esa especialidad está disponible.
     */
    @Override
    public Long assignDoctorBySpecialty(String specialty, String date, String time) {
        // TODO: Consultar todos los doctores registrados en packagee.storage.
        // A. Filtrar doctores que tengan la especialidad especificada.
        // B. Para cada doctor que coincida con la especialidad, llamar a checkAvailability() con la fecha y hora.
        // C. Retornar el ID del primer doctor disponible encontrado.
        // D. Si ningún doctor tiene disponibilidad, retornar null.
        
        // Contrato temporal: devuelve un ID de doctor ficticio de 12 dígitos como demostración si la especialidad no está vacía.
        if (specialty != null && !specialty.trim().isEmpty()) {
            return 987654321012L; // ID ficticio de 12 dígitos
        }
        return null;
    }
}
