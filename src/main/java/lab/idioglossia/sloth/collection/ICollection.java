package lab.idioglossia.sloth.collection;

import lab.idioglossia.sloth.io.FileReader;
import lab.idioglossia.sloth.io.FileWriter;
import lab.idioglossia.sloth.model.QueryResponseValue;
import lab.idioglossia.sloth.model.SaveResponseValue;
import lab.idioglossia.sloth.util.DBValuePathPredict;
import lab.idioglossia.sloth.util.PathUtil;
import lab.idioglossia.sloth.util.Validator;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ICollection<K,D extends Serializable> implements Collection<K,D> {
    private final String dbPath;
    private final String collectionName;
    private Type type;
    private Class<D> valueClass;
    private final FileWriter fileWriter;
    private final FileReader fileReader;
    private String extension = "";
    private volatile int size = -1;
    private volatile boolean sizeSet = false;
    private ListCollectionFileIdGenerator listCollectionFileIdGenerator;
    private File collectionFile;
    private volatile boolean stopped = false;

    public ICollection(String path, String collectionName, Type type, Class<D> valueClass, String extension, FileWriter fileWriter, FileReader fileReader){
        this.extension = extension;
        assert Validator.isValidExtension(extension);
        this.dbPath = path;
        this.fileWriter = fileWriter;
        this.fileReader = fileReader;
        assert Validator.isSupportedValue(valueClass);
        this.collectionName = collectionName;
        this.type = type;
        this.valueClass = valueClass;
        init();
    }

    public ICollection(String path, String collectionName, Type type, Class<D> valueClass, FileWriter fileWriter, FileReader fileReader){
        this(path, collectionName, type, valueClass, "", fileWriter, fileReader);
    }

    private void init() {
        this.collectionFile = new File(dbPath + collectionName);
        if(this.collectionFile.exists()){
            if(!this.collectionFile.isDirectory()){
                throw new IllegalStateException("Collection name can not be as same as existing file name: " + collectionName);
            }
            File configFile = new File(this.collectionFile, ".config");
            if(configFile.exists()){
                readAndValidateConfig(configFile);
            }else {
                createConfigFile(configFile);
            }
        }else {
            try {
                Files.createDirectory(Paths.get(this.collectionFile.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(type.equals(Type.LIST)){
            listCollectionFileIdGenerator = new ListCollectionFileIdGenerator(this.collectionFile, this.extension);
        }
    }

    private void createConfigFile(File configFile) {
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String configString = CollectionConfig.asString(this);
        fileWriter.write(configFile, configString);
    }

    private void readAndValidateConfig(File configFile) {
        String config = fileReader.readAsString(configFile);
        if (!CollectionConfig.match(config, this)) {
            throw new IllegalStateException("Collection already exists under different configuration");
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Class<D> valueClass() {
        return valueClass;
    }

    @Override
    public String getName() {
        return collectionName;
    }

    @Override
    public int size() {
        checkStopped();
        if(!sizeSet){
            try {
                size = (int) Files.list(this.collectionFile.toPath()).filter(new DBValuePathPredict(this.collectionFile)).count();
                sizeSet = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return size;
    }

    @Override
    public Value<D> get(K key) {
        checkStopped();
        String keyAsString = getKeyAsString(key);
        File file = new File(collectionFile, keyAsString + extension);

        if(!file.exists()){
            return new QueryResponseValue<D>(null, false);
        }

        if(valueClass.getName().equals(String.class.getName())) {
            String value = fileReader.readAsString(file);
            return new QueryResponseValue<D>((D) value, keyAsString);
        }else {
            byte[] bytes = fileReader.readAsByteArray(file);
            return new QueryResponseValue<D>((D) bytes, keyAsString);
        }
    }

    @Override
    public void update(K key, Value<D> value) {
        write(getKeyAsString(key), value, false);
    }

    @Override
    public Value<D> save(K key, Value<D> value) {
        if(type.equals(Type.LIST)){
            return save(value);
        }else {
            String keyAsString = getKeyAsString(key);
            write(keyAsString, value, true);
            return new SaveResponseValue<D>(value, keyAsString);
        }
    }

    @Override
    public Value<D> save(Value<D> value) {
        if(type.equals(Type.MAP)){
            throw new RuntimeException("Calling save(value) on map collection is illegal");
        }
        String id = String.valueOf(listCollectionFileIdGenerator.getId());
        write(id, value, true);
        listCollectionFileIdGenerator.increase();
        return new SaveResponseValue<D>(value, id);
    }

    @Override
    public synchronized void remove(K key) {
        File file = new File(collectionFile, getKeyAsString(key) + extension);
        if(file.exists()){
            boolean delete = file.delete();
            if(delete){
                synchronized (this){
                    size--;
                }
                if(type.equals(Type.LIST))
                    listCollectionFileIdGenerator.decrease();
            }

        }
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    @Override
    public synchronized java.util.Collection<K> getKeys() {
        try {
            List<K> keys = new ArrayList<>();
            Stream<Path> stream = Files.list(this.collectionFile.toPath()).filter(new DBValuePathPredict(this.collectionFile));
            List<Path> paths = Files.list(this.collectionFile.toPath()).filter(new DBValuePathPredict(this.collectionFile)).sorted().collect(Collectors.toList());
            paths.forEach(path -> {
                String key = PathUtil.cleanPath(path, collectionFile.getAbsolutePath()).replace(extension, "");
                if(type.equals(Type.MAP)){
                    keys.add((K) key);
                }else {
                    keys.add((K) new Integer(Integer.parseInt(key)));
                }
            });
            if(type.equals(Type.LIST))
                Collections.sort((List<Integer>) keys);
            return keys;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkStopped(){
        if(this.stopped){
            throw new IllegalStateException("Cant work with a collection after its stopped");
        }
    }

    private void write(String key, Value<D> value, boolean inc){
        File file = new File(collectionFile, key + extension);
        if(valueClass.getName().equals(String.class.getName())){
            fileWriter.write(file, (String) value.getData());
        }else {
            fileWriter.write(file, (byte[]) value.getData());
        }
        if(inc){
            synchronized (this){
                size++;
            }
        }
    }

    private String getKeyAsString(K key){
        assert Validator.isValidKey(key);
        return key instanceof String ? (String) key : String.valueOf(key);
    }


}
