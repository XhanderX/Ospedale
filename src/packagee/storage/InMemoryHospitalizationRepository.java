/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import packagee.model.Hospitalization;

/**
 *
 * @author USER
 */
public class InMemoryHospitalizationRepository implements HospitalizationRepository {

    private final List<Hospitalization> hospitalizations;

    public InMemoryHospitalizationRepository() {
        this.hospitalizations = new ArrayList<>();
    }

    @Override
    public void save(Hospitalization hospitalization) {
        boolean exists = false;
        for (int i = 0; i < hospitalizations.size(); i++) {
            if (hospitalizations.get(i).getId().equals(hospitalization.getId())) {
                hospitalizations.set(i, hospitalization);
                exists = true;
                break;
            }
        }
        if (!exists) {
            hospitalizations.add(hospitalization);
        }

        if (!containsHospitalizationWithId(hospitalization.getPatient().getHospitalizations(), hospitalization.getId())) {
            hospitalization.getPatient().addHospitalization(hospitalization);
        }

        if (hospitalization.getDoctor() != null
                && !containsHospitalizationWithId(hospitalization.getDoctor().getHospitalizations(), hospitalization.getId())) {
            hospitalization.getDoctor().addHospitalization(hospitalization);
        }
    }

    @Override
    public List<Hospitalization> findAll() {
        return new ArrayList<>(hospitalizations);
    }

    @Override
    public Optional<Hospitalization> findById(String id) {
        for (Hospitalization hospitalization : hospitalizations) {
            if (hospitalization.getId().equals(id)) {
                return Optional.of(hospitalization);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Hospitalization> findByPatientId(long patientId) {
        List<Hospitalization> result = new ArrayList<>();
        for (Hospitalization hospitalization : hospitalizations) {
            if (hospitalization.getPatient().getId() == patientId) {
                result.add(hospitalization);
            }
        }
        return result;
    }

    @Override
    public List<Hospitalization> findByDoctorId(long doctorId) {
        List<Hospitalization> result = new ArrayList<>();
        for (Hospitalization hospitalization : hospitalizations) {
            if (hospitalization.getDoctor() != null && hospitalization.getDoctor().getId() == doctorId) {
                result.add(hospitalization);
            }
        }
        return result;
    }

    @Override
    public int countByPatientId(long patientId) {
        int count = 0;
        for (Hospitalization hospitalization : hospitalizations) {
            if (hospitalization.getPatient().getId() == patientId) {
                count++;
            }
        }
        return count;
    }

    private boolean containsHospitalizationWithId(List<Hospitalization> hospitalizations, String id) {
        for (Hospitalization hospitalization : hospitalizations) {
            if (hospitalization.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

}
