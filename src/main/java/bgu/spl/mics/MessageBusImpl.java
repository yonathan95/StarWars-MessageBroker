package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.*;

import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl bus = null;
	private HashMap<Event,Future> futureMap;
	private HashMap<MicroService,Queue<Message>> queueMap;
	private HashMap<Class,Queue<MicroService>> eventSubscribersMap;
	private static Object newBusLock = new Object();
	private static Object elementLock = new Object();
	private static Object sendingLock = new Object();
	private static Object subscribeLock = new Object();

	private MessageBusImpl(){
		futureMap = new HashMap<Event,Future>();
		queueMap = new HashMap<MicroService,Queue<Message>>();
		eventSubscribersMap = new HashMap<Class,Queue<MicroService>>();
	}

	public static MessageBusImpl getBus(){
		synchronized (newBusLock){
			if (bus == null){
				bus = new MessageBusImpl();
			}
			return bus;
		}
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (subscribeLock){
			if (!eventSubscribersMap.containsKey(type)) {
				eventSubscribersMap.put(type, new ArrayDeque<MicroService>());
			}
			eventSubscribersMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (subscribeLock){
			if (!eventSubscribersMap.containsKey(type)){
				eventSubscribersMap.put(type,new ArrayDeque<MicroService>());
			}
			eventSubscribersMap.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = futureMap.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (sendingLock){
			if(!eventSubscribersMap.get(b.getClass()).isEmpty()){
				for (MicroService m :eventSubscribersMap.get(b.getClass())){
					queueMap.get(m).add(b);
				}
				synchronized (elementLock){
					notifyAll();
				}
			}
		}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (sendingLock){
			if(eventSubscribersMap.get(e.getClass()).isEmpty()){
				return null;
			}
			Future<T> future = new Future<T>();
			futureMap.put(e,future);
			Queue<MicroService> eventQueue = eventSubscribersMap.get(e.getClass());
			MicroService m = eventQueue.poll();
			queueMap.get(m).add(e);
			eventQueue.add(m);
			synchronized (elementLock){
				notifyAll();
			}
			return future;
		}
	}

	@Override
	public void register(MicroService m) {
		queueMap.put(m,new ArrayDeque<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		queueMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (elementLock){
			while (queueMap.get(m).isEmpty()){
				try{
					wait();
				}catch (InterruptedException ignored){}
			}
			return queueMap.get(m).poll();
		}
	}
}
