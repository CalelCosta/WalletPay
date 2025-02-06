package recargapay.wallet.application.dto.response;

public record AuthenticationResponseDTO(String token, String type, Integer expiresIn) {
}
