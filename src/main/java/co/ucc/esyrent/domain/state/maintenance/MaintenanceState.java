package co.ucc.esyrent.domain.state.maintenance;

import co.ucc.esyrent.domain.entity.MaintenanceRequest;
import co.ucc.esyrent.domain.enums.MaintenanceStatus;

public interface MaintenanceState {

    void advance(MaintenanceRequest request);

    boolean canAdvance();

    MaintenanceStatus getStatus();
}
