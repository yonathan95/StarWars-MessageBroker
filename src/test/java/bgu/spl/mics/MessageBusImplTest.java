package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageBusImplTest {
    MessageBusImpl bus;
    MicroService mS;

    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getBus();
        mS = new HanSoloMicroservice();
    }

    @Test
    public void getBus() {
    }

    @Test
    public void testSubscribeEvent() {
        int size = bus.attackEventSubscribers.size();
        bus.subscribeEvent(AttackEvent.class,mS);
        assertEquals(bus.attackEventSubscribers.size(),size+1);
        assertEquals(mS,bus.attackEventSubscribers.get(size));
    }

    @Test
    public void testSubscribeBroadcast() {
        int size = bus.broadcastSubscribers.size();
        bus.subscribeBroadcast(Broadcast.class,mS);
        assertEquals(bus.broadcastSubscribers.size(),size+1);
        assertEquals(mS,bus.broadcastSubscribers.get(size));
    }

    @Test
    public void testComplete() {
        AttackEvent event = new AttackEvent();
        bus.complete(event,true);
        assertTrue(event.getFuture().isDone());
    }

    @Test
    public void testSendBroadcast() {
        bus.register(mS);
        int size = bus.hanSoloQueue.size();
        Broadcast broadcast = new Broadcast(){};
        bus.subscribeBroadcast(Broadcast.class,mS);
        bus.sendBroadcast(broadcast);
        assertEquals(bus.hanSoloQueue.size(),size+1);
    }

    @Test
    public void testSendEvent() {
        bus.register(mS);
        int size = bus.hanSoloQueue.size();
        AttackEvent event = new AttackEvent();
        bus.subscribeEvent(AttackEvent.class,mS);
        Future<Boolean> future = bus.sendEvent(event);
        assertEquals(bus.hanSoloQueue.size(),size+1);
        assertNotNull(future);
    }

    @Test
    public void testRegister() {
        bus.register(mS);
        assertNotNull(bus.hanSoloQueue);
    }

    @Test
    public void testUnregister() {
        bus.unregister(mS);
        assertNull(bus.hanSoloQueue);
    }

    @Test
    public void testAwaitMessage() {
        bus.register(mS);
        AttackEvent a = new AttackEvent();
        bus.hanSoloQueue.add(a);
        int size = bus.hanSoloQueue.size();
        try {
            Message m = bus.awaitMessage(mS);
            assertEquals(bus.hanSoloQueue.size(),size -1);
            assertEquals(a,m);
        }catch (Exception e){}
    }

    @Test
    public void testPeek(){
        bus.register(mS);
        AttackEvent a = new AttackEvent();
        bus.hanSoloQueue.add(a);
        assertEquals(bus.peek(mS),a);
    }

    @Test
    public void testRemove(){
        bus.register(mS);
        bus.hanSoloQueue.add(new AttackEvent());
        int size = bus.hanSoloQueue.size();
        bus.remove(mS);
        assertEquals(bus.hanSoloQueue.size(),size -1);
        try{
            bus.remove(mS);
            fail();
        }catch (Exception e){}


    }
}