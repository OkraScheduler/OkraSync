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

import okra.base.OkraStatus;
import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PeekTest extends OkraBaseContainerTest {

    @Test
    public void ensurePeekDoesNotRetrieveTheSameItemTwiceTest() {
        final DefaultOkraItem persistedItem = new DefaultOkraItem();
        persistedItem.setRunDate(LocalDateTime.now().minusNanos(100));
        getDefaultOkra().schedule(persistedItem);

        final Optional<DefaultOkraItem> retrievedOpt = getDefaultOkra().peek();
        assertThat(retrievedOpt.isPresent()).isTrue();

        final DefaultOkraItem item = retrievedOpt.get();

        final long processingItemsCount = getDefaultOkra().countByStatus(OkraStatus.PROCESSING);
        assertThat(processingItemsCount).isEqualTo(1L);

        final long pendingItemsCount = getDefaultOkra().countByStatus(OkraStatus.PENDING);
        assertThat(pendingItemsCount).isEqualTo(0L);

        final long doneItemsCount = getDefaultOkra().countByStatus(OkraStatus.DONE);
        assertThat(doneItemsCount).isEqualTo(0L);

        final Optional<DefaultOkraItem> optThatShouldBeEmpty = getDefaultOkra().peek();
        assertThat(optThatShouldBeEmpty.isPresent()).isFalse();

        getDefaultOkra().delete(item);
    }

}