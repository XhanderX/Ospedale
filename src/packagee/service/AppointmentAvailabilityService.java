package packagee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Specialty;
import packagee.storage.AppointmentRepository;
import packagee.storage.UserRepository;

/**
 * Implementación concreta del Servicio de Disponibilidad de Citas.
 * Verifica franjas de 15 minutos y asigna doctores por especialidad.
 * Implementa IAppointmentAvailabilityService para cumplir con DIP.
 *
 * @author Issa
 */
public class AppointmentAvailabilityService implements IAppointmentAvailabilityService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     * Depende de las interfaces de repositorio, no de implementaciones concretas.
     *
     * @param appointmentRepository Repositorio de citas.
     * @param userRepository        Repositorio de usuarios (para buscar doctores por especialidad).
     */
    public AppointmentAvailabilityService(AppointmentRepository appointmentRepository,
                                          UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Verifica si el doctor tiene libre la franja de 15 minutos solicitada en una fecha dada.
     * Carga todas las citas del doctor en esa fecha y compara la hora exacta.
     *
     * @param doctorId ID del doctor a verificar.
     * @param date     Fecha de la cita (AAAA-MM-DD).
     * @param time     Hora de la cita (hh:mm en formato 24h).
     * @return true si la franja está disponible, false si ya existe una cita en esa hora.
     */
    @Override
    public boolean checkAvailability(long doctorId, String date, String time) {
        LocalDate targetDate = LocalDate.parse(date);
        int targetHour = Integer.parseInt(time.substring(0, 2));
        int targetMinute = Integer.parseInt(time.substring(3, 5));

        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorId(doctorId);

        for (Appointment appointment : doctorAppointments) {
            LocalDateTime dt = appointment.getDatetime();

            // Comparamos
            if (dt.toLocalDate().equals(targetDate)
                    && dt.getHour() == targetHour
                    && dt.getMinute() == targetMinute) {
                return false; // La franja ocupada
            }
        }
        return true; // La franja  libre
    }

    /**
     * Asigna automáticamente el primer doctor disponible con la especialidad indicada
     * en la fecha y hora solicitadas.
     *
     * @param specialty Nombre del valor del enum Specialty (ej. "CARDIOLOGY").
     * @param date      Fecha solicitada (AAAA-MM-DD).
     * @param time      Hora solicitada (hh:mm).
     * @return ID del doctor asignado, o null si ninguno está disponible.
     */
    @Override
    public Long assignDoctorBySpecialty(String specialty, String date, String time) {
        Specialty targetSpecialty;
        try {
            targetSpecialty = Specialty.valueOf(specialty);
        } catch (IllegalArgumentException e) {
            return null; // Especialidad inválida
        }

        List<Doctor> doctors = userRepository.findAllDoctors();

        for (Doctor doctor : doctors) {
            if (doctor.getSpecialty() == targetSpecialty) {
                if (checkAvailability(doctor.getId(), date, time)) {
                    return doctor.getId(); // Primer doctor disponible con esa especialidad
                }
            }
        }

        return null; // Ningún doctor disponible con esa especialidad en esa franja
    }
}
