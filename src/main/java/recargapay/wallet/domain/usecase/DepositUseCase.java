package recargapay.wallet.domain.usecase;

import recargapay.wallet.application.dto.request.DepositRequestDTO;

public interface DepositUseCase {

    void execute(DepositRequestDTO depositRequestDTO);
}
