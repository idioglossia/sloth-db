package lab.idioglossia.sloth;

import java.io.Serializable;
import java.util.List;

public interface Storage {
    <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass);
    <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass, String extension);
    boolean removeCollection(String name);
    <K, V extends Serializable> List<Collection<K, V>> getCollections();
    List<String> getCollectionNames();
}
