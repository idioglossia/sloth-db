import lab.idioglossia.sloth.collection.Collection;
import lab.idioglossia.sloth.collection.Value;
import lab.idioglossia.sloth.storage.SlothStorage;

import java.io.IOException;

public class Sample2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        SlothStorage slothStorage = new SlothStorage("/home/sepehr/environment/.db/", 3, 10);
        Collection<Integer, String> collection = slothStorage.getCollectionOfType("posts", Collection.Type.LIST, String.class, ".json");
        Value<String> save = collection.save(new Value<String>() {
            @Override
            public String getData() {
                return "{\"key\":\"value\"}";
            }
        });
        System.out.println("Saved with ID: " + save.id());
        Value<String> stringValue = collection.get(Integer.valueOf(save.id()));
        System.out.println(stringValue.getData());

    }
}
