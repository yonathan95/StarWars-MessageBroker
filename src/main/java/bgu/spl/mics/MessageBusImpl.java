package bgu.spl.mics;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl bus = null;
	protected Queue<Message> hanSoloQueue;
	protected Queue<Message> c3POQueue;
	protected Queue<Message> r2D2Queue;
	protected Queue<Message> leiaQueue;
	protected ArrayList<MicroService> attackEventSubscribers;
	protected ArrayList<MicroService> deactivationEventSubscribers;
	protected ArrayList<MicroService> broadcastSubscribers;

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
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		
	}

	@Override
	public void unregister(MicroService m) {
		
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		
		return null;
	}

	public Message peek(MicroService m){
		return null;
	}

	public void remove(MicroService m){

	}
}
