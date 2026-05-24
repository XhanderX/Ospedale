package packagee.validator;

/**
 * Validador base para las propiedades comunes de un usuario.
 * Implementa IUserValidator para cumplir con DIP.
 * 
 * @author Issa
 */
public class UserValidator implements IUserValidator {

    /**
     * Valida que un ID numérico sea mayor que 0 y tenga exactamente 12 dígitos.
     */
    @Override
    public boolean validateId(long id) {
        if (id <= 0) {
            return false;
        }
        String idStr = String.valueOf(id);
        return idStr.length() == 12;
    }

    /**
     * Valida que un ID recibido como String tenga exactamente 12 dígitos numéricos.
     */
    @Override
    public boolean validateIdStr(String idStr) {
        if (idStr == null) {
            return false;
        }
        return idStr.matches("^\\d{12}$");
    }

    /**
     * Valida que el nombre de usuario no esté vacío ni contenga solo espacios en blanco.
     */
    @Override
    public boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    /**
     * Valida que la contraseña y su confirmación coincidan exactamente.
     */
    @Override
    public boolean validatePasswords(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
