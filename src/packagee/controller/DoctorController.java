package packagee.controller;

import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.validator.IDoctorValidator;
import packagee.validator.IUserValidator;

/**
 * Controlador de Doctores.
 * Gestiona el registro, actualización y consulta de doctores y sus agendas.

 *
 * @author Issa
 */
public class DoctorController {

    private final IDoctorValidator doctorValidator;
    private final IUserValidator userValidator;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     */
    public DoctorController(IDoctorValidator doctorValidator, IUserValidator userValidator) {
        this.doctorValidator = doctorValidator;
        this.userValidator = userValidator;
    }

    /**
     * Registra un nuevo doctor en el sistema (por parte de un Administrador).
     *
     * @param id Cédula/ID (12 dígitos).
     * @param username Nombre de usuario único.
     * @param password Contraseña.
     * @param confirmPassword Confirmación de contraseña.
     * @param licenceNumber Licencia médica (L-XXXXXXXXXX MTL).
     * @param assignedOffice Oficina (O-XXX).
     * @param specialty Especialidad médica.
     * @return Response correspondiente.
     */
    public Response<Object> registerDoctor(long id, String username, String password, 
                                           String confirmPassword, String licenceNumber, 
                                           String assignedOffice, String specialty) {
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

        // 4. Validar Licencia Médica (L-XXXXXXXXXX MTL)
        if (!doctorValidator.validateLicenceNumber(licenceNumber)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la licencia médica debe ser L-XXXXXXXXXX MTL.");
        }

        // 5. Validar Oficina Asignada (O-XXX)
        if (!doctorValidator.validateAssignedOffice(assignedOffice)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la oficina asignada debe ser O-XXX.");
        }

        // 6. Validar Especialidad (no vacía)
        if (specialty == null || specialty.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad médica no puede estar vacía.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Validar duplicados de ID, Username y Licencia Médica en almacenamiento.
        // B. Guardar el nuevo Doctor en el archivo JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Registro de doctor pendiente de integración con persistencia (storage).");
    }

    /**
     * Actualiza la información de un doctor existente.
     * Mismos requerimientos de validación (el ID es inmodificable).
     *
     * @param id ID del doctor (llave de búsqueda, inmodificable).
     * @param username Nuevo nombre de usuario.
     * @param licenceNumber Nueva licencia médica.
     * @param assignedOffice Nueva oficina.
     * @param specialty Nueva especialidad.
     * @return Response correspondiente.
     */
    public Response<Object> updateDoctor(long id, String username, String licenceNumber, 
                                        String assignedOffice, String specialty) {
        // 1. Validar ID
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        // 2. Validar Username
        if (!userValidator.validateUsername(username)) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre de usuario no puede estar vacío.");
        }

        // 3. Validar Licencia Médica
        if (!doctorValidator.validateLicenceNumber(licenceNumber)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la licencia médica debe ser L-XXXXXXXXXX MTL.");
        }

        // 4. Validar Oficina Asignada
        if (!doctorValidator.validateAssignedOffice(assignedOffice)) {
            return Response.error(StatusCode.INVALID_DATA, "El formato de la oficina asignada debe ser O-XXX.");
        }

        // 5. Validar Especialidad
        if (specialty == null || specialty.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad médica no puede estar vacía.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Verificar existencia del doctor por ID.
        // B. Validar duplicidad de username o licencia.
        // C. Guardar cambios en JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Actualización de doctor pendiente de integración con persistencia (storage).");
    }

    /**
     * Obtiene la información de un doctor por su ID.
     */
    public Response<Object> getDoctorInfo(long id) {
        if (!userValidator.validateId(id)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        // TODO: Buscar doctor en almacenamiento y retornar su representación DTO/JSON serializada
        return Response.error(StatusCode.NOT_IMPLEMENTED, "Consulta de info de doctor pendiente de integración (storage).");
    }

    /**
     * Obtiene la lista de citas de un doctor ordenadas de manera descendente (fecha y hora).
     * Puede filtrarse por citas pendientes únicamente o citas totales.
     *
     * @param doctorId ID del doctor.
     * @param onlyPending True si se desea filtrar para ver únicamente las citas pendientes (PENDING).
     * @return Response correspondiente.
     */
    public Response<Object> getAppointments(long doctorId, boolean onlyPending) {
        if (!userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        // TODO: Consultar citas del doctor en almacenamiento, aplicar filtro onlyPending, ordenar descendentemente y retornar datos serializados
        return Response.error(StatusCode.NOT_IMPLEMENTED, "Consulta de citas del doctor pendiente de integración (storage).");
    }
}
