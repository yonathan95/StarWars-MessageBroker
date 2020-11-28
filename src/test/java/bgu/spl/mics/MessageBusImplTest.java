package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageBusImplTest {
    MessageBusImpl bus;
    MicroService mS;
    MicroService mS2;
    List<Message> testerList;


    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getBus();
        mS = new HanSoloMicroservice();
        mS2 = new C3POMicroservice();
        testerList = new ArrayList<>();
    }

    @Test
    // test that we get the singleton MessageBus
    public void testGetBus() {
        assertEquals(bus,MessageBusImpl.getBus());
    }

  /*  @Test
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
    }*///TODO DEL

    @Test
    //test that complete update the a future instance's members as expected.
    public void testComplete() {
        try{
            AttackEvent event = new AttackEvent();
            bus.complete(event,true);
            assertTrue(event.getFuture().isDone());
            assertTrue(event.getFuture().get());
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testSendBroadcast() { // TODO add doc
        try{
            FinishBroadcast  broadcast = new FinishBroadcast();
            bus.register(mS);
            bus.register(mS2);
            mS.subscribeBroadcast(FinishBroadcast.class, c -> {});
            mS2.subscribeBroadcast(FinishBroadcast.class, c -> {});
            bus.sendBroadcast(broadcast);
            Message broadcastCheck = bus.awaitMessage(mS);
            Message broadcastCheck2 = bus.awaitMessage(mS2);
            assertEquals(broadcast,broadcastCheck);
            assertEquals(broadcast,broadcastCheck2);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testSendEvent() { // TODO add doc
        try{
            bus.register(mS);
            AttackEvent event = new AttackEvent();
            mS.subscribeEvent(AttackEvent.class, c -> {});
            Future<Boolean> future = bus.sendEvent(event);
            Message EventCheck = bus.awaitMessage(mS);
            assertEquals(event,EventCheck);
            assertNotNull(future);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testRegister() { // TODo
        try{
            bus.register(mS);
            assertNotNull(bus.hanSoloQueue);
        }catch (Exception e){
            fail();
        }
    }



    @Test
    public void testAwaitMessage() { // TODO
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
}