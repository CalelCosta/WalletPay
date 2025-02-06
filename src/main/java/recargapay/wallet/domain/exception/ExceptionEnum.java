package recargapay.wallet.domain.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public enum ExceptionEnum {
    AUTHOR_PARAMETER_NULL("xxxxxx is mandatory", Response.Status.BAD_REQUEST.getStatusCode()),
    GENRE_PARAMETER_NULL("yyyyyy is mandatory", Response.Status.BAD_REQUEST.getStatusCode()),
    ID_NOT_FOUND("No zzzzzzz found with the given id", Response.Status.NOT_FOUND.getStatusCode()),
    NOT_FOUND("No one vvvvvvv found", Response.Status.NOT_FOUND.getStatusCode());

    final String message;
    final Integer statusCode;

    ExceptionEnum(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
