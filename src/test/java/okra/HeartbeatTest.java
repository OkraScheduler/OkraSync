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

import okra.exception.InvalidOkraItemException;
import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class HeartbeatTest extends OkraBaseContainerTest {

    @Test
    public void heartbeatHappyDayTest() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusHours(1));
        getDefaultOkra().schedule(item);
        Optional<DefaultOkraItem> retrievedItemOpt = getDefaultOkra().peek();
        assertThat(retrievedItemOpt).isPresent();

        DefaultOkraItem retrievedItem = retrievedItemOpt.get();
        Optional<DefaultOkraItem> hbItem = getDefaultOkra().heartbeat(retrievedItem);
        assertThat(hbItem).isPresent();
        assertThat(hbItem.get().getHeartbeat()).isAfter(LocalDateTime.now().minusMinutes(1));
    }

    @Test(expected = InvalidOkraItemException.class)
    public void heartbeatShouldFailIfHeartbeatIsNullTest() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusHours(1));
        getDefaultOkra().schedule(item);
        Optional<DefaultOkraItem> retrievedItemOpt = getDefaultOkra().peek();
        assertThat(retrievedItemOpt).isPresent();

        DefaultOkraItem retrievedItem = retrievedItemOpt.get();

        retrievedItem.setHeartbeat(null); // here
        Optional<DefaultOkraItem> hbItem = getDefaultOkra().heartbeat(retrievedItem);
        assertThat(hbItem).isPresent();
        assertThat(hbItem.get().getHeartbeat()).isAfter(LocalDateTime.now().minusMinutes(1));
    }

    @Test
    public void heartbeatShouldFailIfHeartbeatIsNotValidIsNullTest() {
        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now().minusHours(1));
        getDefaultOkra().schedule(item);
        Optional<DefaultOkraItem> retrievedItemOpt = getDefaultOkra().peek();
        assertThat(retrievedItemOpt).isPresent();

        DefaultOkraItem retrievedItem = retrievedItemOpt.get();

        retrievedItem.setHeartbeat(LocalDateTime.now().minusMinutes(5)); // heartbeat date was modified
        Optional<DefaultOkraItem> hbItem = getDefaultOkra().heartbeat(retrievedItem);
        assertThat(hbItem).isNotPresent();
    }

}
