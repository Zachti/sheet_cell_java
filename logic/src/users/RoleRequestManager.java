package users;

import java.util.ArrayList;
import java.util.List;

public class RoleRequestManager {
    private final List<RoleRequest> requests = new ArrayList<>();

    private RoleRequestManager() {}

    public void addRequest(RoleRequest request) {
        requests.add(request);
    }

    public void acceptRequest(RoleRequest request) {
        request.setStatus(RequestStatus.ACCEPTED);
    }

    public void rejectRequest(RoleRequest request) {
        request.setStatus(RequestStatus.REJECTED);
    }

    public List<RoleRequest> getPendingRequests(User user) {
        List<RoleRequest> pendingRequests = new ArrayList<>();
        requests.forEach(request -> {
            if (request.getStatus() == RequestStatus.PENDING && user.ownsSheet(request.getSheetId())) {
                pendingRequests.add(request);
            }
        });
        return pendingRequests;
    }

    private static class RoleRequestManagerHolder {
        private static final RoleRequestManager INSTANCE = new RoleRequestManager();
    }

    public static RoleRequestManager getInstance() { return RoleRequestManagerHolder.INSTANCE; }
}

