/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.dto;

import java.time.LocalDate;
import packagee.model.HospitalizationStatus;
import packagee.model.RoomType;

/**
 *
 * @author USER
 */
public class HospitalizationDTO {

    private final String id;
    private final long patientId;
    private final String patientName;
    private final long doctorId;
    private final String doctorName;
    private final LocalDate date;
    private final String reason;
    private final RoomType roomType;
    private final String observations;
    private final HospitalizationStatus status;

    public HospitalizationDTO(String id, long patientId, String patientName, long doctorId, String doctorName, LocalDate date, String reason, RoomType roomType, String observations, HospitalizationStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.date = date;
        this.reason = reason;
        this.roomType = roomType;
        this.observations = observations;
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

    public LocalDate getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public String getObservations() {
        return observations;
    }

    public HospitalizationStatus getStatus() {
        return status;
    }

}
