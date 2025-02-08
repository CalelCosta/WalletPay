package recargapay.wallet.domain.usecase;

import recargapay.wallet.application.dto.request.WithdrawRequestDTO;

public interface WithdrawUseCase {

    void execute(WithdrawRequestDTO withdrawRequestDTO);
}
