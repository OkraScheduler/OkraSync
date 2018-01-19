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
package okra;

import com.mongodb.MongoClient;
import okra.builder.OkraSyncBuilder;
import okra.exception.InvalidOkraConfigurationException;
import org.junit.Test;

public class BuilderTest {

    @Test(expected = InvalidOkraConfigurationException.class)
    public void builderMongoNullTest() {
        new OkraSyncBuilder().withCollection("myCollection").build();
    }

    @Test(expected = InvalidOkraConfigurationException.class)
    public void builderDatabaseNullTest() {
        new OkraSyncBuilder().withMongo(new MongoClient()).withCollection("myCollection").withDatabase(null).build();
    }

    @Test(expected = InvalidOkraConfigurationException.class)
    public void builderDatabaseEmptyTest() {
        new OkraSyncBuilder().withMongo(new MongoClient()).withCollection("myCollection").withDatabase("").build();
    }
}