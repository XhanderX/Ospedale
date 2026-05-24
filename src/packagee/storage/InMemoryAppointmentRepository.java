/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import packagee.model.Appointment;
import packagee.model.AppointmentStatus;

/**
 *
 * @author USER
 */
public class InMemoryAppointmentRepository implements AppointmentRepository {

    private final List<Appointment> appointments;

    public InMemoryAppointmentRepository() {
        this.appointments = new ArrayList<>();
    }

    @Override
    public void save(Appointment appointment) {
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(appointment.getId())) {
                appointments.set(i, appointment);
                return;
            }
        }
        appointments.add(appointment);

        if (!containsAppointmentWithId(appointment.getPatient().getAppointments(), appointment.getId())) {
            appointment.getPatient().addAppointment(appointment);
        }

        if (!containsAppointmentWithId(appointment.getDoctor().getAppointments(), appointment.getId())) {
            appointment.getDoctor().addAppointment(appointment);
        }
    }

    @Override
    public List<Appointment> findAll() {
        return new ArrayList<>(appointments);
    }

    @Override
    public Optional<Appointment> findById(String id) {
        for (Appointment appointment : appointments) {
            if (appointment.getId().equals(id)) {
                return Optional.of(appointment);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Appointment> findByPatientId(long patientId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getId() == patientId) {
                result.add(appointment);
            }
        }
        return result;
    }

    @Override
    public List<Appointment> findByDoctorId(long doctorId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().getId() == doctorId) {
                result.add(appointment);
            }
        }
        return result;
    }

    @Override
    public List<Appointment> findByDoctorIdAndStatus(long doctorId, AppointmentStatus status) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().getId() == doctorId && appointment.getStatus() == status) {
                result.add(appointment);
            }
        }
        return result;
    }

    @Override
    public int countByPatientId(long patientId) {
        int count = 0;
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getId() == patientId) {
                count++;
            }
        }
        return count;
    }

    private boolean containsAppointmentWithId(List<Appointment> appointments, String id) {
        for (Appointment appointment : appointments) {
            if (appointment.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

}
