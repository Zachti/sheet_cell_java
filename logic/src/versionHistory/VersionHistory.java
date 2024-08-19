package versionHistory;

import java.util.Map;
import java.util.TreeMap;

import static common.utils.InputValidation.validateOrThrow;

public final class VersionHistory<T> implements IVersionHistory<T> {
    private int currentVersion;
    private final TreeMap<Integer, T> version2History = new TreeMap<>();

    public VersionHistory(T value, int currentVersion) {
        version2History.put(currentVersion, value);
        this.currentVersion = currentVersion;
    }

    @Override
    public void copy(TreeMap<Integer, T> version2History) {
        this.version2History.putAll(version2History);
        this.currentVersion = version2History.lastKey();
    }

    @Override
    public int getCurrentVersion() { return currentVersion; }

    @Override
    public T getByVersionOrUnder(int version) {
        validateOrThrow(
                version,
                v -> v >= 1,
                v -> "Version must be greater than 0, but was: " + v
        );

        return version2History.floorEntry(version).getValue();
    }

    @Override
    public void addNewVersion(T value, int version) {
        version2History.put(version,value);
        currentVersion = version;
    }

    @Override
    public TreeMap<Integer, T> getStore() { return version2History; }

    @Override
    public String toString() { return String.format("%d", currentVersion);}

    @Override
    @SuppressWarnings("unchecked")
    public VersionHistory<T> clone() {
        try {
            VersionHistory<T> clone = (VersionHistory<T>) super.clone();
            clone.copy(version2History);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
