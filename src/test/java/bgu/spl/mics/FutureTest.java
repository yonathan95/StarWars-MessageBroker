package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {
    private Future<String> future;

    @Before
    public void setUp(){
        future = new Future<>();
    }

    @Test
    //pre: future.isDone == false;
    //post: future.isDone == True; future.result == someResult
    // test that resolve update future members.
    public void testResolve(){
        String str = "someResult";
        assertFalse(future.isDone());
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(future.get(), str);
    }

    @Test
    //test that future.get() return the right result.
    public void testGet() {
        String s = "someResult";
        future.resolve(s);
        assertSame(future.get(), s);
    }

    @Test
    //test that future.isDone() return the right result.
    public void testisDone() {
        String s = "someResult";
        assertFalse(future.isDone());
        future.resolve(s);
        assertTrue(future.isDone());
    }
    @Test
    //test that future.isDone() return the right result after a given time unit.
        public void testGetTime() {
        String s = "someResult";
        future.resolve(s);
        assertSame(future.get(30, TimeUnit.MILLISECONDS), s);
    }
}
