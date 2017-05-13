package okra;

import okra.index.IndexCreator;
import okra.model.DefaultOkraItem;
import org.junit.Test;

import java.time.LocalDateTime;

public class EnsureIndexesTest extends OkraBaseContainerTest {

    @Test
    public void ensureIndexesTest() {

        DefaultOkraItem item = new DefaultOkraItem();
        item.setRunDate(LocalDateTime.now());

        getDefaultOkra().schedule(item);
        IndexCreator.ensureIndexes((OkraSimple<DefaultOkraItem>) getDefaultOkra(), getDefaultMongo(), "okraSimpleTests", "okraSimple");
    }

}
