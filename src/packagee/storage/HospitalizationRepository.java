/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.util.List;
import java.util.Optional;
import packagee.model.Hospitalization;

/**
 *
 * @author USER
 */
public interface HospitalizationRepository {

    void save(Hospitalization hospitalization);

    List<Hospitalization> findAll();

    Optional<Hospitalization> findById(String id);

    List<Hospitalization> findByPatientId(long patientId);

    List<Hospitalization> findByDoctorId(long doctorId);

    int countByPatientId(long patientId);
}
