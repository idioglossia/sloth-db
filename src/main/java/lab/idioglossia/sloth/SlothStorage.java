package lab.idioglossia.sloth;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class SlothStorage implements Storage {
    private final FileWriter fileWriter;
    private final FileReader fileReader;
    private final String path;
    private final Lock lock = new ReentrantLock();
    private final Map<String, Collection> collectionMap = new HashMap<>();

    public SlothStorage(String path, int concurrentWrites, int concurrentReads) {
        this.path = path;
        this.fileWriter = new FileWriter(concurrentWrites);
        this.fileReader = new FileReader(concurrentReads);
    }

    @Override
    public <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass) {
        return this.getCollectionOfType(name, type, dataClass, "");
    }

    @Override
    public <K, V extends Serializable> Collection<K, V> getCollectionOfType(String name, Collection.Type type, Class<V> dataClass, String extension) {
        try {
            lock.tryLock();

            try {
                lock.tryLock();

                AtomicReference<Collection<K,V>> collectionAtomicReference = new AtomicReference<>();

                Collection<K, V> collection = collectionMap.computeIfAbsent(name, new Function<String, Collection<K, V>>() {
                    @Override
                    public Collection<K, V> apply(String s) {
                        Collection<K, V> collection = new ICollection<>(path, name, type, dataClass, extension, fileWriter, fileReader);
                        collectionAtomicReference.set(collection);
                        return collection;
                    }
                });

                if(collection != null){
                    return collection;
                }else {
                    return collectionAtomicReference.get();
                }
            }finally {
                lock.unlock();
            }
        }finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeCollection(String name) {
        try {
            lock.tryLock();
            if(!collectionMap.containsKey(name)){
                return false;
            }

            File file = new File(path, name);
            if(file.isDirectory()){
                if (file.delete()) {
                    collectionMap.remove(name);
                    return true;
                }
            }

            return false;

        }finally {
            lock.unlock();
        }
    }

    @Override
    public List<Collection<?,?>> getCollections() {
        return new ArrayList<>(collectionMap.values());
    }

    @Override
    public Set<String> getCollectionNames() {
        return collectionMap.keySet();
    }
}
