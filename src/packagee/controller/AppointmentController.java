package packagee.controller;

import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.service.IAppointmentAvailabilityService;
import packagee.validator.IAppointmentValidator;
import packagee.validator.IUserValidator;

/**
 * Controlador de Citas Médicas.
 * Gestiona todo el ciclo de vida de una cita y las prescripciones asociadas.
 * Cumple con SOLID (DIP al depender de interfaces en su constructor).
 *
 * @author Issa
 */
public class AppointmentController {

    private final IAppointmentValidator appointmentValidator;
    private final IUserValidator userValidator;
    private final IAppointmentAvailabilityService availabilityService;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     */
    public AppointmentController(IAppointmentValidator appointmentValidator, 
                                 IUserValidator userValidator, 
                                 IAppointmentAvailabilityService availabilityService) {
        this.appointmentValidator = appointmentValidator;
        this.userValidator = userValidator;
        this.availabilityService = availabilityService;
    }

    /**
     * Solicita una nueva cita médica.
     * La cita se creará con el formato de ID "A-{id_paciente}-NNNN" e iniciará en estado REQUESTED.
     *
     * @param patientId ID del paciente (12 dígitos).
     * @param doctorId ID del doctor opcional (puede ser null si se solicita por especialidad).
     * @param specialty Especialidad solicitada.
     * @param date Fecha de la cita (AAAA-MM-DD).
     * @param time Hora de la cita (hh:mm en formato 24h, minutos en cuartos).
     * @param reason Motivo de la consulta.
     * @return Response correspondiente.
     */
    public Response<Object> requestAppointment(long patientId, Long doctorId, String specialty, 
                                              String date, String time, String reason) {
        // 1. Validar ID del paciente
        if (!userValidator.validateId(patientId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de paciente inválido.");
        }

        // 2. Validar ID de doctor si se proporcionó
        if (doctorId != null && !userValidator.validateId(doctorId)) {
            return Response.error(StatusCode.INVALID_DATA, "ID de doctor inválido.");
        }

        // 3. Validar Especialidad (no vacía)
        if (specialty == null || specialty.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La especialidad no puede estar vacía.");
        }

        // 4. Validar Fecha (AAAA-MM-DD)
        if (!appointmentValidator.validateDate(date)) {
            return Response.error(StatusCode.INVALID_DATA, "La fecha de la cita no es válida o no tiene el formato AAAA-MM-DD.");
        }

        // 5. Validar Hora (hh:mm 24h, minutos en cuartos)
        if (!appointmentValidator.validateTime(time)) {
            return Response.error(StatusCode.INVALID_DATA, "La hora de la cita debe estar en formato de 24 horas hh:mm y en cuartos de hora (00, 15, 30, 45).");
        }

        // 6. Validar Motivo (no vacío)
        if (reason == null || reason.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El motivo de la cita no puede estar vacío.");
        }

        // TODO: Conectar con:
        // A. packagee.service.AppointmentAvailabilityService para verificar la disponibilidad horaria del doctor 
        //    o realizar la asignación automática por especialidad si no se escoge doctor.
        // B. packagee.storage para obtener el consecutivo NNNN del paciente, generar el ID de cita A-{id_paciente}-NNNN
        //    y guardar la cita en estado REQUESTED.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Solicitud de cita pendiente de integración con persistencia (storage) y servicio de disponibilidad.");
    }

    /**
     * Acepta una cita solicitada. Pasa su estado a PENDING.
     *
     * @param appointmentId ID de la cita a aceptar.
     * @return Response correspondiente.
     */
    public Response<Object> acceptAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar la cita.
        // B. Garantizar que la cita exista y esté en estado REQUESTED.
        // C. Cambiar estado a PENDING y guardar.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Aceptación de cita pendiente de integración con persistencia (storage).");
    }

    /**
     * Completa una cita médica. Pasa su estado a COMPLETED.
     *
     * @param appointmentId ID de la cita.
     * @return Response correspondiente.
     */
    public Response<Object> completeAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar la cita.
        // B. Verificar que esté en estado PENDING.
        // C. Cambiar estado a COMPLETED y guardar.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Completado de cita pendiente de integración con persistencia (storage).");
    }

    /**
     * Cancela una cita médica. Pasa su estado a CANCELED.
     * Un paciente o doctor puede cancelarla siempre y cuando NO esté en estado COMPLETED.
     *
     * @param appointmentId ID de la cita.
     * @return Response correspondiente.
     */
    public Response<Object> cancelAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar la cita.
        // B. Validar que su estado actual no sea COMPLETED ni CANCELED.
        // C. Cambiar estado a CANCELED y guardar.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Cancelación de cita pendiente de integración con persistencia (storage).");
    }

    /**
     * Reagenda una cita a una nueva hora.
     * Reglas: Debe seguir el mismo formato de 24h y cuartos de hora, NO se puede cambiar el día de la cita,
     * y la razón de reprogramación debe ser concatenada a la razón original.
     *
     * @param appointmentId ID de la cita.
     * @param newTime Nueva hora (hh:mm en formato 24h, minutos en cuartos).
     * @param reason Razón de la reprogramación.
     * @return Response correspondiente.
     */
    public Response<Object> rescheduleAppointment(String appointmentId, String newTime, String reason) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // 1. Validar formato de la nueva hora
        if (!appointmentValidator.validateTime(newTime)) {
            return Response.error(StatusCode.INVALID_DATA, "La nueva hora de la cita debe estar en formato de 24 horas hh:mm y en cuartos de hora (00, 15, 30, 45).");
        }

        // 2. Validar razón
        if (reason == null || reason.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La razón de la reprogramación no puede estar vacía.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar la cita.
        // B. Validar que la cita exista y esté en un estado reprogramable (REQUESTED o PENDING).
        // C. Validar la disponibilidad del doctor en la nueva hora (mismo día).
        // D. Concatenar la nueva razón a la razón original de la cita.
        // E. Actualizar la hora, la razón y guardar cambios en JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Reprogramación de cita pendiente de integración con persistencia (storage).");
    }

    /**
     * Prescribe medicamentos a una cita.
     * Solo se pueden prescribir si la cita está en estado PENDING (aceptada pero no completada ni cancelada).
     *
     * @param appointmentId ID de la cita.
     * @param medicationName Nombre del medicamento.
     * @param dose Dosis del medicamento.
     * @param adminRoute Vía de administración.
     * @param duration Duración del tratamiento (días).
     * @param instructions Instrucciones adicionales.
     * @param frequency Frecuencia de toma (horas).
     * @return Response correspondiente.
     */
    public Response<Object> prescribeMedications(String appointmentId, String medicationName, 
                                                 double dose, String adminRoute, int duration, 
                                                 String instructions, int frequency) {
        if (appointmentId == null || appointmentId.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "ID de cita no válido.");
        }

        // 1. Validaciones básicas del medicamento
        if (medicationName == null || medicationName.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El nombre del medicamento no puede estar vacío.");
        }
        if (dose <= 0) {
            return Response.error(StatusCode.INVALID_DATA, "La dosis debe ser mayor que 0.");
        }
        if (adminRoute == null || adminRoute.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "La vía de administración no puede estar vacía.");
        }
        if (duration <= 0) {
            return Response.error(StatusCode.INVALID_DATA, "La duración del tratamiento debe ser de al menos 1 día.");
        }
        if (frequency <= 0) {
            return Response.error(StatusCode.INVALID_DATA, "La frecuencia debe ser mayor que 0 horas.");
        }

        // TODO: Conectar con packagee.storage para:
        // A. Buscar la cita.
        // B. Verificar que su estado sea estrictamente PENDING.
        // C. Crear el objeto Prescription, relacionarlo con la cita y guardarlo en el archivo JSON.

        return Response.error(StatusCode.NOT_IMPLEMENTED, "Prescripción de medicamentos pendiente de integración con persistencia (storage).");
    }
}
