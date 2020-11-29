package bgu.spl.mics;

import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;
import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl bus = null;
	private Queue<Message> hanSoloQueue;
	private Queue<Message> c3POQueue;
	private Queue<Message> r2D2Queue;
	private Queue<Message> leiaQueue;
	private ArrayList<MicroService> attackEventSubscribers;
	private ArrayList<MicroService> deactivationEventSubscribers;
	private ArrayList<MicroService> broadcastSubscribers;

	private MessageBusImpl(){
		hanSoloQueue = null;
		c3POQueue = null;
		r2D2Queue = null;
		leiaQueue = null;
		attackEventSubscribers = new ArrayList<MicroService>();
		deactivationEventSubscribers = new ArrayList<MicroService>();
		broadcastSubscribers = new ArrayList<MicroService>();
	}

	public static MessageBusImpl getBus(){
		if (bus == null){
			bus = new MessageBusImpl();
		}
		return bus;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return new Future<T>();
	}

	@Override
	public void register(MicroService m) {
		if (m instanceof HanSoloMicroservice){
			hanSoloQueue = new ArrayDeque<Message>();
		}
		else if (m instanceof C3POMicroservice){
			c3POQueue = new ArrayDeque<Message>();
		}
		else if (m instanceof LeiaMicroservice){
			leiaQueue = new ArrayDeque<Message>();
		}
		else if (m instanceof R2D2Microservice){
			r2D2Queue = new ArrayDeque<Message>();
		}
	}

	@Override
	public void unregister(MicroService m) {
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}

}
