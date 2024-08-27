package users;

import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final Map<String, User> users = new HashMap<>();

    private UserManager() {}

    public void addUser(User user) {
        if (isUserExists(user.getUsername())) {
            throw new IllegalArgumentException("User already exists");
        }
        users.put(user.getUsername(), user);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public boolean isUserExists(String username) {
        return users.containsKey(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void clear() {
        users.clear();
    }

    private static class UserManagerHolder {
        private static final UserManager INSTANCE = new UserManager();
    }

    public static UserManager getInstance() {
        return UserManagerHolder.INSTANCE;
    }
}
