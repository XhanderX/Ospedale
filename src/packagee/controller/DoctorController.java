package packagee.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import packagee.dto.AppointmentDTO;
import packagee.dto.DoctorDTO;
import packagee.mapper.ModelMapper;
import packagee.model.Appointment;
import packagee.model.AppointmentStatus;
import packagee.model.Doctor;
import packagee.model.Specialty;
import packagee.model.User;
import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.storage.AppointmentRepository;
import packagee.storage.UserRepository;
import packagee.validator.IDoctorValidator;
import packagee.validator.IUserValidator;

public class DoctorController {

    private final IDoctorValidator doctorValidator;
    private final IUserValidator userValidator;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorController(IDoctorValidator doctorValidator, IUserValidator userValidator,
                            UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.doctorValidator = doctorValidator;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Response<DoctorDTO> registerDoctor(long id, String username, String firstname,
                                              String lastname, String password,
                                              String confirmPassword, String licenceNumber,
                                              String assignedOffice, String specialty) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "El ID debe tener exactamente 12 dígitos y ser mayor que 0.");
        }
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }
        if (!userValidator.validatePersonName(firstname)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre solo puede contener letras y espacios.");
        }
        if (!userValidator.validatePersonName(lastname)) {
            return Response.error(StatusCode.INVALID_DATA, "El apellido solo puede contener letras y espacios.");
        }
        if (!userValidator.validatePasswords(password, confirmPassword)) {
            return Response.error(StatusCode.INVALID_DATA, "Las contraseñas no coinciden.");
        }
        if (!doctorValidator.validateLicenceNumber(licenceNumber)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la licencia médica debe ser L-XXXXXXXXXX MTL.");
        }
        if (!doctorValidator.validateAssignedOffice(assignedOffice)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la oficina asignada debe ser O-XXX.");
        }

        Specialty doctorSpecialty;
        try {
            doctorSpecialty = Specialty.valueOf(specialty.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad médica no es válida.");
        }

        if (userRepository.existsById(id)) {
            return Response.error(StatusCode.DUPLICATE_USER, "Ya existe un usuario registrado con ese ID.");
        }
        if (userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso.");
        }

        Doctor doctor = new Doctor(id, username, firstname, lastname, password,
                doctorSpecialty, licenceNumber, assignedOffice);
        userRepository.save(doctor);
        return Response.success("Doctor registrado exitosamente.", ModelMapper.toDoctorDTO(doctor));
    }

    public Response<DoctorDTO> registerDoctor(String id, String username, String firstname,
                                              String lastname, String password,
                                              String confirmPassword, String licenceNumber,
                                              String assignedOffice, String specialty) {
        if (!userValidator.validateIdStr(id)) {
            return Response.error(StatusCode.INVALID_DATA, "El ID debe tener exactamente 12 digitos y ser mayor que 0.");
        }
        return registerDoctor(Long.parseLong(id), username, firstname, lastname, password,
                confirmPassword, licenceNumber, assignedOffice, specialty);
    }

    public Response<DoctorDTO> updateDoctor(long id, String username, String firstname,
                                            String lastname, String licenceNumber,
                                            String assignedOffice, String specialty,
                                            String password, String confirmPassword) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }
        if (!userValidator.validatePersonName(firstname)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre solo puede contener letras y espacios.");
        }
        if (!userValidator.validatePersonName(lastname)) {
            return Response.error(StatusCode.INVALID_DATA, "El apellido solo puede contener letras y espacios.");
        }
        if (!doctorValidator.validateLicenceNumber(licenceNumber)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la licencia médica debe ser L-XXXXXXXXXX MTL.");
        }
        if (!doctorValidator.validateAssignedOffice(assignedOffice)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la oficina asignada debe ser O-XXX.");
        }

        boolean shouldUpdatePassword = (password != null && !password.trim().isEmpty())
                || (confirmPassword != null && !confirmPassword.trim().isEmpty());
        if (shouldUpdatePassword && !userValidator.validatePasswords(password, confirmPassword)) {
            return Response.error(StatusCode.INVALID_DATA, "Las contraseÃ±as no coinciden.");
        }

        Specialty doctorSpecialty;
        try {
            doctorSpecialty = Specialty.valueOf(specialty.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad médica no es válida.");
        }

        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un doctor con ese ID.");
        }
        Doctor doctor = (Doctor) found.get();

        if (!doctor.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso por otro usuario.");
        }

        doctor.setUsername(username);
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);
        doctor.setSpecialty(doctorSpecialty);
        if (shouldUpdatePassword) {
            doctor.setPassword(password);
        }

        userRepository.save(doctor);
        return Response.success("Información del doctor actualizada exitosamente.", ModelMapper.toDoctorDTO(doctor));
    }

    public Response<DoctorDTO> getDoctorInfo(long id) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un doctor con ese ID.");
        }

        return Response.success("Información del doctor obtenida exitosamente.", ModelMapper.toDoctorDTO((Doctor) found.get()));
    }

    public Response<List<AppointmentDTO>> getAppointments(long doctorId, boolean onlyPending) {
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        List<Appointment> appointments = onlyPending
                ? appointmentRepository.findByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING)
                : appointmentRepository.findByDoctorId(doctorId);

        List<Appointment> sorted = new ArrayList<>(appointments);
        Collections.sort(sorted, (a, b) -> b.getDatetime().compareTo(a.getDatetime()));

        List<AppointmentDTO> dtos = new ArrayList<>();
        for (Appointment appointment : sorted) {
            dtos.add(ModelMapper.toAppointmentDTO(appointment));
        }

        return Response.success("Citas del doctor obtenidas exitosamente.", dtos);
    }

    public Response<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> dtos = new ArrayList<>();
        for (Doctor doctor : userRepository.findAllDoctors()) {
            dtos.add(ModelMapper.toDoctorDTO(doctor));
        }
        return Response.success("Doctores obtenidos exitosamente.", dtos);
    }
}
