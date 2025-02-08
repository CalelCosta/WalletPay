package recargapay.wallet.domain.usecase;

import recargapay.wallet.application.dto.request.TransferRequestDTO;

public interface TransferUseCase {

    void execute(TransferRequestDTO transferRequestDTO);
}
