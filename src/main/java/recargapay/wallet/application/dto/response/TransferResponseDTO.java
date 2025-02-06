package recargapay.wallet.application.dto.response;

public record TransferResponseDTO(String message, String sender, String receiver, Double amount) {
}
