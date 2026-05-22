package co.ucc.esyrent.domain.state.contract;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.valueobject.CancellationDetails;
import java.time.LocalDate;

public class ExpiredState implements ContractState {

    @Override
    public void onSchedulerTick(Contract contract, LocalDate today) {
    }

    @Override
    public void cancel(Contract contract, CancellationDetails details) {
        throw new IllegalStateException("Expired contracts cannot be cancelled");
    }

    @Override
    public boolean canRegisterPayment() {
        return false;
    }

    @Override
    public ContractStatus getStatus() {
        return ContractStatus.EXPIRED;
    }
}
