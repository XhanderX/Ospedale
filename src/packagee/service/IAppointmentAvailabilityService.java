package packagee.service;

/**
 * Interfaz para el Servicio de Disponibilidad de Citas Médicas.
 * Abstracción que define las reglas para verificar horarios de doctores y asignaciones automáticas.
 * Cumple con SOLID (DIP).
 *
 * @author Issa
 */
public interface IAppointmentAvailabilityService {

    /**
     * Valida si un doctor específico tiene libre la franja de 15 minutos solicitada en una fecha dada.
     *
     * @param doctorId ID del doctor a verificar.
     * @param date Fecha de la cita (AAAA-MM-DD).
     * @param time Hora de la cita (hh:mm en formato 24h).
     * @return true si la franja está disponible, false si está ocupada.
     */
    boolean checkAvailability(long doctorId, String date, String time);

    /**
     * Asigna automáticamente un doctor disponible de una determinada especialidad en la fecha y hora indicadas.
     *
     * @param specialty Especialidad médica requerida.
     * @param date Fecha solicitada.
     * @param time Hora solicitada.
     * @return ID del doctor asignado, o null si ningún doctor de esa especialidad está disponible.
     */
    Long assignDoctorBySpecialty(String specialty, String date, String time);
}
