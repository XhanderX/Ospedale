package packagee.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import packagee.dto.AppointmentDTO;
import packagee.dto.PrescriptionDTO;
import packagee.mapper.ModelMapper;
import packagee.model.Appointment;
import packagee.model.AppointmentStatus;
import packagee.model.AppointmentType;
import packagee.model.Doctor;
import packagee.model.Patient;
import packagee.model.Prescription;
import packagee.model.Specialty;
import packagee.model.User;
import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.service.IAppointmentAvailabilityService;
import packagee.storage.AppointmentRepository;
import packagee.storage.IdGenerator;
import packagee.storage.UserRepository;
import packagee.validator.IAppointmentValidator;
import packagee.validator.IUserValidator;

/**
 * Controlador de Citas Médicas. Gestiona todo el ciclo de vida de una cita y
 * las prescripciones asociadas. Cumple con SOLID (DIP al depender de interfaces
 * en su constructor).
 *
 * @author Issa
 */
public class AppointmentController {

    private final IAppointmentValidator appointmentValidator;
    private final IUserValidator userValidator;
    private final IAppointmentAvailabilityService availabilityService;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     *
     * @param appointmentValidator Validador de fecha y hora de citas.
     * @param userValidator Validador de datos de usuarios.
     * @param availabilityService Servicio de disponibilidad horaria.
     * @param appointmentRepository Repositorio de citas.
     * @param userRepository Repositorio de usuarios.
     * @param idGenerator Generador de IDs de citas.
     */
    public AppointmentController(IAppointmentValidator appointmentValidator,
            IUserValidator userValidator,
            IAppointmentAvailabilityService availabilityService,
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            IdGenerator idGenerator) {
        this.appointmentValidator = appointmentValidator;
        this.userValidator = userValidator;
        this.availabilityService = availabilityService;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * Solicita una nueva cita médica. Genera el ID con formato
     * A-{id_paciente}-NNNN y la crea en estado REQUESTED. Si no se especifica
     * doctor, se asigna automáticamente por especialidad.
     *
     * @param patientId ID del paciente (12 dígitos).
     * @param doctorId ID del doctor (opcional, null para asignar por
     * especialidad).
     * @param specialty Especialidad solicitada (valor del enum Specialty).
     * @param date Fecha de la cita (AAAA-MM-DD).
     * @param time Hora de la cita (hh:mm, 24h, minutos en cuartos).
     * @param reason Motivo de la consulta.
     * @param type Tipo de cita ("REMOTE" o "IN_PERSON").
     * @return Response con AppointmentDTO en caso de éxito.
     */
    public Response<AppointmentDTO> requestAppointment(long patientId, Long doctorId,
            String specialty, String date,
            String time, String reason, String type) {
        // 1. Validar ID del paciente
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        // 2. Validar ID de doctor si fue proporcionado
        if (doctorId != null && !userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        // 3. Validar especialidad
        Specialty appointmentSpecialty = null;
        if (doctorId == null) {
            try {
                appointmentSpecialty = Specialty.valueOf(specialty.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return Response.error(StatusCode.INVALID_DATA, "La especialidad no es válida.");
            }
        }

        // 4. Validar fecha (AAAA-MM-DD)
        if (!appointmentValidator.validateDate(date)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de la cita no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // 5. Validar hora (hh:mm, 24h, cuartos de hora)
        if (!appointmentValidator.validateTime(time)) {
            return Response.error(StatusCode.INVALID_DATA, "La hora debe estar en formato 24h hh:mm y en cuartos de hora (00, 15, 30, 45).");
        }

        // 6. Validar motivo
        if (reason == null || reason.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la cita no puede estar vacío.");
        }

        // 7. Validar tipo de cita
        AppointmentType appointmentType;
        try {
            appointmentType = AppointmentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de cita debe ser REMOTE o IN_PERSON.");
        }

        // 8. Buscar paciente
        Optional<User> patientFound = userRepository.findById(patientId);
        if (!patientFound.isPresent() || !(patientFound.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un paciente con ese ID.");
        }
        Patient patient = (Patient) patientFound.get();

        // 9. Resolver doctor: por ID directo o por asignación automática
        Doctor doctor;

if (doctorId != null) {
    Optional<User> doctorFound = userRepository.findById(doctorId);
    if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
        return Response.error(StatusCode.INVALID_DATA, "No se encontro un doctor con ese ID.");
    }

    doctor = (Doctor) doctorFound.get();
    appointmentSpecialty = doctor.getSpecialty();
} else {
    Long assignedId = availabilityService.assignDoctorBySpecialty(specialty.toUpperCase(), date, time);
    if (assignedId == null) {
        return Response.error(StatusCode.DOCTOR_UNAVAILABLE, "No hay doctores disponibles con esa especialidad en la franja horaria solicitada.");
    }

    Optional<User> assignedFound = userRepository.findById(assignedId);
    if (!assignedFound.isPresent() || !(assignedFound.get() instanceof Doctor)) {
        return Response.error(StatusCode.DOCTOR_UNAVAILABLE, "No se pudo asignar un doctor disponible.");
    }

    doctor = (Doctor) assignedFound.get();
}

        // 10. Verificar disponibilidad del doctor en la franja horaria
        if (!availabilityService.checkAvailability(doctor.getId(), date, time)) {
            return Response.error(StatusCode.DOCTOR_UNAVAILABLE, "El doctor no tiene disponibilidad en la franja horaria solicitada.");
        }

        // 11. Construir LocalDateTime combinando fecha y hora
        LocalDate localDate = LocalDate.parse(date);
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        LocalDateTime datetime = LocalDateTime.of(localDate, LocalTime.of(hour, minute));

        // 12. Generar ID y crear la cita en estado REQUESTED
        String appointmentId = idGenerator.nextAppointmentId(patientId);
        Appointment appointment = new Appointment(appointmentId, patient, doctor,
                appointmentSpecialty, datetime, reason, appointmentType);

        // 13. Guardar y retornar DTO
        appointmentRepository.save(appointment);
        AppointmentDTO dto = ModelMapper.toAppointmentDTO(appointment);
        return Response.success("Cita médica solicitada exitosamente con ID: " + appointmentId, dto);
    }

    /**
     * Acepta una cita solicitada. Pasa su estado de REQUESTED a PENDING.
     *
     * @param appointmentId ID de la cita.
     * @return Response con AppointmentDTO actualizado.
     */
    public Response<AppointmentDTO> acceptAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontró la cita con ese ID.");
        }

        Appointment appointment = found.get();

        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede aceptar una cita en estado REQUESTED. Estado actual: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appointment);
        return Response.success("Cita aceptada. Estado actualizado a PENDING.", ModelMapper.toAppointmentDTO(appointment));
    }

    /**
     * Completa una cita médica. Pasa su estado de PENDING a COMPLETED.
     *
     * @param appointmentId ID de la cita.
     * @return Response con AppointmentDTO actualizado.
     */
    public Response<AppointmentDTO> completeAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontró la cita con ese ID.");
        }

        Appointment appointment = found.get();

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede completar una cita en estado PENDING. Estado actual: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        return Response.success("Cita completada exitosamente.", ModelMapper.toAppointmentDTO(appointment));
    }

    /**
     * Cancela una cita médica. Pasa su estado a CANCELED. No se puede cancelar
     * una cita ya COMPLETED o CANCELED.
     *
     * @param appointmentId ID de la cita.
     * @return Response con AppointmentDTO actualizado.
     */
    public Response<AppointmentDTO> cancelAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontró la cita con ese ID.");
        }

        Appointment appointment = found.get();

        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "No se puede cancelar una cita en estado " + appointment.getStatus() + ".");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
        return Response.success("Cita cancelada exitosamente.", ModelMapper.toAppointmentDTO(appointment));
    }

    /**
     * Reagenda una cita a una nueva hora del mismo día. No se puede cambiar la
     * fecha, solo la hora. La razón de reprogramación se concatena a la razón
     * original con "|".
     *
     * @param appointmentId ID de la cita.
     * @param newTime Nueva hora (hh:mm, 24h, cuartos de hora).
     * @param reason Razón de la reprogramación.
     * @return Response con AppointmentDTO actualizado.
     */
    public Response<AppointmentDTO> rescheduleAppointment(String appointmentId, String newTime, String reason) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // 1. Validar formato de nueva hora
        if (!appointmentValidator.validateTime(newTime)) {
            return Response.error(StatusCode.INVALID_DATA, "La nueva hora debe estar en formato 24h hh:mm y en cuartos de hora (00, 15, 30, 45).");
        }

        // 2. Validar razón
        if (reason == null || reason.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La razón de la reprogramación no puede estar vacía.");
        }

        // 3. Buscar la cita
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontró la cita con ese ID.");
        }

        Appointment appointment = found.get();

        // 4. Verificar estado reprogramable (REQUESTED o PENDING)
        if (appointment.getStatus() != AppointmentStatus.REQUESTED
                && appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede reprogramar una cita en estado REQUESTED o PENDING. Estado actual: " + appointment.getStatus());
        }

        // 5. Verificar disponibilidad del doctor en la nueva hora (mismo día)
        String date = appointment.getDatetime().toLocalDate().toString();
        if (!availabilityService.checkAvailability(appointment.getDoctor().getId(), date, newTime)) {
            return Response.error(StatusCode.DOCTOR_UNAVAILABLE, "El doctor no tiene disponibilidad en la nueva franja horaria.");
        }

        // 6. Construir nuevo LocalDateTime conservando el mismo día
        int hour = Integer.parseInt(newTime.substring(0, 2));
        int minute = Integer.parseInt(newTime.substring(3, 5));
        LocalDateTime newDatetime = LocalDateTime.of(appointment.getDatetime().toLocalDate(), LocalTime.of(hour, minute));

        // 7. Actualizar hora y concatenar razón
        appointment.setDatetime(newDatetime);
        appointment.appendReason(reason); // Usa el método appendReason del modelo que concatena con "|"

        appointmentRepository.save(appointment);
        return Response.success("Cita reprogramada exitosamente.", ModelMapper.toAppointmentDTO(appointment));
    }

    /**
     * Prescribe medicamentos a una cita en estado PENDING.
     *
     * @param appointmentId ID de la cita.
     * @param medicationName Nombre del medicamento.
     * @param dose Dosis del medicamento (mayor que 0).
     * @param adminRoute Vía de administración.
     * @param duration Duración del tratamiento en días (mayor que 0).
     * @param instructions Instrucciones adicionales.
     * @param frequency Frecuencia de toma en horas (mayor que 0).
     * @return Response con PrescriptionDTO en caso de éxito.
     */
    public Response<PrescriptionDTO> prescribeMedications(String appointmentId, String medicationName,
            double dose, String adminRoute, int duration,
            String instructions, int frequency) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // 1. Validaciones del medicamento
        if (medicationName == null || medicationName.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre del medicamento no puede estar vacío.");
        }
        if (dose <= 0) {
            return Response.error(StatusCode.INVALID_DATA, "La dosis debe ser mayor que 0.");
        }
        if (adminRoute == null || adminRoute.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La vía de administración no puede estar vacía.");
        }
        if (duration <= 0) {
            return Response.error(StatusCode.INVALID_DATA, "La duración del tratamiento debe ser de al menos 1 día.");
        }
        if (frequency <= 0) {
            return Response.error(StatusCode.INVALID_DATA, "La frecuencia debe ser mayor que 0 horas.");
        }

        // 2. Buscar la cita
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontró la cita con ese ID.");
        }

        Appointment appointment = found.get();

        // 3. Verificar que la cita esté en PENDING
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se pueden prescribir medicamentos en una cita en estado PENDING. Estado actual: " + appointment.getStatus());
        }

        // 4. Crear la prescripción y vincularla a la cita
        Prescription prescription = new Prescription(medicationName, dose, adminRoute,
                duration, instructions, frequency);
        appointment.addPrescription(prescription);
        appointmentRepository.save(appointment);

        PrescriptionDTO dto = ModelMapper.toPrescriptionDTO(prescription);
        return Response.success("Medicamento prescrito exitosamente.", dto);
    }
}
