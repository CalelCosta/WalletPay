package recargapay.wallet.application.service;

import recargapay.wallet.application.dto.request.WithdrawRequestDTO;

public interface WithdrawService {

    void handleWithdraw(WithdrawRequestDTO withdrawRequestDTO);
}
