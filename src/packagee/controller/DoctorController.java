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

/**
 * Controlador de Doctores.
 * Gestiona el registro, actualización y consulta de doctores y sus agendas.
 * Cumple con SOLID (DIP al depender de interfaces en su constructor).
 *
 * @author Issa
 */
public class DoctorController {

    private final IDoctorValidator doctorValidator;
    private final IUserValidator userValidator;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     *
     * @param doctorValidator       Validador de datos específicos del doctor.
     * @param userValidator         Validador de datos comunes de usuario.
     * @param userRepository        Repositorio de usuarios.
     * @param appointmentRepository Repositorio de citas (para consultar agenda).
     */
    public DoctorController(IDoctorValidator doctorValidator, IUserValidator userValidator,
                            UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.doctorValidator = doctorValidator;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Registra un nuevo doctor en el sistema (por parte de un Administrador).
     *
     * @param id              Cédula/ID (12 dígitos, mayor que 0).
     * @param username        Nombre de usuario único.
     * @param firstname       Nombre del doctor.
     * @param lastname        Apellido del doctor.
     * @param password        Contraseña.
     * @param confirmPassword Confirmación de contraseña.
     * @param licenceNumber   Licencia médica (L-XXXXXXXXXX MTL).
     * @param assignedOffice  Oficina asignada (O-XXX).
     * @param specialty       Especialidad (valor del enum Specialty, ej. "CARDIOLOGY").
     * @return Response con DoctorDTO en caso de éxito.
     */
    public Response<DoctorDTO> registerDoctor(long id, String username, String firstname,
                                              String lastname, String password,
                                              String confirmPassword, String licenceNumber,
                                              String assignedOffice, String specialty) {
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

        // 5. Validar licencia médica (L-XXXXXXXXXX MTL)
        if (!doctorValidator.validateLicenceNumber(licenceNumber)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la licencia médica debe ser L-XXXXXXXXXX MTL.");
        }

        // 6. Validar oficina asignada (O-XXX)
        if (!doctorValidator.validateAssignedOffice(assignedOffice)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la oficina asignada debe ser O-XXX.");
        }

        // 7. Validar especialidad: debe ser un valor válido del enum Specialty
        Specialty doctorSpecialty;
        try {
            doctorSpecialty = Specialty.valueOf(specialty.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad médica no es válida.");
        }

        // 8. Verificar duplicado de ID en repositorio
        if (userRepository.existsById(id)) {
            return Response.error(StatusCode.DUPLICATE_USER, "Ya existe un usuario registrado con ese ID.");
        }

        // 9. Verificar duplicado de username en repositorio
        if (userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso.");
        }

        // 10. Construir el doctor y guardarlo
        Doctor doctor = new Doctor(id, username, firstname, lastname, password,
                doctorSpecialty, licenceNumber, assignedOffice);
        userRepository.save(doctor);

        DoctorDTO dto = ModelMapper.toDoctorDTO(doctor);
        return Response.success("Doctor registrado exitosamente.", dto);
    }

    /**
     * Actualiza la información de un doctor existente.
     * El ID es inmodificable y se usa como llave de búsqueda.
     *
     * @param id             ID del doctor (llave de búsqueda).
     * @param username       Nuevo nombre de usuario.
     * @param firstname      Nuevo nombre.
     * @param lastname       Nuevo apellido.
     * @param licenceNumber  Nueva licencia médica.
     * @param assignedOffice Nueva oficina.
     * @param specialty      Nueva especialidad.
     * @return Response con DoctorDTO actualizado.
     */
    public Response<DoctorDTO> updateDoctor(long id, String username, String firstname,
                                            String lastname, String licenceNumber,
                                            String assignedOffice, String specialty) {
        // 1. Validar ID
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
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

        // 4. Validar licencia médica
        if (!doctorValidator.validateLicenceNumber(licenceNumber)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la licencia médica debe ser L-XXXXXXXXXX MTL.");
        }

        // 5. Validar oficina asignada
        if (!doctorValidator.validateAssignedOffice(assignedOffice)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la oficina asignada debe ser O-XXX.");
        }

        // 6. Validar especialidad
        Specialty doctorSpecialty;
        try {
            doctorSpecialty = Specialty.valueOf(specialty.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad médica no es válida.");
        }

        // 7. Buscar el doctor en el repositorio
        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un doctor con ese ID.");
        }
        Doctor doctor = (Doctor) found.get();

        // 8. Verificar que el nuevo username no esté tomado por otro usuario
        if (!doctor.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            return Response.error(StatusCode.DUPLICATE_USER, "El nombre de usuario ya está en uso por otro usuario.");
        }

        // 9. Aplicar cambios con setters
        doctor.setUsername(username);
        doctor.setFirstname(firstname);
        doctor.setLastname(lastname);
        doctor.setLicenceNumber(licenceNumber);
        doctor.setAssignedOffice(assignedOffice);
        doctor.setSpecialty(doctorSpecialty);

        // 10. Guardar y retornar DTO
        userRepository.save(doctor);
        DoctorDTO dto = ModelMapper.toDoctorDTO(doctor);
        return Response.success("Información del doctor actualizada exitosamente.", dto);
    }

    /**
     * Obtiene la información de un doctor por su ID.
     *
     * @param id ID del doctor.
     * @return Response con DoctorDTO.
     */
    public Response<DoctorDTO> getDoctorInfo(long id) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        Optional<User> found = userRepository.findById(id);
        if (!found.isPresent() || !(found.get() instanceof Doctor)) {
            return Response.error(StatusCode.INVALID_DATA, "No se encontró un doctor con ese ID.");
        }

        DoctorDTO dto = ModelMapper.toDoctorDTO((Doctor) found.get());
        return Response.success("Información del doctor obtenida exitosamente.", dto);
    }

    /**
     * Obtiene la lista de citas de un doctor ordenadas de manera DESCENDENTE por fecha y hora.
     * Se puede filtrar para ver únicamente las citas en estado PENDING.
     *
     * @param doctorId    ID del doctor.
     * @param onlyPending true para ver solo citas PENDING; false para ver todas.
     * @return Response con lista de AppointmentDTO ordenada descendentemente.
     */
    public Response<List<AppointmentDTO>> getAppointments(long doctorId, boolean onlyPending) {
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        List<Appointment> appointments;
        if (onlyPending) {
            appointments = appointmentRepository.findByDoctorIdAndStatus(doctorId, AppointmentStatus.PENDING);
        } else {
            appointments = appointmentRepository.findByDoctorId(doctorId);
        }

        // Ordenar descendentemente por fecha y hora
        List<Appointment> sorted = new ArrayList<>(appointments);
        Collections.sort(sorted, (a, b) -> b.getDatetime().compareTo(a.getDatetime()));

        // Convertir a DTOs (nunca exponer modelos de dominio a la vista)
        List<AppointmentDTO> dtos = new ArrayList<>();
        for (Appointment appointment : sorted) {
            dtos.add(ModelMapper.toAppointmentDTO(appointment));
        }

        return Response.success("Citas del doctor obtenidas exitosamente.", dtos);
    }
}
