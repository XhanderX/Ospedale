/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.util.List;
import java.util.Optional;
import packagee.model.Appointment;
import packagee.model.AppointmentStatus;

/**
 *
 * @author USER
 */
public interface AppointmentRepository {

    void save(Appointment appointment);

    List<Appointment> findAll();

    Optional<Appointment> findById(String id);

    List<Appointment> findByPatientId(long patientId);

    List<Appointment> findByDoctorId(long doctorId);

    List<Appointment> findByDoctorIdAndStatus(long doctorId, AppointmentStatus status);

    int countByPatientId(long patientId);
}
