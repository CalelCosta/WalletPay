package recargapay.wallet.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "wallet.balance", target = "balance")
    @Mapping(source = "wallet.currency", target = "currency")
    @Mapping(target = "message", expression = "java(\"User and Wallet created successfully\")")
    WalletResponseDTO toDto(User user, Wallet wallet);
}
