package packagee.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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

/**
 * Controlador de Pacientes.
 * Gestiona el registro, actualización y consulta de pacientes e historial de citas.
 * Cumple con SOLID (DIP al depender de interfaces en su constructor).
 *
 * @author Issa
 */
public class PatientController {

    private final IPatientValidator patientValidator;
    private final IUserValidator userValidator;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     *
     * @param patientValidator      Validador de datos específicos del paciente.
     * @param userValidator         Validador de datos comunes de usuario.
     * @param userRepository        Repositorio de usuarios.
     * @param appointmentRepository Repositorio de citas (para consultar historial).
     */
    public PatientController(IPatientValidator patientValidator, IUserValidator userValidator,
                             UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.patientValidator = patientValidator;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     * Valida formato, verifica duplicados y persiste el nuevo paciente.
     *
     * @param id              Cédula/ID (12 dígitos, mayor que 0).
     * @param username        Nombre de usuario único.
     * @param firstname       Nombre del paciente.
     * @param lastname        Apellido del paciente.
     * @param password        Contraseña.
     * @param confirmPassword Confirmación de contraseña.
     * @param phone           Teléfono (String de exactamente 10 dígitos).
     * @param email           Correo electrónico.
     * @param birthdate       Fecha de nacimiento (AAAA-MM-DD).
     * @param gender          Género ("MALE" o "FEMALE").
     * @param address         Dirección del paciente.
     * @return Response con PatientDTO en caso de éxito.
     */
    public Response<PatientDTO> registerPatient(long id, String username, String firstname,
                                                String lastname, String password,
                                                String confirmPassword, String phone,
                                                String email, String birthdate,
                                                String gender, String address) {
        // 1. Validar ID (12 dígitos, > 0)
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "El ID debe tener exactamente 12 dígitos y ser mayor que 0.");
        }

        // 2. Validar username no vacío
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }

        // 3. Validar nombre y apellido no vacíos
        if (firstname == null || firstname.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre no puede estar vacío.");
        }
        if (lastname == null || lastname.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El apellido no puede estar vacío.");
        }

        // 4. Validar que las contraseñas coincidan
        if (!userValidator.validatePasswords(password, confirmPassword)) {
            return Response.error(StatusCode.INVALID_DATA, "Las contraseñas no coinciden.");
        }

        // 5. Validar teléfono (10 dígitos)
        if (!patientValidator.validatePhone(phone)) {
            return Response.error(StatusCode.INVALID_DATA, "El teléfono debe tener exactamente 10 dígitos.");
        }

        // 6. Validar email
        if (!patientValidator.validateEmail(email)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de correo electrónico no es válido.");
        }

        // 7. Validar fecha de nacimiento (AAAA-MM-DD)
        if (!patientValidator.validateBirthdate(birthdate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de nacimiento no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // 8. Verificar duplicado de ID en repositorio
        if (userRepository.existsById(id)) {
            return Response.error(StatusCode.DUPLICATE_USER, "Ya existe un usuario registrado con ese ID.");
        }

        // 9. Verificar duplicado de username en repositorio
        if (userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso.");
        }

        // 10. Parsear género
        Gender patientGender;
        try {
            patientGender = Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "El género debe ser MALE o FEMALE.");
        }

        // 11. Parsear fecha y construir paciente
        LocalDate birthdateParsed = LocalDate.parse(birthdate);
        long phoneNum = Long.parseLong(phone);
        Patient patient = new Patient(id, username, firstname, lastname, password,
                email, birthdateParsed, patientGender, phoneNum, address);

        // 12. Guardar en repositorio y retornar DTO
        userRepository.save(patient);
        PatientDTO dto = ModelMapper.toPatientDTO(patient);
        return Response.success("Paciente registrado exitosamente.", dto);
    }

    /**
     * Actualiza la información de un paciente existente.
     * El ID no es modificable y se usa como llave de búsqueda.
     *
     * @param id        ID del paciente (llave de búsqueda).
     * @param username  Nuevo nombre de usuario.
     * @param firstname Nuevo nombre.
     * @param lastname  Nuevo apellido.
     * @param phone     Nuevo teléfono (String de 10 dígitos).
     * @param email     Nuevo correo.
     * @param birthdate Nueva fecha de nacimiento (AAAA-MM-DD).
     * @param gender    Nuevo género ("MALE" o "FEMALE").
     * @param address   Nueva dirección.
     * @return Response con PatientDTO actualizado en caso de éxito.
     */
    public Response<PatientDTO> updatePatient(long id, String username, String firstname,
                                              String lastname, String phone, String email,
                                              String birthdate, String gender, String address) {
        // 1. Validar ID
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de usuario inválido.");
        }

        // 2. Validar username
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }

        // 3. Validar nombre y apellido
        if (firstname == null || firstname.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre no puede estar vacío.");
        }
        if (lastname == null || lastname.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El apellido no puede estar vacío.");
        }

        // 4. Validar teléfono
        if (!patientValidator.validatePhone(phone)) {
            return Response.error(StatusCode.INVALID_DATA, "El teléfono debe tener exactamente 10 dígitos.");
        }

        // 5. Validar email
        if (!patientValidator.validateEmail(email)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de correo electrónico no es válido.");
        }

        // 6. Validar fecha de nacimiento
        if (!patientValidator.validateBirthdate(birthdate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de nacimiento no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // 7. Buscar el paciente en el repositorio
        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un paciente con ese ID.");
        }
        Patient patient = (Patient) found.get();

        // 8. Verificar que el nuevo username no esté tomado por otro usuario
        if (!patient.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso por otro usuario.");
        }

        // 9. Parsear género
        Gender patientGender;
        try {
            patientGender = Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "El género debe ser MALE o FEMALE.");
        }

        // 10. Aplicar cambios con setters
        patient.setUsername(username);
        patient.setFirstname(firstname);
        patient.setLastname(lastname);
        patient.setPhone(Long.parseLong(phone));
        patient.setEmail(email);
        patient.setBirthdate(LocalDate.parse(birthdate));
        patient.setGender(patientGender);
        patient.setAddress(address);

        // 11. Guardar y retornar DTO
        userRepository.save(patient);
        PatientDTO dto = ModelMapper.toPatientDTO(patient);
        return Response.success("Información del paciente actualizada exitosamente.", dto);
    }

    /**
     * Obtiene la información de un paciente por su ID.
     *
     * @param id ID del paciente.
     * @return Response con PatientDTO.
     */
    public Response<PatientDTO> getPatientInfo(long id) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Patient)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un paciente con ese ID.");
        }

        PatientDTO dto = ModelMapper.toPatientDTO((Patient) found.get());
        return Response.success("Información del paciente obtenida exitosamente.", dto);
    }

    /**
     * Obtiene la lista de citas de un paciente ordenadas de manera DESCENDENTE por fecha y hora.
     *
     * @param patientId ID del paciente.
     * @return Response con lista de AppointmentDTO ordenada descendentemente.
     */
    public Response<List<AppointmentDTO>> getAppointments(long patientId) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

        // Ordenar descendentemente por fecha y hora usando comparador sin lambdas (compatibilidad Java)
        List<Appointment> sorted = new ArrayList<>(appointments);
        Collections.sort(sorted, (a, b) -> b.getDatetime().compareTo(a.getDatetime()));

        // Convertir cada cita a DTO (nunca exponer modelos de dominio a la vista)
        List<AppointmentDTO> dtos = new ArrayList<>();
        for (Appointment appointment : sorted) {
            dtos.add(ModelMapper.toAppointmentDTO(appointment));
        }

        return Response.success("Citas del paciente obtenidas exitosamente.", dtos);
    }
}
