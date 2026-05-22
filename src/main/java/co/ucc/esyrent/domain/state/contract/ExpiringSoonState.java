package co.ucc.esyrent.domain.state.contract;

import co.ucc.esyrent.domain.entity.Contract;
import co.ucc.esyrent.domain.enums.ContractStatus;
import co.ucc.esyrent.domain.valueobject.CancellationDetails;
import java.time.LocalDate;

public class ExpiringSoonState implements ContractState {

    @Override
    public void onSchedulerTick(Contract contract, LocalDate today) {
        if (!today.isBefore(contract.getEndDate())) {
            contract.setState(new ExpiredState());
        }
    }

    @Override
    public void cancel(Contract contract, CancellationDetails details) {
        contract.applyCancellation(details);
        contract.setState(new CancelledState());
    }

    @Override
    public boolean canRegisterPayment() {
        return true;
    }

    @Override
    public ContractStatus getStatus() {
        return ContractStatus.EXPIRING_SOON;
    }
}
