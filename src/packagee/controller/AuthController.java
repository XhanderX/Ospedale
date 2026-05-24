package packagee.controller;

import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.validator.IUserValidator;

/**
 * Controlador de Autenticación.
 * Gestiona el inicio de sesión del sistema Ospedale.
 *
 * @author Issa
 */
public class AuthController {

    private final IUserValidator userValidator;

    /**
     * Constructor con Inyección de Dependencia de IUserValidator (DIP).
     */
    public AuthController(IUserValidator userValidator) {
        this.userValidator = userValidator;
    }

    /**
     * Realiza el login a la plataforma.
     * 
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return Response con DTO/JSON temporal de sesión o código de error correspondiente.
     */
    public Response<Object> login(String username, String password) {
        // 1. Validar que los datos de entrada sean correctos
        if (!userValidator.validateUsername(username) || password == null || password.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El usuario o la contraseña no pueden estar vacíos.");
        }

        // TODO: Conectar con packagee.storage para buscar el usuario en la base de datos (JSON)
        // Ejemplo de validación de almacenamiento (ficticia para el contrato temporal):
        // if (!storage.userExists(username)) { return Response.error(StatusCode.INVALID_CREDENTIALS, "Usuario o contraseña incorrectos."); }

        // Como contrato temporal, devolvemos NOT_IMPLEMENTED indicando que está listo a la espera del almacenamiento
        return Response.error(StatusCode.NOT_IMPLEMENTED, "Funcionalidad de login pendiente de integración de almacenamiento (storage).");
    }
}
