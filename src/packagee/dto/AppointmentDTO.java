/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.dto;

import java.time.LocalDateTime;
import packagee.model.AppointmentStatus;
import packagee.model.AppointmentType;
import packagee.model.Specialty;

/**
 *
 * @author USER
 */
public class AppointmentDTO {

    private final String id;
    private final long patientId;
    private final String patientName;
    private final long doctorId;
    private final String doctorName;
    private final Specialty specialty;
    private final LocalDateTime datetime;
    private final String reason;
    private final AppointmentType type;
    private final AppointmentStatus status;

    public AppointmentDTO(String id, long patientId, String patientName, long doctorId, String doctorName, Specialty specialty, LocalDateTime datetime, String reason, AppointmentType type, AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.datetime = datetime;
        this.reason = reason;
        this.type = type;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public String getReason() {
        return reason;
    }

    public AppointmentType getType() {
        return type;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

}
