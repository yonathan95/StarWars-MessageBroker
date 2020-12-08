package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

/**
 * MessageBusImpl is a thread-safe singleton shared object
 * used for communication between micro-services.
 * @code MessageBusImpl bus - The only instance of MessageBusImpl.
 * @code HashMap<Event,Future> futureMap - A map that holds each event future.
 * @code HashMap<MicroService,Queue<Message>> queueMap - A map that holds each micro-service queue.
 * @code HashMap<Class,Queue<MicroService>> eventSubscribersMap - A map that holds each massage subscribers queue.
 * @code CountDownLatch counter - A CountDownLatch that help us synchronize the micro-services in order to make sure all subscribe before events are sent.
 */
public class MessageBusImpl implements MessageBus {
	private static class MessageBusImplHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private final CountDownLatch counter = new CountDownLatch(4);
	private final HashMap<Event,Future> futureMap;
	private final HashMap<MicroService,Queue<Message>> queueMap;
	private final HashMap<Class,Queue<MicroService>> messageSubscribersMap;
	private final Object sendingLock = new Object();
	private final Object subscribeLock = new Object();

	/**
	 * Constructs the only MessageBusImpl instance of this class.
	 */
	private MessageBusImpl(){
		futureMap = new HashMap<>();
		queueMap = new HashMap<>();
		messageSubscribersMap = new HashMap<>();
	}

	/**
	 * Get the only MessageBusImpl instance if exists, else creates it first.
	 */
	public static MessageBusImpl getBus(){
		return MessageBusImplHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		counter.countDown();
		synchronized (subscribeLock){
			if (!messageSubscribersMap.containsKey(type)) {
				messageSubscribersMap.put(type, new ArrayDeque<>());
			}
			messageSubscribersMap.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (subscribeLock){
			if (!messageSubscribersMap.containsKey(type)){
				messageSubscribersMap.put(type,new ArrayDeque<>());
			}
			messageSubscribersMap.get(type).add(m);
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
			if(messageSubscribersMap.get(b.getClass()) != null && !messageSubscribersMap.get(b.getClass()).isEmpty()){
				for (MicroService m : messageSubscribersMap.get(b.getClass())){
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
		try {
			counter.await();
		} catch (InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
		synchronized (sendingLock){
			if(messageSubscribersMap.get(e.getClass()) == null || messageSubscribersMap.get(e.getClass()).isEmpty()){
				return null;
			}
			Future<T> future = new Future<>();
			futureMap.put(e,future);
			Queue<MicroService> eventQueue = messageSubscribersMap.get(e.getClass());
			MicroService m = eventQueue.remove();
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
		synchronized (sendingLock){
			queueMap.put(m,new ArrayDeque<>());
		}

	}

	@Override
	public void unregister(MicroService m) {
		synchronized (sendingLock){
			for (Class type : messageSubscribersMap.keySet()){
				messageSubscribersMap.get(type).remove(m);
			}
			queueMap.remove(m);
		}
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		while (queueMap.get(m) == null || queueMap.get(m).isEmpty()){
			try{
				wait();
			}catch (InterruptedException e){
				throw e;
			}
		}
		notifyAll();
		return queueMap.get(m).poll();
	}
}
