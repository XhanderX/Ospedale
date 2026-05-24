package packagee.response;

/**
 * Clase genérica para el transporte de respuestas desde los controladores
 * hacia las vistas Swing, cumpliendo con la regla de no exponer modelos de dominio.
 * 
 * @author Issa
 * @param <T> Tipo de datos serializados (normalmente JSONObject, JSONArray o DTOs)
 */
public class Response<T> {
    private final boolean success;
    private final StatusCode statusCode;
    private final String message;
    private final T data;

    public Response(boolean success, StatusCode statusCode, String message, T data) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    /**
     * Crea una respuesta de éxito con datos.
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(true, StatusCode.SUCCESS, message, data);
    }

    /**
     * Crea una respuesta de éxito sin datos (operaciones void).
     */
    public static <T> Response<T> success(String message) {
        return new Response<>(true, StatusCode.SUCCESS, message, null);
    }

    /**
     * Crea una respuesta de error con un código de estado específico y mensaje.
     */
    public static <T> Response<T> error(StatusCode statusCode, String message) {
        return new Response<>(false, statusCode, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
