package users;

public enum RequestStatus {
    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String status;

    RequestStatus(String status) { this.status = status; }

    public String getStatus() { return status; }
}
