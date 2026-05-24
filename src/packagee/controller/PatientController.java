package packagee.controller;

import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.validator.IPatientValidator;
import packagee.validator.IUserValidator;

/**
 * Controlador de Pacientes.
 * Gestiona el registro, actualización y consulta de pacientes e historial de citas.

 *
 * @author Issa
 */
public class PatientController {

    private final IPatientValidator patientValidator;
    private final IUserValidator userValidator;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     */
    public PatientController(IPatientValidator patientValidator, IUserValidator userValidator) {
        this.patientValidator = patientValidator;
        this.userValidator = userValidator;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @param id Cédula/ID (12 dígitos).
     * @param username Nombre de usuario único.
     * @param password Contraseña.
     * @param confirmPassword Confirmación de contraseña.
     * @param phone Teléfono (10 dígitos).
     * @param email Correo electrónico.
     * @param birthdate Fecha de nacimiento (AAAA-MM-DD).
     * @return Response con código de estado correspondiente.
     */
    public Response<Object> registerPatient(long id, String username, String password, 
                                            String confirmPassword, String phone, 
                                            String email, String birthdate) {
        // 1. Validar ID de usuario (12 dígitos, > 0)
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "El ID de usuario debe tener exactamente 12 dígitos y ser mayor que 0.");
        }

        // 2. Validar Username (no vacío)
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }

        // 3. Validar Contraseñas coinciden
        if (!userValidator.validatePasswords(password, confirmPassword)) {
            return Response.error(StatusCode.INVALID_DATA, "Las contraseñas no coinciden.");
        }

        // 4. Validar Teléfono (10 dígitos)
        if (!patientValidator.validatePhone(phone)) {
            return Response.error(StatusCode.INVALID_DATA, "El teléfono debe tener exactamente 10 dígitos.");
        }

        // 5. Validar Email
        if (!patientValidator.validateEmail(email)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de correo electrónico no es válido.");
        }

        // 6. Validar Fecha de nacimiento (AAAA-MM-DD)
        if (!patientValidator.validateBirthdate(birthdate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de nacimiento no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Validar duplicados de ID y Username en base de datos.
        // B. Guardar el nuevo Paciente en el archivo JSON.
        // Ejemplo:
        // if (storage.existsUserById(id)) return Response.error(StatusCode.DUPLICATE_USER, "El ID ya está registrado.");

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Registro de paciente pendiente de integración con persistencia (storage).");
    }

    /**
     * Actualiza la información de un paciente existente.
     * Los requerimientos son los mismos que para el registro (a excepción del password que no se actualiza aquí, 
     * y el ID que no puede ser modificado).
     *
     * @param id ID del paciente (llave de búsqueda, inmodificable).
     * @param username Nuevo nombre de usuario.
     * @param phone Nuevo teléfono.
     * @param email Nuevo correo.
     * @param birthdate Nueva fecha de nacimiento.
     * @return Response correspondiente.
     */
    public Response<Object> updatePatient(long id, String username, String phone, 
                                         String email, String birthdate) {
        // 1. Validar ID
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de usuario inválido.");
        }

        // 2. Validar Username
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }

        // 3. Validar Teléfono
        if (!patientValidator.validatePhone(phone)) {
            return Response.error(StatusCode.INVALID_DATA, "El teléfono debe tener exactamente 10 dígitos.");
        }

        // 4. Validar Email
        if (!patientValidator.validateEmail(email)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de correo electrónico no es válido.");
        }

        // 5. Validar Fecha de nacimiento
        if (!patientValidator.validateBirthdate(birthdate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de nacimiento no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Verificar existencia del paciente por ID.
        // B. Verificar que el nuevo username no esté tomado por otro usuario diferente (duplicado).
        // C. Guardar cambios en JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Actualización de paciente pendiente de integración con persistencia (storage).");
    }

    /**
     * Obtiene la información de un paciente por su ID.
     */
    public Response<Object> getPatientInfo(long id) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        // TODO: Buscar paciente en almacenamiento y retornar su representación serializada (no el objeto de dominio directo)
        return Response.error(StatusCode.NOT_IMPLEMENTED, "Consulta de info de paciente pendiente de integración (storage).");
    }

    /**
     * Obtiene la lista de citas de un paciente ordenadas de manera descendente (fecha y hora).
     */
    public Response<Object> getAppointments(long patientId) {
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        // TODO: Consultar citas en almacenamiento, ordenar descendentemente por fecha y hora, y retornar datos serializados
        return Response.error(StatusCode.NOT_IMPLEMENTED, "Consulta de citas del paciente pendiente de integración (storage).");
    }
}
