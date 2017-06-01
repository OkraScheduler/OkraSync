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

package okra.schedule;

import okra.OkraBaseContainerTest;
import okra.base.model.OkraStatus;
import okra.base.sync.OkraSync;
import okra.builder.OkraSyncBuilder;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomAttributeSchedulingTest extends OkraBaseContainerTest {

    @Test
    public void scheduleAndPeekItemWithCustomAttr() {

        OkraSync<CustomOkraItem> customOkra = (OkraSync<CustomOkraItem>) new OkraSyncBuilder<CustomOkraItem>()
                .withMongo(getDefaultMongo())
                .withDatabase("okraSimpleTests")
                .withCollection("okraSync")
                .withExpiration(5, TimeUnit.MINUTES)
                .withItemClass(CustomOkraItem.class)
                .build();



        CustomOkraItem item = new CustomOkraItem();
        item.setRunDate(LocalDateTime.now().minusMinutes(5));
        item.setSomeCustomStringValue("hello");
        item.setSomeCustomValue(10L);

        customOkra.schedule(item);

        Optional<CustomOkraItem> retrievedItemOpt = customOkra.peek();

        assertThat(retrievedItemOpt).isPresent();

        CustomOkraItem retrievedItem = retrievedItemOpt.get();

        assertThat(retrievedItem.getId()).isNotNull();
        assertThat(retrievedItem.getSomeCustomValue()).isEqualTo(10L);
        assertThat(retrievedItem.getSomeCustomStringValue()).isEqualTo("hello");
        assertThat(retrievedItem.getHeartbeat()).isNotNull();
        assertThat(retrievedItem.getRunDate()).isNotNull();
        assertThat(retrievedItem.getStatus()).isEqualTo(OkraStatus.PROCESSING);
    }

}
