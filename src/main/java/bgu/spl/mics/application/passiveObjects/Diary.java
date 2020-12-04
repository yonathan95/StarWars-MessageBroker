package bgu.spl.mics.application.passiveObjects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 * @code long totalAttacks - record the number of attecks that has been complete by C3PO and hanSolo
 * @code long HanSoloFinish - record the time hanSolo finished his last attack.
 * @code long C3POFinish- record the time C3PO finished his last attack.
 * @code long R2D2Deactivate - record the time R2D2  finished to deactivate the shield
 * @code long LeiaTerminate - record the time leia has been terminated.
 * @code long HanSoloTerminate - record the time HanSolo has been terminated
 * @code long C3POTerminate - record the time C3PO has been terminated
 * @code long R2D2Terminate - record the time  R2D2T has been terminated
 * @code long landoTerminate -record the time Lando has been terminated
 *
 */
public class Diary {
    private final AtomicInteger totalAttacks;
    private long HanSoloFinish = 0;
    private long C3POFinish= 0;
    private long R2D2Deactivate = 0;
    private long LeiaTerminate = 0;
    private long HanSoloTerminate = 0;
    private long C3POTerminate = 0;
    private long R2D2Terminate = 0;
    private long LandoTerminate = 0;
    private static final  Object lock = new Object();
    private static Diary diary = null;

    private Diary(){
        totalAttacks = new AtomicInteger(0);
    }
    public static Diary getDiary(){
        synchronized (lock){
            if (diary == null){
                diary = new Diary();
            }
            return diary;
        }
    }

    public synchronized void addToTotalAttacks() {
        totalAttacks.addAndGet(1);
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

}
