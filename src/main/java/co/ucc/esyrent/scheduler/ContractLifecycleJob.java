package co.ucc.esyrent.scheduler;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.repository.ContractRepository;
import co.ucc.esyrent.service.EmailNotificationService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ContractLifecycleJob {

    private final ContractRepository contractRepository;
    private final EmailNotificationService emailNotificationService;
    private final String scheduledTime;

    public ContractLifecycleJob(ContractRepository contractRepository,
                                EmailNotificationService emailNotificationService,
                                @Value("${app.scheduler.contract-lifecycle.cron}") String scheduledTime) {
        this.contractRepository = contractRepository;
        this.emailNotificationService = emailNotificationService;
        this.scheduledTime = scheduledTime;
    }

    @Scheduled(cron = "${app.scheduler.contract-lifecycle.cron}")
    @Transactional
    public void runDailyTransition() {
        transitionAll(LocalDate.now());
    }

    private void transitionAll(LocalDate today) {
        List<Contract> contracts = contractRepository.findByStatusIn(List.of(
                ContractStatus.ACTIVE,
                ContractStatus.EXPIRING_SOON
        ));

        for (Contract contract : contracts) {
            ContractStatus previousStatus = contract.getStatus();
            contract.transitionState(today);
            if (previousStatus != ContractStatus.EXPIRING_SOON
                    && contract.getStatus() == ContractStatus.EXPIRING_SOON) {
                emailNotificationService.sendContractExpiringNotification(contract.getId());
            }
        }
    }

    public String getScheduledTime() {
        return scheduledTime;
    }
}
