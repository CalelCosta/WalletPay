package recargapay.wallet.domain.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public enum ExceptionEnum {
    ERROR_PERSISTENCE("Error on create new User and Wallet", Response.Status.BAD_REQUEST.getStatusCode()),
    ALREADY_EXIST("There is already a Wallet for the CPF informed", Response.Status.BAD_REQUEST.getStatusCode()),
    AMOUNT_NOT_VALID("Deposit amount must be positive", Response.Status.BAD_REQUEST.getStatusCode()),
    NOT_FOUND("No one balance found with informed date", Response.Status.NOT_FOUND.getStatusCode());

    final String message;
    final Integer statusCode;

    ExceptionEnum(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
