package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import static java.lang.Thread.currentThread;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private Diary diary = Diary.getDiary();

    public C3POMicroservice() {super("C3PO");

    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, c-> {
            Ewoks ewoks = Ewoks.get();
            ewoks.acquireEwoks(c.getAttack().getSerials());
            try{currentThread().sleep(c.getAttack().getDuration());
            }catch (InterruptedException ignored){}
            ewoks.releaseEwoks(c.getAttack().getSerials());
            complete(c,true);
            diary.addToTotalAttacks();
            if (c.getAttackNumber() <= 1){
                diary.setHanSoloFinish(System.currentTimeMillis());
            }});

        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setC3POTerminate(System.currentTimeMillis());
        });
    }
}
