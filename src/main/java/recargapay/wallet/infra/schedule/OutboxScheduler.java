package recargapay.wallet.infra.schedule;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import recargapay.wallet.infra.repository.OutboxRepository;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void processOutbox() {
        outboxRepository.findUnprocessedEvents().forEach(event -> {
            try {
                String topic = switch (event.getEventType()) {
                    case "DepositRequested" -> "deposit-events";
                    case "WithdrawRequested" -> "withdraw-events";
                    case "TransferRequested" -> "transfer-events";
                    default -> throw new IllegalStateException("Unmapped event: " + event.getEventType());
                };

                log.info("Send To topic {}: {}", topic, event.getPayload());
                kafkaTemplate.send(topic, event.getPayload());
                event.setProcessed(true);
                outboxRepository.saveAndFlush(event);
                log.info("Event processed: {}", event.getId());
            } catch (Exception e) {
                log.error("Failure to process event {}", event.getId(), e);
            }
        });
    }
}
