package lab.idioglossia.sloth.storage;

import lab.idioglossia.sloth.collection.Collection;
import lab.idioglossia.sloth.collection.ICollection;
import lab.idioglossia.sloth.io.FileReader;
import lab.idioglossia.sloth.io.FileWriter;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private final Map<String, lab.idioglossia.sloth.collection.Collection> collectionMap = new HashMap<>();

    public SlothStorage(String path, int concurrentWrites, int concurrentReads) throws IOException {
        this.path = path;
        if (!Files.exists(Paths.get(path))) {
            Files.createDirectory(Paths.get(path));
        }
        this.fileWriter = new FileWriter(concurrentWrites);
        this.fileReader = new FileReader(concurrentReads);
    }

    @Override
    public <K, V extends Serializable> lab.idioglossia.sloth.collection.Collection<K, V> getCollectionOfType(String name, lab.idioglossia.sloth.collection.Collection.Type type, Class<V> dataClass) {
        return this.getCollectionOfType(name, type, dataClass, "");
    }

    @Override
    public <K, V extends Serializable> lab.idioglossia.sloth.collection.Collection<K, V> getCollectionOfType(String name, lab.idioglossia.sloth.collection.Collection.Type type, Class<V> dataClass, String extension) {
        try {
            lock.lock();

            AtomicReference<lab.idioglossia.sloth.collection.Collection<K,V>> collectionAtomicReference = new AtomicReference<>();

            lab.idioglossia.sloth.collection.Collection<K, V> collection = collectionMap.computeIfAbsent(name, new Function<String, lab.idioglossia.sloth.collection.Collection<K, V>>() {
                @Override
                public lab.idioglossia.sloth.collection.Collection<K, V> apply(String s) {
                    lab.idioglossia.sloth.collection.Collection<K, V> collection = new ICollection<>(path, name, type, dataClass, extension, fileWriter, fileReader);
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
    }

    @Override
    public boolean removeCollection(String name) {
        try {
            lock.lock();
            if(!collectionMap.containsKey(name)){
                return false;
            }
            collectionMap.remove(name);

            File file = new File(path, name);
            if(file.isDirectory()){
                return file.delete();
            }

            return false;

        }finally {
            lock.unlock();
        }
    }

    @Override
    public List<Collection> getCollections() {
        return new ArrayList<>(collectionMap.values());
    }

    @Override
    public Set<String> getCollectionNames() {
        return collectionMap.keySet();
    }
}
