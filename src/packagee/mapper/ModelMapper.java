/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.mapper;

import packagee.dto.AppointmentDTO;
import packagee.dto.DoctorDTO;
import packagee.dto.HospitalizationDTO;
import packagee.dto.PatientDTO;
import packagee.dto.PrescriptionDTO;
import packagee.dto.UserDTO;
import packagee.model.Administrator;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Hospitalization;
import packagee.model.Patient;
import packagee.model.Prescription;
import packagee.model.User;

/**
 *
 * @author USER
 */
public class ModelMapper {

    private ModelMapper() {
    }

    public static UserDTO toUserDTO(User user) {
        String type;
        if (user instanceof Administrator) {
            type = "admin";
        } else if (user instanceof Doctor) {
            type = "doctor";
        } else if (user instanceof Patient) {
            type = "patient";
        } else {
            type = "unknown";
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                type
        );
    }

    public static PatientDTO toPatientDTO(Patient patient) {
        return new PatientDTO(
                patient.getId(),
                patient.getUsername(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getEmail(),
                patient.getBirthdate(),
                patient.getGender(),
                patient.getPhone(),
                patient.getAddress()
        );
    }

    public static DoctorDTO toDoctorDTO(Doctor doctor) {
        return new DoctorDTO(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getFirstname(),
                doctor.getLastname(),
                doctor.getSpecialty(),
                doctor.getLicenceNumber(),
                doctor.getAssignedOffice()
        );
    }

    public static AppointmentDTO toAppointmentDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getFirstname() + " " + appointment.getPatient().getLastname(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getFirstname() + " " + appointment.getDoctor().getLastname(),
                appointment.getSpecialty(),
                appointment.getDatetime(),
                appointment.getReason(),
                appointment.getType(),
                appointment.getStatus()
        );
    }

    public static HospitalizationDTO toHospitalizationDTO(Hospitalization hospitalization) {
        return new HospitalizationDTO(
                hospitalization.getId(),
                hospitalization.getPatient().getId(),
                hospitalization.getPatient().getFirstname() + " " + hospitalization.getPatient().getLastname(),
                hospitalization.getDoctor().getId(),
                hospitalization.getDoctor().getFirstname() + " " + hospitalization.getDoctor().getLastname(),
                hospitalization.getDate(),
                hospitalization.getReason(),
                hospitalization.getRoomType(),
                hospitalization.getObservations(),
                hospitalization.getStatus()
        );
    }

    public static PrescriptionDTO toPrescriptionDTO(Prescription prescription) {
        return new PrescriptionDTO(
                prescription.getMedicationName(),
                prescription.getDose(),
                prescription.getAdministrationRoute(),
                prescription.getTreatmentDuration(),
                prescription.getAdditionalInstructions(),
                prescription.getFrequency()
        );
    }
}
