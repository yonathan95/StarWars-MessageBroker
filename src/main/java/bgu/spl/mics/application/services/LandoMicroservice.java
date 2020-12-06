package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 * @code long duration - the time required from the microservice to sleep when simulate the deactivation event
 * @code Diary diary - a singleton used to record the time of the thread actions.
 */
public class LandoMicroservice  extends MicroService {
    long duration;
    private final Diary diary = Diary.getDiary();
    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
    }

    @Override
    /**
     * this method is called once when the event loop starts.
     * Used by the microservices to subscribes to the messages that it is interested to
     * receive, and to define a callback function to how it will handle it.
     */
    protected void initialize() {

        subscribeEvent(BombDestroyerEvent.class, c -> { //Pram. c: instance of type Message.
            // simulate the deactivation by sleeping, complete the associated future for this event , and send a finish broadcast
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
