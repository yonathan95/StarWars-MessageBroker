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
	private HashMap<Class,Set<MicroService>> eventSubscribersMap;
	private int roundRobin;
	private static Object newBusLock = new Object();
	private static Object elementLock = new Object();
	private static Object sendingLock = new Object();
	private static Object subscribeLock = new Object();
	private static final int HAN_SOLO_TURN = 1;
	private static final int C3PO_TURN = 2;

	private MessageBusImpl(){
		futureMap = new HashMap<Event,Future>();
		queueMap = new HashMap<MicroService,Queue<Message>>();
		eventSubscribersMap = new HashMap<Class,Set<MicroService>>();
		roundRobin = 0;
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
				eventSubscribersMap.put(type, new HashSet<MicroService>());
			}
			eventSubscribersMap.get(type).add(m);
			if (type.equals(AttackEvent.class) & roundRobin == 0) {
				if (m.getName().equals("Han")) {
					roundRobin = HAN_SOLO_TURN;
				}
				else{
					roundRobin = C3PO_TURN;
				}
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (subscribeLock){
			if (!eventSubscribersMap.containsKey(type)){
				eventSubscribersMap.put(type,new HashSet<MicroService>());
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
			if (e.getClass() == AttackEvent.class){
				if(roundRobin == HAN_SOLO_TURN){
					queueMap.get(HanSoloMicroservice.class).add(e);
					if (eventSubscribersMap.get(e.getClass()).contains(C3POMicroservice.class)){
						roundRobin = C3PO_TURN;
					}
				}
				else {
					queueMap.get(C3POMicroservice.class).add(e);
					if (eventSubscribersMap.get(e.getClass()).contains(HanSoloMicroservice.class)){
						roundRobin = HAN_SOLO_TURN;
					}
				}
			}
			else{
				for (MicroService m :eventSubscribersMap.get(e.getClass())){
					queueMap.get(m).add(e);
				}
			}
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
