package bgu.spl.mics.application.passiveObjects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EwokTest {

    private Ewok ewok;

    @Before
    public void setUp() throws Exception {
        ewok = new Ewok(0);
    }

    @Test
    // pre: ewok.avaliable == true
    // post : pre: ewok.avaliable == false;
    // test if acquire() change ewok.avilable in to false
    public void testAcquire() {
        assertTrue(ewok.available);
        ewok.acquire();
        assertFalse(ewok.available);
    }

    @Test
    // pre: ewok.avaliable == true
    // post : pre: ewok.avaliable == false
    // test if release() change ewok.avilable in to false
    public void testRelease() {
        ewok.acquire();
        assertFalse(ewok.available);
        ewok.release();
        assertTrue(ewok.available);
    }
}