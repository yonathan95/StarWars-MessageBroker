package bgu.spl.mics;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {
    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testResolve(){
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void testGet() {
        String s = "someResult";
        assertNull(future);
        future.resolve(s);
        assertTrue(future.get()==s);
    }

    @Test
    public void testisDone() {
        String s = "someResult";
        assertFalse(future.isDone());
        future.resolve(s);
        assertTrue(future.isDone());
    }
    @Test
        public void testGetTime() {
        String s = "someResult";
        assertNull(future);
        future.resolve(s);
        assertTrue(future.get (30, TimeUnit.MILLISECONDS) == s);
    }
}
