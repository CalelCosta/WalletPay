package recargapay.wallet.infra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import recargapay.wallet.infra.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.cpf = :cpf")
    User findByCpf(@Param("cpf") String cpf);
}
