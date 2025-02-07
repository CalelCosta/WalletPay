package recargapay.wallet.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import recargapay.wallet.infra.model.Wallet;

import java.math.BigDecimal;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query(value = "select w from Wallet w where w.id = :id")
    Wallet findWalletById(@Param("id") Long id);
}
