package recargapay.wallet.domain.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    final String message;
    final Integer code;

    public BusinessException(String message, Integer statusCode) {
        this.message = message;
        this.code = statusCode;
    }
}
