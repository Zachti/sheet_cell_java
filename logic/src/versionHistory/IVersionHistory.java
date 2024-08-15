package versionHistory;

import java.util.Map;
import java.util.TreeMap;

public interface IVersionHistory<T> extends Cloneable{
    int getCurrentVersion();
    T getByVersionOrUnder(int version);
    void addNewVersion(T value, int version);
    void copy(Map<Integer, T> version2History);
    TreeMap<Integer, T> getStore();
}
