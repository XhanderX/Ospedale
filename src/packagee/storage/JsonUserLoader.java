/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import org.json.JSONObject;
import org.json.JSONArray;
import packagee.model.*;

/**
 *
 * @author USER
 */
public class JsonUserLoader {

    private final UserRepository userRepository;

    public JsonUserLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void load(String jsonPath) throws IOException {
        String content = Files.readString(Path.of(jsonPath));
        JSONObject root = new JSONObject(content);
        JSONArray users = root.getJSONArray("users");

        for (int i = 0; i < users.length(); i++) {
            JSONObject item = users.getJSONObject(i);
            String type = item.getString("type").toLowerCase();

            if (type.equals("admin")) {
                userRepository.save(new Administrator(
                        item.getLong("id"),
                        item.getString("username"),
                        item.getString("firstname"),
                        item.getString("lastname"),
                        item.getString("password")
                ));
            } else if (type.equals("patient")) {
                userRepository.save(new Patient(
                        item.getLong("id"),
                        item.getString("username"),
                        item.getString("firstname"),
                        item.getString("lastname"),
                        item.getString("password"),
                        item.getString("email"),
                        LocalDate.parse(item.getString("birthdate")),
                        item.getBoolean("gender") ? Gender.MALE : Gender.FEMALE,
                        item.getLong("phone"),
                        item.getString("address")
                ));
            } else if (type.equals("doctor")) {
                userRepository.save(new Doctor(
                        item.getLong("id"),
                        item.getString("username"),
                        item.getString("firstname"),
                        item.getString("lastname"),
                        item.getString("password"),
                        parseSpecialty(item.getString("specialty")),
                        item.getString("licenceNumber"),
                        item.getString("assignedOffice")
                ));
            }
        }
    }

    private Specialty parseSpecialty(String rawSpecialty) {
        if ("ORTHOPEDICS".equals(rawSpecialty)) {
            return Specialty.TRAUMATOLOGY_ORTHOPEDICS;
        }
        if ("GYNECOLOGY".equals(rawSpecialty)) {
            return Specialty.GYNECOLOGY_OBSTETRICS;
        }
        return Specialty.valueOf(rawSpecialty);
    }

}
