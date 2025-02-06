package recargapay.wallet.domain.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

    final String message;
    final Integer code;

    public NotFoundException(String message, Integer statusCode) {
        this.message = message;
        this.code = statusCode;
    }
}
