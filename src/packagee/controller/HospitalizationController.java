package packagee.controller;

import java.time.LocalDate;
import java.util.Optional;
import packagee.dto.HospitalizationDTO;
import packagee.mapper.ModelMapper;
import packagee.model.Appointment;
import packagee.model.AppointmentStatus;
import packagee.model.Doctor;
import packagee.model.Hospitalization;
import packagee.model.HospitalizationStatus;
import packagee.model.Patient;
import packagee.model.RoomType;
import packagee.model.User;
import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.storage.AppointmentRepository;
import packagee.storage.HospitalizationRepository;
import packagee.storage.IdGenerator;
import packagee.storage.UserRepository;
import packagee.validator.IHospitalizationValidator;
import packagee.validator.IUserValidator;

/**
 * Controlador de Hospitalizaciones.
 * Gestiona el ciclo de vida completo: solicitud, aprobación, cancelación e internación directa.
 * Cumple con SOLID (DIP al depender de interfaces en su constructor).
 *
 * @author Issa
 */
public class HospitalizationController {

    private final IHospitalizationValidator hospitalizationValidator;
    private final IUserValidator userValidator;
    private final HospitalizationRepository hospitalizationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     *
     * @param hospitalizationValidator  Validador de datos de hospitalización.
     * @param userValidator             Validador de datos de usuario.
     * @param hospitalizationRepository Repositorio de hospitalizaciones.
     * @param appointmentRepository     Repositorio de citas (para internación directa).
     * @param userRepository            Repositorio de usuarios.
     * @param idGenerator               Generador de IDs de hospitalizaciones.
     */
    public HospitalizationController(IHospitalizationValidator hospitalizationValidator,
                                     IUserValidator userValidator,
                                     HospitalizationRepository hospitalizationRepository,
                                     AppointmentRepository appointmentRepository,
                                     UserRepository userRepository,
                                     IdGenerator idGenerator) {
        this.hospitalizationValidator = hospitalizationValidator;
        this.userValidator = userValidator;
        this.hospitalizationRepository = hospitalizationRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * Solicita una hospitalización para un paciente.
     * Se crea en estado REQUESTED con ID H-{id_paciente}-NNNN.
     * El doctor y la habitación se asignan en la etapa de aprobación.
     *
     * @param patientId    ID del paciente (12 dígitos).
     * @param estimateDate Fecha estimada de admisión (AAAA-MM-DD).
     * @param reason       Motivo de la hospitalización.
     * @return Response con HospitalizationDTO en caso de éxito.
     */
    public Response<HospitalizationDTO> requestHospitalization(long patientId, String estimateDate, String reason) {
        // 1. Validar ID del paciente
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        // 2. Validar fecha estimada (AAAA-MM-DD)
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada de admisión no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // 3. Validar motivo
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalización no puede estar vacío.");
        }

        // 4. Buscar paciente en repositorio
        Optional<User> found = userRepository.findById(patientId);
        if (!found.isPresent() || !(found.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un paciente con ese ID.");
        }
        Patient patient = (Patient) found.get();

        // 5. Generar ID, crear hospitalización en REQUESTED (sin doctor ni habitación aún)
        String hospitalizationId = idGenerator.nextHospitalizationId(patientId);
        LocalDate date = LocalDate.parse(estimateDate);
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId, patient, null, date, reason, null, null, HospitalizationStatus.REQUESTED);

        // 6. Guardar y retornar DTO
        hospitalizationRepository.save(hospitalization);
        HospitalizationDTO dto = ModelMapper.toHospitalizationDTO(hospitalization);
        return Response.success("Hospitalización solicitada exitosamente con ID: " + hospitalizationId, dto);
    }

    /**
     * Aprueba una hospitalización solicitada. Asigna habitación y doctor, cambia estado a ONGOING.
     *
     * @param hospitalizationId ID de la hospitalización.
     * @param doctorId          ID del doctor que atenderá la hospitalización.
     * @param roomType          Tipo de habitación (valor del enum RoomType, ej. "STANDARD").
     * @param observations      Observaciones iniciales (puede ser vacío).
     * @return Response con HospitalizationDTO actualizado.
     */
    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId, long doctorId,
                                                               String roomType, String observations) {
        // 1. Validar ID de hospitalización
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalización inválido.");
        }

        // 2. Validar ID del doctor
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        // 3. Validar tipo de habitación
        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitación no es válido. Opciones: STANDARD, ICU, NICU, IMC, ISOLATION.");
        }

        // 4. Buscar la hospitalización
        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.HOSPITALIZATION_NOT_FOUND, "No se encontró la hospitalización con ese ID.");
        }

        Hospitalization hospitalization = found.get();

        // 5. Verificar que esté en estado REQUESTED
        if (hospitalization.getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede aprobar una hospitalización en estado REQUESTED. Estado actual: " + hospitalization.getStatus());
        }

        // 6. Buscar el doctor
        Optional<User> doctorFound = userRepository.findById(doctorId);
        if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un doctor con ese ID.");
        }

        // 7. Aplicar cambios: asignar roomType, observaciones y cambiar estado a ONGOING
        hospitalization.setRoomType(room);
        hospitalization.setObservations(observations);
        hospitalization.setStatus(HospitalizationStatus.ONGOING);

        // 8. Guardar y retornar DTO
        hospitalizationRepository.save(hospitalization);
        HospitalizationDTO dto = ModelMapper.toHospitalizationDTO(hospitalization);
        return Response.success("Hospitalización aprobada. Estado actualizado a ONGOING.", dto);
    }

    /**
     * Rechaza o cancela una hospitalización. Cambia estado a CANCELED.
     * No se puede cancelar una hospitalización ya en CANCELED.
     *
     * @param hospitalizationId ID de la hospitalización.
     * @return Response con HospitalizationDTO actualizado.
     */
    public Response<HospitalizationDTO> denyOrCancelHospitalization(String hospitalizationId) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalización inválido.");
        }

        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.HOSPITALIZATION_NOT_FOUND, "No se encontró la hospitalización con ese ID.");
        }

        Hospitalization hospitalization = found.get();

        if (hospitalization.getStatus() == HospitalizationStatus.CANCELED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION, "La hospitalización ya está en estado CANCELED.");
        }

        hospitalization.setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.save(hospitalization);
        HospitalizationDTO dto = ModelMapper.toHospitalizationDTO(hospitalization);
        return Response.success("Hospitalización cancelada/denegada exitosamente.", dto);
    }

    /**
     * Crea una hospitalización directa y atómica desde una cita médica en estado PENDING.
     * La cita pasa a COMPLETED y la hospitalización se crea directamente en ONGOING.
     *
     * @param appointmentId ID de la cita médica de origen.
     * @param estimateDate  Fecha estimada de admisión (AAAA-MM-DD).
     * @param reason        Motivo de la hospitalización.
     * @param roomType      Tipo de habitación (valor del enum RoomType).
     * @param observations  Observaciones iniciales.
     * @return Response con HospitalizationDTO en caso de éxito.
     */
    public Response<HospitalizationDTO> createDirectHospitalization(String appointmentId, String estimateDate,
                                                                     String reason, String roomType,
                                                                     String observations) {
        // 1. Validaciones de entrada
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita médica inválido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada no es válida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalización no puede estar vacío.");
        }

        // 2. Validar tipo de habitación
        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitación no es válido. Opciones: STANDARD, ICU, NICU, IMC, ISOLATION.");
        }

        // 3. Buscar la cita médica
        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontró la cita médica con ese ID.");
        }

        Appointment appointment = found.get();

        // 4. Verificar que la cita esté en estado PENDING
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede crear una internación directa desde una cita en estado PENDING. Estado actual: " + appointment.getStatus());
        }

        // 5. Operación atómica: marcar cita como COMPLETED
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        // 6. Generar ID de hospitalización y crearla directamente en ONGOING
        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        String hospitalizationId = idGenerator.nextHospitalizationId(patient.getId());
        LocalDate date = LocalDate.parse(estimateDate);

        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId, patient, doctor, date, reason, room, observations, HospitalizationStatus.ONGOING);

        // 7. Guardar hospitalización y retornar DTO
        hospitalizationRepository.save(hospitalization);
        HospitalizationDTO dto = ModelMapper.toHospitalizationDTO(hospitalization);
        return Response.success("Internación directa creada exitosamente. Cita completada. ID hospitalización: " + hospitalizationId, dto);
    }
}
