/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

/**
 *
 * @author USER
 */
public class IdGenerator {

    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;

    public IdGenerator(AppointmentRepository appointmentRepository, HospitalizationRepository hospitalizationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    public String nextAppointmentId(long patientId) {
        int next = appointmentRepository.countByPatientId(patientId);
        return String.format("A-%012d-%04d", patientId, next);
    }

    public String nexthospitalizationId(long patientId) {
        int next = hospitalizationRepository.countByPatientId(patientId);
        return String.format("H-%012d-%04d", patientId, next);
    }

}
