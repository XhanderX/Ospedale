package packagee.controller;

import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.validator.IHospitalizationValidator;
import packagee.validator.IUserValidator;

/**
 * Controlador de Hospitalizaciones.
 * Gestiona el registro, aprobación, cancelación e internación directa de hospitalizaciones.

 *
 * @author Issa
 */
public class HospitalizationController {

    private final IHospitalizationValidator hospitalizationValidator;
    private final IUserValidator userValidator;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     */
    public HospitalizationController(IHospitalizationValidator hospitalizationValidator, IUserValidator userValidator) {
        this.hospitalizationValidator = hospitalizationValidator;
        this.userValidator = userValidator;
    }

    /**
     * Solicita una hospitalización (por parte de un paciente).
     * Iniciará en estado REQUESTED y su ID tendrá la estructura H-{id_paciente}-NNNN.
     *
     * @param patientId ID del paciente (12 dígitos).
     * @param estimateDate Fecha estimada de admisión (AAAA-MM-DD).
     * @param reason Motivo de la hospitalización.
     * @return Response correspondiente.
     */
    public Response<Object> requestHospitalization(long patientId, String estimateDate, String reason) {
        // 1. Validar ID del paciente
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        // 2. Validar Fecha Estimada de Admisión (AAAA-MM-DD)
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada de admisión no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // 3. Validar Motivo
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalización no puede estar vacío.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Validar existencia del paciente en el sistema.
        // B. Obtener el consecutivo NNNN del paciente, generar el ID de hospitalización H-{id_paciente}-NNNN
        //    y guardar el registro en estado REQUESTED en el archivo JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Solicitud de hospitalización pendiente de integración con persistencia (storage).");
    }

    /**
     * Aprueba una solicitud de hospitalización. Pasa su estado a ONGOING.
     *
     * @param hospitalizationId ID de la hospitalización.
     * @param assignedRoom Habitación asignada (ej. número o nombre).
     * @param roomType Tipo de habitación (ej. de RoomType enum/clase).
     * @return Response correspondiente.
     */
    public Response<Object> approveHospitalization(String hospitalizationId, String assignedRoom, String roomType) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalización inválido.");
        }
        if (assignedRoom == null || assignedRoom.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "Debe asignar una habitación válida.");
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "Debe seleccionar un tipo de habitación válido.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar el registro de hospitalización.
        // B. Verificar que esté en estado REQUESTED.
        // C. Asignar la habitación y cambiar estado a ONGOING.
        // D. Guardar los cambios en el JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Aprobación de hospitalización pendiente de integración con persistencia (storage).");
    }

    /**
     * Rechaza una solicitud de hospitalización o cancela una en curso. Pasa su estado a CANCELED.
     *
     * @param hospitalizationId ID de la hospitalización.
     * @return Response correspondiente.
     */
    public Response<Object> denyOrCancelHospitalization(String hospitalizationId) {
        if (hospitalizationId == null || hospitalizationId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de hospitalización inválido.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar el registro de hospitalización.
        // B. Validar que no esté cancelada previamente.
        // C. Cambiar estado a CANCELED y guardar en el JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Cancelación/Denegación de hospitalización pendiente de integración con persistencia (storage).");
    }

    /**
     * Crea una hospitalización directa y atómica desde una cita médica.
     * La cita médica original pasa automáticamente a COMPLETED y la hospitalización inicia directamente en estado ONGOING.
     *
     * @param appointmentId ID de la cita médica de origen.
     * @param estimateDate Fecha estimada de admisión (AAAA-MM-DD).
     * @param reason Motivo de la hospitalización.
     * @param assignedRoom Habitación asignada.
     * @param roomType Tipo de habitación.
     * @return Response correspondiente.
     */
    public Response<Object> createDirectHospitalization(String appointmentId, String estimateDate, 
                                                         String reason, String assignedRoom, 
                                                         String roomType) {
        // 1. Validaciones básicas de entrada
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita médica inválido.");
        }
        if (!hospitalizationValidator.validateEstimateDate(estimateDate)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha estimada de admisión no es válida o no tiene el formato AAAA-MM-DD.");
        }
        if (!hospitalizationValidator.validateReason(reason)) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la hospitalización no puede estar vacío.");
        }
        if (assignedRoom == null || assignedRoom.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "Debe asignar una habitación válida.");
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "Debe seleccionar un tipo de habitación válido.");
        }

        // TODO: Conectar con packagee.storage en una única transacción atómica para:
        // A. Buscar la cita médica y verificar que esté en un estado válido (PENDING).
        // B. Cambiar el estado de la cita médica a COMPLETED.
        // C. Obtener el ID del paciente desde la cita.
        // D. Generar el ID de hospitalización H-{id_paciente}-NNNN.
        // E. Crear y guardar la hospitalización directamente en estado ONGOING con la habitación asignada.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Internación directa desde cita médica pendiente de integración con persistencia (storage).");
    }
}
