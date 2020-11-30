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


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private List<Future> futureList;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		futureList = new ArrayList<Future>();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(FinishBroadcast.class, c-> terminate());
        for (Attack attack :attacks){
            Future future = (sendEvent(new AttackEvent(attack)));
            while (future == null){ //TODO runtime
                future = (sendEvent(new AttackEvent(attack)));
            }
            futureList.add(future);
        }
        for (Future future: futureList){
            future.get();
        }
        Future<Boolean> future = sendEvent(new DeactivationEvent());
        while (future == null){ //TODO runtime
            future = (sendEvent(new DeactivationEvent()));
        }
        if (future.get()){
            Future destroyerFuture = sendEvent(new BombDestroyerEvent());
            while (destroyerFuture == null){ //TODO runtime
                destroyerFuture = (sendEvent(new DeactivationEvent()));
            }
        }






    }
}
