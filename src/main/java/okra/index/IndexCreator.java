/*
 * Copyright (c) 2017 Okra Scheduler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
