package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 * @code Attack[] attacks - an array of all the attack needed to be executed.
 * @code List<Future> futureList - list of all the future created from the attack events.
 * @code Diary diary - a singleton used to record the time of the thread actions.
 */
public class LeiaMicroservice extends MicroService {
	private final Attack[] attacks;
	private final List<Future> futureList;
	private final Diary diary = Diary.getDiary();
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		futureList = new ArrayList<>();
    }

    @Override
    /**
     * this method is called once when the event loop starts.
     * Used by the microservices to subscribes to the messages that it is interested to
     * receive, and to define a callback function to how it will handle it.
     * also used by leia to sent all the attacks event, and afterwards the Deactivation event and the BombDestroyer event
     */
    protected void initialize() {
        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setLeiaTerminate(System.currentTimeMillis());
        });

        for (Attack attack : attacks){
            Future future = sendEvent(new AttackEvent(attack));
            while (future == null){ // until the event has been sent, leia will send this event.
                future = sendEvent(new AttackEvent(attack));
            }
            futureList.add(future);
        }
        for (Future future : futureList){
            future.get(); // continue to the next line of code only when all the future in the future list has been complete, since get() return only when the future has been completed.
        }
        Future<Boolean> future = sendEvent(new DeactivationEvent());
        while (future == null){ // until the event has been sent, leia will send this event.
            future = sendEvent(new DeactivationEvent());
        }
        if (future.get()){
            Future destroyerFuture = sendEvent(new BombDestroyerEvent());
            while (destroyerFuture == null){ // until the event has been sent, leia will send this event.
                destroyerFuture = sendEvent(new BombDestroyerEvent());
            }
        }
    }
}
