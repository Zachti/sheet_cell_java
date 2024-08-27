package users;

import sheet.interfaces.ISheet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import static sheet.SheetsManager.getSheetById;

public class User implements PropertyChangeListener {
    private final String username;
    private final Map<UUID, Role> sheet2Roles = new HashMap<>();
    private final Map<UUID, ISheet> ownedSheets = new HashMap<>();
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setRole(UUID sheetId, Role role) {
        support.firePropertyChange("role", sheetId, role);
        sheet2Roles.put(sheetId, role);
        getSheetById(sheetId).addUser(this);
    }

    public Map<UUID, Role> getSheet2Roles() {
        return sheet2Roles;
    }

    public Role getRoleBySheetId(UUID sheetId) {
        return sheet2Roles.get(sheetId);
    }

    public void addOwnedSheet(UUID sheetId, ISheet sheet) {
        ownedSheets.put(sheetId, sheet);
    }

    public boolean ownsSheet(UUID sheetId) {
        return ownedSheets.containsKey(sheetId);
    }

    public void requestRole(Role role, UUID sheetId) {
        RoleRequest request = new RoleRequest(this, role, sheetId);
        RoleRequestManager.getInstance().addRequest(request);
        support.addPropertyChangeListener(ownedSheets.get(sheetId));
    }

    public void acceptRequest(RoleRequest request) {
        validateSheetOwnership(request.getSheetId());
        RoleRequestManager.getInstance().acceptRequest(request);
    }

    public void rejectRequest(RoleRequest request) {
        validateSheetOwnership(request.getSheetId());
        RoleRequestManager.getInstance().rejectRequest(request);
    }

    public List<RoleRequest> getPendingRoleRequests() {
        return RoleRequestManager.getInstance().getPendingRequests(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("status".equals(evt.getPropertyName()) && RequestStatus.ACCEPTED.equals(evt.getNewValue())) {
            setRole(((RoleRequest) evt.getSource()).getSheetId(), (Role) evt.getNewValue());
        }
    }

    private void validateSheetOwnership(UUID sheetId) {
        if (!ownsSheet(sheetId)) {
            throw new IllegalArgumentException("User " + username + " cannot reject request for sheet " + sheetId);

        }
    }
}
