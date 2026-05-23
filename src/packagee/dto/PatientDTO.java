/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.dto;

import java.time.LocalDate;
import packagee.model.Gender;

/**
 *
 * @author USER
 */
public class PatientDTO {

    private final long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final LocalDate birthdate;
    private final Gender gender;
    private final long phone;
    private final String address;

    public PatientDTO(long id, String username, String firstname, String lastname, String email, LocalDate birthdate, Gender gender, long phone, String address) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
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

}
