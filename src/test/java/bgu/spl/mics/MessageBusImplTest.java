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
    private MessageBusImpl bus;
    private MicroService mS;
    private MicroService mS2;


    @Before
    public void setUp() throws Exception {
        bus = MessageBusImpl.getBus();
        mS = new HanSoloMicroservice();
        mS2 = new C3POMicroservice();
    }

    @Test
    // test that we get the singleton MessageBus
    public void testGetBus() {
        assertEquals(bus,MessageBusImpl.getBus());
    }

    @Test
    //test that complete update the a future instance's members as expected.
    public void testComplete() {
        try{
            AttackEvent event = new AttackEvent();
            bus.complete(event,true);
            assertTrue(event.getFuture().isDone());
            assertTrue(event.getFuture().get());
        }catch (Exception e){}
    }

    @Test
    /* test 2 method: sentBroadcast and subscribeBroadcast:
      first we subscribe two microservices to the same broadcast queue, then we send an broadcast, and make sure both
       microservices get the broadcast */
    public void testSendBroadcast() {
        try{
            FinishBroadcast  broadcast = new FinishBroadcast();
            bus.register(mS);
            bus.register(mS2);
            bus.subscribeBroadcast(broadcast.getClass(),mS);
            bus.subscribeBroadcast(broadcast.getClass(),mS2);
            bus.sendBroadcast(broadcast);
            Message broadcastCheck = bus.awaitMessage(mS);
            Message broadcastCheck2 = bus.awaitMessage(mS2);
            assertEquals(broadcast,broadcastCheck);
            assertEquals(broadcast,broadcastCheck2);
        }catch (Exception e){}
    }

    @Test
    /* test 2 method: sentEvent and subscribeEvent:
      first we subscribe a microservice to an event queue, then we send an event, and make sure the
       microservice got the event. in addition we make sure the future instance that we got from sentEvent is not null. */
    public void testSendEvent() {
        try{
            bus.register(mS);
            AttackEvent event = new AttackEvent();
            bus.subscribeEvent(event.getClass(),mS);
            Future<Boolean> future = bus.sendEvent(event);
            Message EventCheck = bus.awaitMessage(mS);
            assertEquals(event,EventCheck);
            assertNotNull(future);
        }catch (Exception e){}
    }

    @Test
    /*test for register method: we first crate a queue for the microservice and the we check that an event can be enqueue to the queue
     any exception mean that the test has failed.*/
    public void testRegister() {
        try{
            bus.register(mS);
            mS.subscribeEvent(AttackEvent.class,c->{});
            AttackEvent event = new AttackEvent();
            bus.sendEvent(event);
        }catch (Exception e){
            fail();
        }
    }

    @Test
    /* test for awaitMessage: we sent an event to an microservice queue ,
    and we make sure the awaitMessage retrieve this event form the current microservice queue.*/
    public void testAwaitMessage() {
        try{
            bus.register(mS);
            mS.subscribeEvent(AttackEvent.class,c->{});
            AttackEvent a = new AttackEvent();
            bus.sendEvent(a);
            Message m = bus.awaitMessage(mS);
            assertEquals(a,m);
        }catch (Exception e){}
    }
}