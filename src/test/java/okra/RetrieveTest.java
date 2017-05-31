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
package okra;

import okra.exception.OkraItemNotFoundException;
import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class RetrieveTest extends OkraBaseContainerTest {

    @Test
    public void retrieveHappyDayTest() throws OkraItemNotFoundException {
        // Given a scheduled item...
        final DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusMinutes(5));
        getDefaultOkra().schedule(item);

        // When retrieve is executed...
        final DefaultOkraItem retrievedItem = getDefaultOkra().retrieve();

        // It should return an item...
        assertThat(retrievedItem).isNotNull();

        getDefaultOkra().delete(retrievedItem);
    }

    @Test(expected = OkraItemNotFoundException.class)
    public void retrieveShouldThrowExceptionIfNoItemIsFoundTest() throws OkraItemNotFoundException {
        // Should throw exception because no item is scheduled / found.
        getDefaultOkra().retrieve();
    }
}