import lab.idioglossia.sloth.Collection;
import lab.idioglossia.sloth.SlothStorage;
import lab.idioglossia.sloth.Value;

import java.io.IOException;

public class Sample {
    public static void main(String[] args) throws IOException {
        SlothStorage slothStorage = new SlothStorage(".db/", 3, 10);
        Collection<Integer, String> collection = slothStorage.getCollectionOfType("posts", Collection.Type.LIST, String.class, ".json");
        System.out.println("Collection size before save: " + collection.size());
        for(int i = 0; i < 100; i++){
            collection.save(new Value<String>() {
                @Override
                public String getData() {
                    return "{\"key\":\"value\"}";
                }
            });
        }
        System.out.println("Collection size after save: " + collection.size());
        System.out.println(collection.get(1).getData());

        Collection<String, String> configsCollection = slothStorage.getCollectionOfType("configs", Collection.Type.MAP, String.class);
        configsCollection.save("main", new Value<String>() {
            @Override
            public String getData() {
                return "a:b";
            }
        });

        System.out.println(configsCollection.size());
        System.out.println(configsCollection.get("main").getData());

    }
}
