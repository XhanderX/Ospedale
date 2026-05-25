package packagee.validator;

public interface IUserValidator {

    boolean validateId(long id);

    boolean validateIdStr(String idStr);

    boolean validateUsername(String username);

    boolean validatePersonName(String value);

    boolean validatePasswords(String password, String confirmPassword);
}
