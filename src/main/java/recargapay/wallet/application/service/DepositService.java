package recargapay.wallet.application.service;

import recargapay.wallet.application.dto.request.DepositRequestDTO;

public interface DepositService {

    void handleDeposit(DepositRequestDTO eventDepositDTO);
}
