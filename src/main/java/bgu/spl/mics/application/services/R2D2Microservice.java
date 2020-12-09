package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 * @code long duration - the time required from the microservice to sleep when simulate the deactivation event
 * @code Diary diary - a singleton used to record the time of the thread actions.
 */
public class R2D2Microservice extends MicroService {
    private final long duration;
    private final Diary diary = Diary.getDiary();
    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    /**
     * this method is called once when the event loop starts.
     * Used by the microservices to subscribes to the messages that it is interested to
     * receive, and to define a callback function to how it will handle it.
     */
    protected void initialize() {
        subscribeBroadcast(FinishBroadcast.class, c-> {
            terminate();
            diary.setR2D2Terminate(System.currentTimeMillis());
        });

        subscribeEvent(DeactivationEvent.class, c-> {  //Param. c: instance of type Message.
            // simulate the deactivation by sleeping and complete the associated future for this event
            try{
                Thread.sleep(duration);
            }catch (InterruptedException ignored){}
            complete(c,true);
            diary.setR2D2Deactivate(System.currentTimeMillis());
            });
    }
}
