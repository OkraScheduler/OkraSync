package okra.index;

import com.mongodb.MongoClient;
import com.mongodb.client.model.Indexes;
import okra.base.Okra;
import okra.base.OkraItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IndexCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexCreator.class);

    public static <T extends OkraItem> void ensureIndexes(Okra<T> okra,
                                                          MongoClient mongo,
                                                          String database,
                                                          String collection) {

        List<IndexDefinition> indexDefinitions = okra.indexDefinitions();

        for (IndexDefinition indexDef : indexDefinitions) {

            LOGGER.info("Ensuring index is created... {}", indexDef);
            String ensuredIndex;
            if (indexDef.getOrdering() == null
                    || indexDef.getOrdering().equals(Ordering.ASC)) {
                ensuredIndex = mongo
                        .getDatabase(database)
                        .getCollection(collection)
                        .createIndex(Indexes.ascending(indexDef.getAttrs()));
            } else {
                ensuredIndex = mongo
                        .getDatabase(database)
                        .getCollection(collection)
                        .createIndex(Indexes.descending(indexDef.getAttrs()));
            }
            LOGGER.info("Done. Index name: {}", ensuredIndex);

        }
    }

}
