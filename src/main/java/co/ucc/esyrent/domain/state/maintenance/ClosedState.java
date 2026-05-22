package co.ucc.esyrent.domain.state.maintenance;

import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;

public class ClosedState implements MaintenanceState {

    @Override
    public void advance(MaintenanceRequest request) {
        throw new IllegalStateException("Closed maintenance requests cannot advance");
    }

    @Override
    public boolean canAdvance() {
        return false;
    }

    @Override
    public MaintenanceStatus getStatus() {
        return MaintenanceStatus.CLOSED;
    }
}
