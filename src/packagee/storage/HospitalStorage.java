/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.io.IOException;

/**
 *
 * @author USER
 */
public class HospitalStorage {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;
    private final IdGenerator idGenerator;

    public HospitalStorage() {
        this.userRepository = new InMemoryUserRepository();
        this.appointmentRepository = new InMemoryAppointmentRepository();
        this.hospitalizationRepository = new InMemoryHospitalizationRepository();
        this.idGenerator = new IdGenerator(appointmentRepository, hospitalizationRepository);
    }

    public void loadUsersFromJson(String path) throws IOException {
        JsonUserLoader loader = new JsonUserLoader(userRepository);
        loader.load(path);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AppointmentRepository getAppointmentRepository() {
        return appointmentRepository;
    }

    public HospitalizationRepository getHospitalizationRepository() {
        return hospitalizationRepository;
    }

    public IdGenerator getIdGenerator() {
        return idGenerator;
    }

}
