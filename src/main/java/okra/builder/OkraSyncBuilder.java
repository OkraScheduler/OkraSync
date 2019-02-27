/*
 * Copyright (c) 2019 Okra Scheduler
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
package okra.builder;

import com.mongodb.MongoClient;
import okra.OkraSyncImpl;
import okra.Preconditions;
import okra.base.model.OkraItem;
import okra.base.sync.OkraSync;
import okra.exception.InvalidOkraConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkraSyncBuilder<T extends OkraItem> extends OkraBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OkraSyncBuilder.class);

    private MongoClient client;

    @Override
    public OkraSync<T> build() {
        validateConfiguration();

        return new OkraSyncImpl<>(
                client,
                getDatabase(),
                getCollection(),
                getItemClass(),
                getExpireDuration(),
                getExpireDurationUnit()
        );
    }

    /**
     * Set mongo template that will be used by Okra
     *
     * @param client the mongo template
     * @return this builder
     */
    public OkraSyncBuilder<T> withMongo(final MongoClient client) {
        this.client = Preconditions.checkConfigurationNotNull(client, "client");
        return this;
    }

    private void validateConfiguration() {
        if (client == null
                || getCollection() == null
                || getCollection().isEmpty()
                || getDatabase() == null
                || getDatabase().isEmpty()
                || getExpireDuration() == null
                || getExpireDurationUnit() == null) {

            LOGGER.error("Invalid MongoScheduler configuration. " +
                            "Please verify params: " +
                            "[MongoClient is null? {}, Database: {}, " +
                            "Collection: {}, ExpireTime: {}, ExpireTimeUnit: {}]",
                    client == null, getDatabase(), getCollection(),
                    getExpireDuration(), getExpireDurationUnit());

            throw new InvalidOkraConfigurationException();
        }
    }
}