package recargapay.wallet.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import recargapay.wallet.application.dto.response.BalanceResponseDTO;
import recargapay.wallet.application.dto.response.WalletResponseDTO;
import recargapay.wallet.infra.model.User;
import recargapay.wallet.infra.model.Wallet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "wallet.balance", target = "balance")
    @Mapping(source = "wallet.currency", target = "currency")
    @Mapping(target = "message", expression = "java(\"User and Wallet created successfully\")")
    WalletResponseDTO toDto(User user, Wallet wallet);

    @Mapping(source = "createdAt", target = "date", qualifiedByName = "localDateToString")
    BalanceResponseDTO toDto(Wallet wallet);
    List<BalanceResponseDTO> toListDto(List<Wallet> wallet);

    @Named("localDateToString")
    static String localDateToString(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
