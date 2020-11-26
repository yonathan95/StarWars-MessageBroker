package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class AttackEvent implements Event<Boolean> {
    private Future<Boolean> future;

    public AttackEvent(){
        future = new Future<Boolean>();
    }
    public Future<Boolean> getFuture() {
        return future;
    }
}
