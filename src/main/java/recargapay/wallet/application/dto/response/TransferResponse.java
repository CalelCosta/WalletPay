package recargapay.wallet.application.dto.response;

public record TransferResponse(String message, String sender, String receiver, Double amount) {
}
