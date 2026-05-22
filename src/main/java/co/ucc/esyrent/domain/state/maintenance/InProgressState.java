package co.ucc.esyrent.domain.state.maintenance;

import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;

public class InProgressState implements MaintenanceState {

    @Override
    public void advance(MaintenanceRequest request) {
        request.setState(new ClosedState());
    }

    @Override
    public boolean canAdvance() {
        return true;
    }

    @Override
    public MaintenanceStatus getStatus() {
        return MaintenanceStatus.IN_PROGRESS;
    }
}
