package bluebomb.urlshortener.model;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class ClickStatTest {


    @Test
    public void verifyConstructors(){
        ClickStat clickStat = new ClickStat();

        assertNotNull(clickStat);

        assertNull(clickStat.getAgent());
        assertNull(clickStat.getClicks());

        clickStat = new ClickStat("chrome", 100);
        assertEquals(clickStat.getAgent(), "chrome");
        assertEquals(100, (int) clickStat.getClicks());
    }

    @Test
    public void verifyGettersAndSetters(){
        ClickStat clickStat = new ClickStat();
        assertNull(clickStat.getAgent());

        clickStat.setAgent("chrome");
        assertEquals(clickStat.getAgent(), "chrome");

        assertNull(clickStat.getClicks());
        clickStat.setClicks(100);
        assertEquals(100, (int) clickStat.getClicks());
    }

    @Test
    public void verifyHash(){
        ClickStat clickStat = new ClickStat("chrome", 100);
        assertEquals(clickStat.hashCode(),754680043);
    }

    @Test
    public void verifyEquals(){
        ClickStat clickStat = new ClickStat("chrome", 100);
        ClickStat clickStat2 = new ClickStat("chrome", 100);

        assertEquals(clickStat, clickStat2);
        assertNotEquals(clickStat, new Object());
        assertNotEquals(clickStat, null);
    }
}
