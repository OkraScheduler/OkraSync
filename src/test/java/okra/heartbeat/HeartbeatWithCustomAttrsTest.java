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
package okra.heartbeat;

import okra.OkraBaseContainerTest;
import okra.base.sync.OkraSync;
import okra.builder.OkraSyncBuilder;
import okra.exception.InvalidOkraItemException;
import okra.model.DefaultOkraItem;
import okra.schedule.CustomOkraItem;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class HeartbeatWithCustomAttrsTest extends OkraBaseContainerTest {

    private OkraSync<CustomOkraItem2> customOkra;

    @Before
    public void prepareCustomOkra() {
        customOkra = (OkraSync<CustomOkraItem2>) new OkraSyncBuilder<CustomOkraItem2>()
                .withMongo(getDefaultMongo())
                .withDatabase("okraSimpleTests")
                .withCollection("okraSync")
                .withExpiration(5, TimeUnit.MINUTES)
                .withItemClass(CustomOkraItem2.class)
                .build();
    }

    @Test
    public void heartbeatHappyDayTest() {
        final CustomOkraItem2 item = new CustomOkraItem2();
        item.setRunDate(LocalDateTime.now().minusHours(1));
        item.setSomeCustomValue(20L);
        customOkra.schedule(item);

        final Optional<CustomOkraItem2> retrievedItemOpt = customOkra.peek();
        assertThat(retrievedItemOpt).isPresent();

        final CustomOkraItem2 retrievedItem = retrievedItemOpt.get();

        assertThat(retrievedItem.getSomeCustomValue()).isEqualTo(20L);

        Map<String, Object> customAttrs = new HashMap<>();
        customAttrs.put("someCustomValue", 77L);

        final Optional<CustomOkraItem2> hbItem = customOkra
                .heartbeatAndUpdateCustomAttrs(retrievedItem, customAttrs);

        assertThat(hbItem).isPresent();
        assertThat(hbItem.get().getHeartbeat()).isAfter(LocalDateTime.now().minusMinutes(1));
        assertThat(hbItem.get().getSomeCustomValue()).isEqualTo(77L);
    }

}