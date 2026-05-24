package packagee.controller;

import java.util.Optional;
import packagee.dto.UserDTO;
import packagee.mapper.ModelMapper;
import packagee.model.User;
import packagee.response.Response;
import packagee.response.StatusCode;
import packagee.storage.UserRepository;
import packagee.validator.IUserValidator;

/**
 * Controlador de Autenticación.
 * Gestiona el inicio de sesión del sistema Ospedale.
 * Cumple con SOLID (DIP al depender de interfaces en su constructor).
 *
 * @author Issa
 */
public class AuthController {

    private final IUserValidator userValidator;
    private final UserRepository userRepository;

    /**
     * Constructor con Inyección de Dependencias (DIP).
     *
     * @param userValidator  Validador de formato de datos de usuario.
     * @param userRepository Repositorio de usuarios para búsqueda en persistencia.
     */
    public AuthController(IUserValidator userValidator, UserRepository userRepository) {
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    /**
     * Realiza el login a la plataforma.
     * Busca al usuario por username, verifica la contraseña y retorna un DTO de sesión.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return Response con UserDTO en caso de éxito, o código de error correspondiente.
     */
    public Response<UserDTO> login(String username, String password) {
        // 1. Validar que los datos de entrada no estén vacíos
        if (!userValidator.validateUsername(username) || password == null || password.trim().isEmpty()) {
            return Response.error(StatusCode.INVALID_DATA, "El usuario o la contraseña no pueden estar vacíos.");
        }

        // 2. Buscar el usuario en el repositorio por username
        Optional<User> found = userRepository.findByUsername(username);

        // 3. Verificar que el usuario exista
        if (!found.isPresent()) {
            return Response.error(StatusCode.INVALID_CREDENTIALS, "Usuario o contraseña incorrectos.");
        }

        User user = found.get();

        // 4. Verificar que la contraseña coincida
        if (!user.getPassword().equals(password)) {
            return Response.error(StatusCode.INVALID_CREDENTIALS, "Usuario o contraseña incorrectos.");
        }

        // 5. Login exitoso: retornar UserDTO (nunca el modelo de dominio directo)
        UserDTO userDTO = ModelMapper.toUserDTO(user);
        return Response.success("Login exitoso. Bienvenido, " + user.getFirstname() + ".", userDTO);
    }
}
