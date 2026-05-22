package co.ucc.esyrent.domain.state.contract;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.valueobject.CancellationDetails;
import java.time.LocalDate;

public interface ContractState {

    void onSchedulerTick(Contract contract, LocalDate today);

    void cancel(Contract contract, CancellationDetails details);

    boolean canRegisterPayment();

    ContractStatus getStatus();
}
