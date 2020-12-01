package bgu.spl.mics;
import java.util.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBusImpl bus = null;
	private final HashMap<Event,Future> futureMap;
	private final HashMap<MicroService,Queue<Message>> queueMap;
	private final HashMap<Class,Queue<MicroService>> eventSubscribersMap;
	private static final Object newBusLock = new Object();
	private final Object sendingLock = new Object();
	private final Object subscribeLock = new Object();


	private MessageBusImpl(){
		futureMap = new HashMap<>();
		queueMap = new HashMap<>();
		eventSubscribersMap = new HashMap<>();
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
				eventSubscribersMap.put(type, new ArrayDeque<>());
			}
			eventSubscribersMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (subscribeLock){
			if (!eventSubscribersMap.containsKey(type)){
				eventSubscribersMap.put(type,new ArrayDeque<>());
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
			if(eventSubscribersMap.get(b.getClass()) != null && !eventSubscribersMap.get(b.getClass()).isEmpty()){
				for (MicroService m :eventSubscribersMap.get(b.getClass())){
					if (queueMap.get(m) != null){
						queueMap.get(m).add(b);
					}
				}
				synchronized (this){
					notifyAll();
				}
			}
		}
	}
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (sendingLock){
			if(eventSubscribersMap.get(e.getClass()) == null || eventSubscribersMap.get(e.getClass()).isEmpty()){
				return null;
			}
			Future<T> future = new Future<>();
			futureMap.put(e,future);
			Queue<MicroService> eventQueue = eventSubscribersMap.get(e.getClass());
			MicroService m = eventQueue.poll();
			queueMap.get(m).add(e);
			eventQueue.add(m);
			synchronized (this){
				notifyAll();
			}
			return future;
		}
	}

	@Override
	public void register(MicroService m) {
		queueMap.put(m,new ArrayDeque<>());
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (sendingLock){
			for (Class type :eventSubscribersMap.keySet()){
				eventSubscribersMap.get(type).remove(m);
			}
			queueMap.remove(m);
		}
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		while (queueMap.get(m) == null || queueMap.get(m).isEmpty()){
			try{
				wait();
			}catch (InterruptedException ignored){}
		}
		notifyAll();
		return queueMap.get(m).poll();
	}
}
