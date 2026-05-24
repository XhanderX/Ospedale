/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.util.List;
import java.util.Optional;
import packagee.model.User;
import packagee.model.Doctor;
import packagee.model.Patient;

/**
 *
 * @author USER
 */
public interface UserRepository {

    void save(User user);

    List<User> findAll();

    List<Doctor> findAllDoctors();

    List<Patient> findAllPatients();

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    boolean existsById(long Id);

    boolean existsByUsername(String username);
}
