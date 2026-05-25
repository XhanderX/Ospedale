package packagee.validator;

public class UserValidator implements IUserValidator {

    @Override
    public boolean validateId(long id) {
        if (id <= 0) {
            return false;
        }
        String idStr = String.valueOf(id);
        return idStr.length() == 12;
    }

    @Override
    public boolean validateIdStr(String idStr) {
        if (idStr == null) {
            return false;
        }
        return idStr.matches("^\\d{12}$");
    }

    @Override
    public boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    @Override
    public boolean validatePersonName(String value) {
        if (value == null) {
            return false;
        }
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            return false;
        }
        return trimmedValue.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$");
    }

    @Override
    public boolean validatePasswords(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
