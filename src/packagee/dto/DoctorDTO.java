/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.dto;

import packagee.model.Specialty;

/**
 *
 * @author USER
 */
public class DoctorDTO {

    private final long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final Specialty specialty;
    private final String licenceNumber;
    private final String assignedOffice;

    public DoctorDTO(long id, String username, String firstname, String lastname, Specialty specialty, String licenceNumber, String assignedOffice) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.specialty = specialty;
        this.licenceNumber = licenceNumber;
        this.assignedOffice = assignedOffice;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

}
