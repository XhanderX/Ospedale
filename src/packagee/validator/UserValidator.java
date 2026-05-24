package packagee.validator;

/**
 * Validador base para las propiedades comunes de un usuario.
 * 
 * @author Issa
 */
public class UserValidator {

    /**
     * Valida que un ID numérico sea mayor que 0 y tenga exactamente 12 dígitos.
     */
    public static boolean validateId(long id) {
        if (id <= 0) {
            return false;
        }
        String idStr = String.valueOf(id);
        return idStr.length() == 12;
    }

    /**
     * Valida que un ID recibido como String tenga exactamente 12 dígitos numéricos.
     */
    public static boolean validateIdStr(String idStr) {
        if (idStr == null) {
            return false;
        }
        return idStr.matches("^\\d{12}$");
    }

    /**
     * Valida que el nombre de usuario no esté vacío ni contenga solo espacios en blanco.
     */
    public static boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    /**
     * Valida que la contraseña y su confirmación coincidan exactamente.
     */
    public static boolean validatePasswords(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

}
