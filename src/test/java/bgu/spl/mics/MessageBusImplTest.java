package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
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
        assertEquals(bus,MessageBusImpl.getBus());
    }

    @Test
    public void testSubscribeEvent() {
        try{
            int size = bus.attackEventSubscribers.size();
            bus.subscribeEvent(AttackEvent.class,mS);
            assertEquals(bus.attackEventSubscribers.size(),size+1);
            assertEquals(mS,bus.attackEventSubscribers.get(size));
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testSubscribeBroadcast() {
        try{
            int size = bus.broadcastSubscribers.size();
            bus.subscribeBroadcast(Broadcast.class,mS);
            assertEquals(bus.broadcastSubscribers.size(),size+1);
            assertEquals(mS,bus.broadcastSubscribers.get(size));
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testComplete() {
        try{
            AttackEvent event = new AttackEvent();
            bus.complete(event,true);
            assertTrue(event.getFuture().isDone());
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testSendBroadcast() {
        try{
            bus.register(mS);
            int size = bus.hanSoloQueue.size();
            Broadcast broadcast = new FinishBroadcast();
            bus.subscribeBroadcast(Broadcast.class,mS);
            bus.sendBroadcast(broadcast);
            assertEquals(bus.hanSoloQueue.size(),size+1);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testSendEvent() {
        try{
            bus.register(mS);
            int size = bus.hanSoloQueue.size();
            AttackEvent event = new AttackEvent();
            bus.subscribeEvent(AttackEvent.class,mS);
            Future<Boolean> future = bus.sendEvent(event);
            assertEquals(bus.hanSoloQueue.size(),size+1);
            assertNotNull(future);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testRegister() {
        try{
            bus.register(mS);
            assertNotNull(bus.hanSoloQueue);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testUnregister() {
        try{
            bus.unregister(mS);
            assertNull(bus.hanSoloQueue);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testAwaitMessage() {
        try{
            bus.register(mS);
            AttackEvent a = new AttackEvent();
            bus.hanSoloQueue.add(a);
            int size = bus.hanSoloQueue.size();
            try {
                Message m = bus.awaitMessage(mS);
                assertEquals(bus.hanSoloQueue.size(),size -1);
                assertEquals(a,m);
            }catch (Exception e){}
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testPeek(){
        try{
            bus.register(mS);
            AttackEvent a = new AttackEvent();
            bus.hanSoloQueue.add(a);
            assertEquals(bus.peek(mS),a);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testRemove(){
        try{
            bus.register(mS);
            bus.hanSoloQueue.add(new AttackEvent());
            int size = bus.hanSoloQueue.size();
            bus.remove(mS);
            assertEquals(bus.hanSoloQueue.size(),size -1);
            try{
                bus.remove(mS);
                fail();
            }catch (Exception e){}
        }catch (Exception e){
            fail();
        }


    }
}