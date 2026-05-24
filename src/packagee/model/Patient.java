/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author USER
 */
public class Patient extends User {

    private String email;
    private LocalDate birthdate;
    private Gender gender;
    private long phone;
    private String address;
    private final List<Appointment> appointments;
    private final List<Hospitalization> hospitalizations;

    public Patient(long id, String username, String firstname, String lastname, String password, String email, LocalDate birthdate, Gender gender, long phone, String address) {
        super(id, username, firstname, lastname, password);
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.appointments = new ArrayList<>();
        this.hospitalizations = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public Gender getGender() {
        return gender;
    }

    public long getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    public List<Hospitalization> getHospitalizations() {
        return Collections.unmodifiableList(hospitalizations);
    }
    
     public void addAppointment(Appointment appointment){
        appointments.add(appointment);
    }
    
    public void addHospitalization(Hospitalization hospitalization){
        hospitalizations.add(hospitalization);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
