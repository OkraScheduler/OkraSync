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

import okra.base.model.OkraStatus;
import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PollTest extends OkraBaseContainerTest {

    @Test
    public void pollTest() {
        // Given a scheduled and delayed item...
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusHours(1));
        getDefaultOkra().schedule(item);

        // When we poll the item...
        Optional<DefaultOkraItem> retrievedItemOpt = getDefaultOkra().poll();

        // Item should be present
        assertThat(retrievedItemOpt).isPresent();

        // But okra should not have any pending items
        assertThat(getDefaultOkra().countByStatus(OkraStatus.PENDING)).isEqualTo(0L);

        // Neither any processing items
        assertThat(getDefaultOkra().countByStatus(OkraStatus.PROCESSING)).isEqualTo(0L);

        // And peek, of course, should not retrieve any items
        assertThat(getDefaultOkra().peek()).isNotPresent();
    }
}
