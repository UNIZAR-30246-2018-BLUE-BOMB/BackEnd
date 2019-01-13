package bluebomb.urlshortener.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class StatsAgentTest {


    @Test
    public void verifyConstructors(){
        StatsAgent statsAgent = new StatsAgent("chrome");

        assertNotNull(statsAgent);
        assertNotNull(statsAgent.getAgent());
        assertEquals("chrome", statsAgent.getAgent());
    }

    @Test
    public void verifyGettersAndSetters(){
        StatsAgent statsAgent = new StatsAgent("chrome");

        assertEquals("chrome", statsAgent.getAgent());
        statsAgent.setAgent("firefox");
        assertEquals("firefox", statsAgent.getAgent());

    }

    @Test
    public void verifyHash(){
        StatsAgent statsAgent = new StatsAgent("chrome");
        assertEquals(-1361128807, statsAgent.hashCode());
    }

    @Test
    public void verifyEquals(){
        StatsAgent statsAgent = new StatsAgent("chrome");
        StatsAgent statsAgent2 = new StatsAgent("chrome");

        assertEquals(statsAgent, statsAgent2);
        assertNotEquals(statsAgent, new Object());
        assertNotEquals(statsAgent, null);
    }
}