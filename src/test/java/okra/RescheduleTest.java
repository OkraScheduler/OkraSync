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

public class RescheduleTest extends OkraBaseContainerTest {

    @Test
    public void rescheduleTest() {
        // Given a scheduled AND delayed item...
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusHours(1));
        getDefaultOkra().schedule(item);

        // peek the item...
        Optional<DefaultOkraItem> retrievedItem = getDefaultOkra().peek();

        // It should be present, of course...
        assertThat(retrievedItem).isPresent();

        // Now let's try to reschedule it to 1 hour from now...
        retrievedItem.get().setRunDate(LocalDateTime.now().plusHours(1));

        // Rescheduling...
        getDefaultOkra().reschedule(retrievedItem.get());

        // Peek should not return any items now...
        assertThat(getDefaultOkra().peek()).isNotPresent();

        // But okra should have 1 pending item.
        assertThat(getDefaultOkra().countByStatus(OkraStatus.PENDING)).isEqualTo(1L);

        // And no delayed items! :)
        OkraSimple<DefaultOkraItem> okra = (OkraSimple<DefaultOkraItem>) getDefaultOkra();
        assertThat(okra.countDelayed()).isEqualTo(0);
    }

}
