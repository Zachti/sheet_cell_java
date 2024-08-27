package users;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

public class RoleRequest {
    private final UUID sheetId;
    private final Role role;
    private RequestStatus status = RequestStatus.PENDING;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public RoleRequest(User user, Role role, UUID sheetId) {
        this.role = role;
        this.sheetId = sheetId;
        addPropertyChangeListener(user);
    }

    public RequestStatus getStatus() { return status; }

    public UUID getSheetId() {
        return sheetId;
    }

    public void setStatus(RequestStatus newStatus) {
        this.status = newStatus;
        support.firePropertyChange("status", RequestStatus.PENDING, newStatus);
    }

    private void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}

