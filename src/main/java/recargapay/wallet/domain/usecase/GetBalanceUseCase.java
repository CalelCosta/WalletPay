package recargapay.wallet.domain.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;

public interface GetBalanceUseCase {

    Page<BalanceResponseDTO> getBalance(Pageable pageable, Long userId, String date);
}
