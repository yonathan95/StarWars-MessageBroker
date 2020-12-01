package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import static java.lang.Thread.currentThread;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    long duration;
    private Diary diary = Diary.getDiary();
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {

        subscribeEvent(BombDestroyerEvent.class, c-> {
           try{currentThread().sleep(duration);
           }catch (InterruptedException ignored){}
           sendBroadcast(new FinishBroadcast());
           complete(c,true);});

        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setLandoTerminate(System.currentTimeMillis());
        });
    }
}
