package recargapay.wallet.domain.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import recargapay.wallet.application.dto.response.ErrorResponseDTO;
import recargapay.wallet.domain.exception.BusinessException;
import recargapay.wallet.domain.exception.NotFoundException;

@ControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(BusinessException businessException) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(businessException.getMessage(), businessException.getCode());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException notFoundException) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(notFoundException.getMessage(), notFoundException.getCode());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

}
