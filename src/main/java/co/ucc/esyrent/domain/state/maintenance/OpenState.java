package co.ucc.esyrent.domain.state.maintenance;

import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;

public class OpenState implements MaintenanceState {

    @Override
    public void advance(MaintenanceRequest request) {
        request.setState(new InProgressState());
    }

    @Override
    public boolean canAdvance() {
        return true;
    }

    @Override
    public MaintenanceStatus getStatus() {
        return MaintenanceStatus.OPEN;
    }
}
