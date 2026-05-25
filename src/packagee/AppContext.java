package packagee;

import java.io.IOException;
import java.nio.file.Path;
import packagee.controller.AppointmentController;
import packagee.controller.AuthController;
import packagee.controller.DoctorController;
import packagee.controller.HospitalizationController;
import packagee.controller.PatientController;
import packagee.service.AppointmentAvailabilityService;
import packagee.service.IAppointmentAvailabilityService;
import packagee.storage.HospitalStorage;
import packagee.validator.AppointmentValidator;
import packagee.validator.DateValidator;
import packagee.validator.DoctorValidator;
import packagee.validator.HospitalizationValidator;
import packagee.validator.PatientValidator;
import packagee.validator.UserValidator;

public final class AppContext {

    private static AppContext instance;

    private final HospitalStorage storage;
    private final AuthController authController;
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;

    private AppContext() {
        try {
            this.storage = new HospitalStorage();
            this.storage.loadUsersFromJson(Path.of("json", "users.json").toString());
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load initial users.", ex);
        }

        DateValidator dateValidator = new DateValidator();
        UserValidator userValidator = new UserValidator();
        PatientValidator patientValidator = new PatientValidator(dateValidator);
        DoctorValidator doctorValidator = new DoctorValidator();
        AppointmentValidator appointmentValidator = new AppointmentValidator(dateValidator);
        HospitalizationValidator hospitalizationValidator = new HospitalizationValidator(dateValidator);
        IAppointmentAvailabilityService availabilityService = new AppointmentAvailabilityService(
                storage.getAppointmentRepository(),
                storage.getUserRepository()
        );

        this.authController = new AuthController(userValidator, storage.getUserRepository());
        this.patientController = new PatientController(
                patientValidator,
                userValidator,
                storage.getUserRepository(),
                storage.getAppointmentRepository()
        );
        this.doctorController = new DoctorController(
                doctorValidator,
                userValidator,
                storage.getUserRepository(),
                storage.getAppointmentRepository()
        );
        this.appointmentController = new AppointmentController(
                appointmentValidator,
                userValidator,
                availabilityService,
                storage.getAppointmentRepository(),
                storage.getUserRepository(),
                storage.getIdGenerator()
        );
        this.hospitalizationController = new HospitalizationController(
                hospitalizationValidator,
                userValidator,
                storage.getHospitalizationRepository(),
                storage.getAppointmentRepository(),
                storage.getUserRepository(),
                storage.getIdGenerator()
        );
    }

    public static synchronized AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public HospitalStorage getStorage() {
        return storage;
    }

    public AuthController getAuthController() {
        return authController;
    }

    public PatientController getPatientController() {
        return patientController;
    }

    public DoctorController getDoctorController() {
        return doctorController;
    }

    public AppointmentController getAppointmentController() {
        return appointmentController;
    }

    public HospitalizationController getHospitalizationController() {
        return hospitalizationController;
    }
}
