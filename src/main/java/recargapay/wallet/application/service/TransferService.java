package recargapay.wallet.application.service;

import recargapay.wallet.application.dto.request.TransferRequestDTO;

public interface TransferService {

    void handleTransfer(TransferRequestDTO transferRequestDTO);
}
