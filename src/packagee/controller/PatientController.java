package packagee.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import packagee.dto.AppointmentDTO;
import packagee.dto.PatientDTO;
import packagee.mapper.ModelMapper;
import packagee.model.Appointment;
import packagee.model.Gender;
import packagee.model.Patient;
import packagee.model.User;
import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.storage.AppointmentRepository;
import packagee.storage.UserRepository;
import packagee.validator.IPatientValidator;
import packagee.validator.IUserValidator;

public class PatientController {

    private final IPatientValidator patientValidator;
    private final IUserValidator userValidator;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public PatientController(IPatientValidator patientValidator, IUserValidator userValidator,
                             UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.patientValidator = patientValidator;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Response<PatientDTO> registerPatient(long id, String username, String firstname,
                                                String lastname, String password,
                                                String confirmPassword, String phone,
                                                String email, String birthdate,
                                                String gender, String address) {
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
        if (!patientValidator.validatePhone(phone)) {
            return Response.error(StatusCode.INVALID_DATA, "El teléfono debe tener exactamente 10 dígitos.");
        }
        if (!patientValidator.validateEmail(email)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de correo electrónico no es válido.");
        }
        if (!patientValidator.validateBirthdate(birthdate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de nacimiento no es válida o no tiene el formato AAAA-MM-DD.");
        }
        if (userRepository.existsById(id)) {
            return Response.error(StatusCode.DUPLICATE_USER, "Ya existe un usuario registrado con ese ID.");
        }
        if (userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso.");
        }

        Gender patientGender;
        try {
            patientGender = Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "El género debe ser MALE o FEMALE.");
        }

        Patient patient = new Patient(
                id,
                username,
                firstname,
                lastname,
                password,
                email,
                LocalDate.parse(birthdate),
                patientGender,
                Long.parseLong(phone),
                address
        );

        userRepository.save(patient);
        return Response.success("Paciente registrado exitosamente.", ModelMapper.toPatientDTO(patient));
    }

    public Response<PatientDTO> registerPatient(String id, String username, String firstname,
                                                String lastname, String password,
                                                String confirmPassword, String phone,
                                                String email, String birthdate,
                                                String gender, String address) {
        if (!userValidator.validateIdStr(id)) {
            return Response.error(StatusCode.INVALID_DATA, "El ID debe tener exactamente 12 digitos y ser mayor que 0.");
        }
        return registerPatient(Long.parseLong(id), username, firstname, lastname, password,
                confirmPassword, phone, email, birthdate, gender, address);
    }

    public Response<PatientDTO> updatePatient(long id, String username, String firstname,
                                              String lastname, String phone, String email,
                                              String birthdate, String gender, String address,
                                              String password, String confirmPassword) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de usuario inválido.");
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
        if (!patientValidator.validatePhone(phone)) {
            return Response.error(StatusCode.INVALID_DATA, "El teléfono debe tener exactamente 10 dígitos.");
        }
        if (!patientValidator.validateEmail(email)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de correo electrónico no es válido.");
        }
        if (!patientValidator.validateBirthdate(birthdate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de nacimiento no es válida o no tiene el formato AAAA-MM-DD.");
        }

        boolean shouldUpdatePassword = (password != null && !password.trim().isEmpty())
                || (confirmPassword != null && !confirmPassword.trim().isEmpty());
        if (shouldUpdatePassword && !userValidator.validatePasswords(password, confirmPassword)) {
            return Response.error(StatusCode.INVALID_DATA, "Las contraseñas no coinciden.");
        }

        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un paciente con ese ID.");
        }
        Patient patient = (Patient) found.get();

        if (!patient.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso por otro usuario.");
        }

        Gender patientGender;
        try {
            patientGender = Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "El género debe ser MALE o FEMALE.");
        }

        patient.setUsername(username);
        patient.setFirstname(firstname);
        patient.setLastname(lastname);
        patient.setPhone(Long.parseLong(phone));
        patient.setEmail(email);
        patient.setBirthdate(LocalDate.parse(birthdate));
        patient.setGender(patientGender);
        patient.setAddress(address);
        if (shouldUpdatePassword) {
            patient.setPassword(password);
        }

        userRepository.save(patient);
        return Response.success("Información del paciente actualizada exitosamente.", ModelMapper.toPatientDTO(patient));
    }

    public Response<PatientDTO> getPatientInfo(long id) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un paciente con ese ID.");
        }

        return Response.success("Información del paciente obtenida exitosamente.", ModelMapper.toPatientDTO((Patient) found.get()));
    }

    public Response<List<AppointmentDTO>> getAppointments(long patientId) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<Appointment> sorted = new ArrayList<>(appointments);
        Collections.sort(sorted, (a, b) -> b.getDatetime().compareTo(a.getDatetime()));

        List<AppointmentDTO> dtos = new ArrayList<>();
        for (Appointment appointment : sorted) {
            dtos.add(ModelMapper.toAppointmentDTO(appointment));
        }

        return Response.success("Citas del paciente obtenidas exitosamente.", dtos);
    }

    public Response<List<PatientDTO>> getAllPatients() {
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient patient : userRepository.findAllPatients()) {
            dtos.add(ModelMapper.toPatientDTO(patient));
        }
        return Response.success("Pacientes obtenidos exitosamente.", dtos);
    }
}
