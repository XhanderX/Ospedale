package packagee.validator;

/**
 * Interfaz para la validación de propiedades comunes de usuarios.
 * Abstracción que permite cumplir con DIP.
 * 
 * @author Issa
 */
public interface IUserValidator {
    /**
     * Valida que un ID numérico sea mayor que 0 y tenga exactamente 12 dígitos.
     */
    boolean validateId(long id);

    /**
     * Valida que un ID recibido como String tenga exactamente 12 dígitos numéricos.
     */
    boolean validateIdStr(String idStr);

    /**
     * Valida que el nombre de usuario no esté vacío.
     */
    boolean validateUsername(String username);

    /**
     * Valida que la contraseña y su confirmación coincidan exactamente.
     */
    boolean validatePasswords(String password, String confirmPassword);
}
