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
        ewok = new Ewok();
    }

    @Test
    public void testAcquire() {
        assertTrue(ewok.available);
        ewok.acquire();
        assertFalse(ewok.available);
    }

    @Test
    public void testRelease() {
        ewok.acquire();
        assertFalse(ewok.available);
        ewok.release();
        assertTrue(ewok.available);
    }
}