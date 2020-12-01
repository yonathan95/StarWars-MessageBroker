package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    long duration;
    private final Diary diary = Diary.getDiary();
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    protected void initialize() {

        subscribeEvent(BombDestroyerEvent.class, c -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ignored) {
            }
            complete(c, true);
            sendBroadcast(new FinishBroadcast());
        });

        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setLandoTerminate(System.currentTimeMillis());
        });
    }
}
