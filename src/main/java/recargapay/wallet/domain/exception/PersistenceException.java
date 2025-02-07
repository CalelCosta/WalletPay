package recargapay.wallet.domain.exception;

import lombok.Getter;

@Getter
public class PersistenceException extends RuntimeException {

    final String message;
    final Integer code;

    public PersistenceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
}
