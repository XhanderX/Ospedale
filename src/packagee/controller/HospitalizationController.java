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

public class HospitalizationController {

    private final IHospitalizationValidator hospitalizationValidator;
    private final IUserValidator userValidator;
    private final HospitalizationRepository hospitalizationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

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

    public Response<HospitalizationDTO> requestHospitalization(long patientId, String estimateDate, String reason) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente invÃ¡lido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada de admisiÃ³n no es vÃ¡lida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalizaciÃ³n no puede estar vacÃ­o.");
        }

        Optional<User> found = userRepository.findById(patientId);
        if (!found.isPresent() || !(found.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontrÃ³ un paciente con ese ID.");
        }

        Patient patient = (Patient) found.get();
        String hospitalizationId = idGenerator.nextHospitalizationId(patientId);
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                patient,
                null,
                LocalDate.parse(estimateDate),
                reason,
                null,
                null,
                HospitalizationStatus.REQUESTED
        );

        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "HospitalizaciÃ³n solicitada exitosamente con ID: " + hospitalizationId,
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId, long doctorId,
                                                               String roomType, String observations) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalizaciÃ³n invÃ¡lido.");
        }
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor invÃ¡lido.");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitaciÃ³n no es vÃ¡lido. Opciones: STANDARD, ICU, NICU, IMC, ISOLATION.");
        }

        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.HOSPITALIZATION_NOT_FOUND, "No se encontrÃ³ la hospitalizaciÃ³n con ese ID.");
        }

        Hospitalization hospitalization = found.get();
        if (hospitalization.getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede aprobar una hospitalizaciÃ³n en estado REQUESTED. Estado actual: " + hospitalization.getStatus());
        }

        Optional<User> doctorFound = userRepository.findById(doctorId);
        if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontrÃ³ un doctor con ese ID.");
        }

        hospitalization.setDoctor((Doctor) doctorFound.get());
        hospitalization.setRoomType(room);
        hospitalization.setObservations(observations);
        hospitalization.setStatus(HospitalizationStatus.ONGOING);

        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "HospitalizaciÃ³n aprobada. Estado actualizado a ONGOING.",
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> denyOrCancelHospitalization(String hospitalizationId) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalizaciÃ³n invÃ¡lido.");
        }

        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.HOSPITALIZATION_NOT_FOUND, "No se encontrÃ³ la hospitalizaciÃ³n con ese ID.");
        }

        Hospitalization hospitalization = found.get();
        if (hospitalization.getStatus() == HospitalizationStatus.CANCELED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION, "La hospitalizaciÃ³n ya estÃ¡ en estado CANCELED.");
        }

        hospitalization.setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "HospitalizaciÃ³n cancelada/denegada exitosamente.",
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> createDirectHospitalization(String appointmentId, String estimateDate,
                                                                    String reason, String roomType,
                                                                    String observations) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita mÃ©dica invÃ¡lido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada no es vÃ¡lida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalizaciÃ³n no puede estar vacÃ­o.");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitaciÃ³n no es vÃ¡lido. Opciones: STANDARD, ICU, NICU, IMC, ISOLATION.");
        }

        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontrÃ³ la cita mÃ©dica con ese ID.");
        }

        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede crear una internaciÃ³n directa desde una cita en estado PENDING. Estado actual: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        String hospitalizationId = idGenerator.nextHospitalizationId(patient.getId());
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                patient,
                doctor,
                LocalDate.parse(estimateDate),
                reason,
                room,
                observations,
                HospitalizationStatus.ONGOING
        );

        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "InternaciÃ³n directa creada exitosamente. Cita completada. ID hospitalizaciÃ³n: " + hospitalizationId,
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> createHospitalizationByPatient(long patientId, long doctorId,
                                                                       String estimateDate, String reason,
                                                                       String observations) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente invÃ¡lido.");
        }
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor invÃ¡lido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada no es vÃ¡lida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalizaciÃ³n no puede estar vacÃ­o.");
        }

        Optional<User> patientFound = userRepository.findById(patientId);
        if (!patientFound.isPresent() || !(patientFound.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontrÃ³ un paciente con ese ID.");
        }
        Optional<User> doctorFound = userRepository.findById(doctorId);
        if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontrÃ³ un doctor con ese ID.");
        }

        String hospitalizationId = idGenerator.nextHospitalizationId(patientId);
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                (Patient) patientFound.get(),
                (Doctor) doctorFound.get(),
                LocalDate.parse(estimateDate),
                reason,
                RoomType.IMC,
                observations,
                HospitalizationStatus.ONGOING
        );

        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "HospitalizaciÃ³n creada exitosamente con ID: " + hospitalizationId,
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }
}
