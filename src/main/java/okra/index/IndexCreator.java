/*
 * Copyright (c) 2018 Okra Scheduler
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
import okra.base.model.OkraItem;
import okra.base.model.index.Ordering;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IndexCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexCreator.class);

    public static <T extends OkraItem> void ensureIndexes(final Okra<T> okra,
                                                          final MongoClient mongo,
                                                          final String database,
                                                          final String collection) {
        okra.getIndexDefs()
                .stream()
                .map(indexDef -> {
                    final boolean ascending = indexDef.getOrdering() == null
                            || indexDef.getOrdering().equals(Ordering.ASC);

                    final Bson ordering = ascending
                            ? Indexes.ascending(indexDef.getAttrs()) : Indexes.descending(indexDef.getAttrs());

                    return mongo
                            .getDatabase(database)
                            .getCollection(collection)
                            .createIndex(ordering);
                })
                .forEach(indexName -> LOGGER.info("Done. Index name: {}", indexName));
    }
}