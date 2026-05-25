package packagee.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    public Response<HospitalizationDTO> requestHospitalization(long patientId, long doctorId,
                                                               String estimateDate, String reason,
                                                               String roomType, String observations) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente invalido.");
        }
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor invalido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada de admision no es valida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalizacion no puede estar vacio.");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitacion no es valido.");
        }

        Optional<User> patientFound = userRepository.findById(patientId);
        if (!patientFound.isPresent() || !(patientFound.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontro un paciente con ese ID.");
        }

        Optional<User> doctorFound = userRepository.findById(doctorId);
        if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontro un doctor con ese ID.");
        }

        String hospitalizationId = idGenerator.nextHospitalizationId(patientId);
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                (Patient) patientFound.get(),
                (Doctor) doctorFound.get(),
                LocalDate.parse(estimateDate),
                reason,
                room,
                observations,
                HospitalizationStatus.REQUESTED
        );

        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "Hospitalizacion solicitada exitosamente con ID: " + hospitalizationId,
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId, long doctorId,
                                                               String roomType, String observations) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalizacion invalido.");
        }
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor invalido.");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitacion no es valido.");
        }

        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.HOSPITALIZATION_NOT_FOUND, "No se encontro la hospitalizacion con ese ID.");
        }

        Hospitalization hospitalization = found.get();
        if (hospitalization.getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede aprobar una hospitalizacion en estado REQUESTED. Estado actual: " + hospitalization.getStatus());
        }

        Optional<User> doctorFound = userRepository.findById(doctorId);
        if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontro un doctor con ese ID.");
        }

        hospitalization.setDoctor((Doctor) doctorFound.get());
        hospitalization.setRoomType(room);
        hospitalization.setObservations(observations);
        hospitalization.setStatus(HospitalizationStatus.ONGOING);

        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "Hospitalizacion aprobada. Estado actualizado a ONGOING.",
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> denyOrCancelHospitalization(String hospitalizationId) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalizacion invalido.");
        }

        Optional<Hospitalization> found = hospitalizationRepository.findById(hospitalizationId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.HOSPITALIZATION_NOT_FOUND, "No se encontro la hospitalizacion con ese ID.");
        }

        Hospitalization hospitalization = found.get();
        if (hospitalization.getStatus() == HospitalizationStatus.CANCELED) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION, "La hospitalizacion ya esta en estado CANCELED.");
        }

        hospitalization.setStatus(HospitalizationStatus.CANCELED);
        hospitalizationRepository.save(hospitalization);
        return Response.success(
                "Hospitalizacion cancelada/denegada exitosamente.",
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> createDirectHospitalization(String appointmentId, String estimateDate,
                                                                    String reason, String roomType,
                                                                    String observations) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita medica invalido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada no es valida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalizacion no puede estar vacio.");
        }

        RoomType room;
        try {
            room = RoomType.valueOf(roomType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Response.error(StatusCode.INVALID_DATA, "El tipo de habitacion no es valido.");
        }

        Optional<Appointment> found = appointmentRepository.findById(appointmentId);
        if (!found.isPresent()) {
            return Response.error(StatusCode.APPOINTMENT_NOT_FOUND, "No se encontro la cita medica con ese ID.");
        }

        Appointment appointment = found.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.error(StatusCode.INVALID_STATE_TRANSITION,
                    "Solo se puede crear una internacion directa desde una cita en estado PENDING. Estado actual: " + appointment.getStatus());
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
                "Internacion directa creada exitosamente. Cita completada. ID hospitalizacion: " + hospitalizationId,
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<HospitalizationDTO> createHospitalizationByPatient(long patientId, long doctorId,
                                                                       String estimateDate, String reason,
                                                                       String observations) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente invalido.");
        }
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor invalido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada no es valida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalizacion no puede estar vacio.");
        }

        Optional<User> patientFound = userRepository.findById(patientId);
        if (!patientFound.isPresent() || !(patientFound.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontro un paciente con ese ID.");
        }
        Optional<User> doctorFound = userRepository.findById(doctorId);
        if (!doctorFound.isPresent() || !(doctorFound.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontro un doctor con ese ID.");
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
                "Hospitalizacion creada exitosamente con ID: " + hospitalizationId,
                ModelMapper.toHospitalizationDTO(hospitalization)
        );
    }

    public Response<List<HospitalizationDTO>> getRequestedHospitalizations() {
        List<HospitalizationDTO> dtos = new ArrayList<>();
        for (Hospitalization hospitalization : hospitalizationRepository.findAll()) {
            if (hospitalization.getStatus() == HospitalizationStatus.REQUESTED) {
                dtos.add(ModelMapper.toHospitalizationDTO(hospitalization));
            }
        }
        return Response.success("Solicitudes de hospitalizacion obtenidas exitosamente.", dtos);
    }
}
