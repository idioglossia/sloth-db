import lab.idioglossia.sloth.Collection;
import lab.idioglossia.sloth.SlothStorage;
import lab.idioglossia.sloth.Value;

import java.io.IOException;

public class Sample {
    public static void main(String[] args) throws IOException {
        SlothStorage slothStorage = new SlothStorage("/home/sepehr/environment/.db/", 3, 10);
        Collection<Integer, String> collection = slothStorage.getCollectionOfType("posts", Collection.Type.LIST, String.class, ".json");
        System.out.println("Collection size before save: " + collection.size());
        collection.save(new Value<String>() {
            @Override
            public String getData() {
                return "{\"key\":\"value\"}";
            }
        });
        System.out.println("Collection size after save: " + collection.size());
        System.out.println(collection.get(1).getData());
    }
}
