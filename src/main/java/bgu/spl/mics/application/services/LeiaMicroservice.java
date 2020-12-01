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
    protected void initialize() {
        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setLeiaTerminate(System.currentTimeMillis());
        });

        int attackNumber = attacks.length;
        for (Attack attack :attacks){
            Future future = (sendEvent(new AttackEvent(attack,attackNumber)));
            while (future == null){ //TODO runtime
                future = (sendEvent(new AttackEvent(attack,attackNumber)));
            }
            --attackNumber;
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
                destroyerFuture = (sendEvent(new BombDestroyerEvent()));
            }
        }
    }
}
