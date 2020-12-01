package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private final Diary diary = Diary.getDiary();

    public C3POMicroservice() {super("C3PO");

    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, c-> {//Pram c: instance of type Message.
            // acquire the number of ewoks needed for the attack, simulate the attack by sleeping , release the ewoks and  complete the associated future for this event
            Ewoks ewoks = Ewoks.get();
            ewoks.acquireEwoks(c.getAttack().getSerials());
            try{
                Thread.sleep(c.getAttack().getDuration());
            }catch (InterruptedException ignored){}
            ewoks.releaseEwoks(c.getAttack().getSerials());
            complete(c,true);
            diary.addToTotalAttacks();
            if (c.getAttackNumber() <= 1){ // since we use the round Robin manner to divide the attacks, if the number of attack is one or lees we know there are no more attackEvent for this microservice to be execute.
                diary.setHanSoloFinish(System.currentTimeMillis());
            }});

        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setC3POTerminate(System.currentTimeMillis());
        });
    }
}
