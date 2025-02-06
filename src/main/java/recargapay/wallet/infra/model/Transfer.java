package recargapay.wallet.infra.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "sender_transaction_id", nullable = false)
    private Transaction senderTransaction;

    @OneToOne
    @JoinColumn(name = "receiver_transaction_id", nullable = false)
    private Transaction receiverTransaction;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
