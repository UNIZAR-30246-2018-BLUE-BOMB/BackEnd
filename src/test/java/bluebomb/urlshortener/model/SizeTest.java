package bluebomb.urlshortener.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SizeTest {


    @Test
    public void verifyConstructors(){
        Size size = new Size(100, 100);

        assertEquals(100, size.getHeight());
        assertEquals(100, size.getWidth());
    }

    @Test
    public void verifyGettersAndSetters(){
        Size size = new Size(100, 100);

        assertEquals(100, size.getHeight());
        size.setHeight(200);
        assertEquals(200, size.getHeight());
        assertEquals(100, size.getWidth());
        size.setWidth(200);
        assertEquals(200, size.getWidth());
    }

    @Test
    public void verifyHash(){
        Size size = new Size(100, 100);
        assertEquals(4161, size.hashCode());
    }

    @Test
    public void verifyEquals(){
        Size size = new Size(100, 100);
        Size size1 = new Size(100, 100);

        assertEquals(size, size1);
        assertNotEquals(size, new Object());
        assertNotEquals(size, null);
    }
}