package bgu.spl.mics.application.passiveObjects;


import java.util.List;
import java.util.Vector;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static Ewoks ewoks = null;
    private final Vector<Ewok> ewokList;
    private static final Object noEwoksLock = new Object();
    private int freeSerialNum;

    private Ewoks(){
        ewokList = new Vector<>();
        freeSerialNum = 1;
    }

    public static Ewoks get(){
        synchronized (noEwoksLock){
            if (ewoks == null){
                ewoks = new Ewoks();
            }
            return ewoks;
        }
    }

    public synchronized void addEwok(){
        if (ewoks == null){
            ewoks = get();
        }
        ewokList.add(new Ewok(freeSerialNum));
            ++freeSerialNum;
        notifyAll();
    }

    public synchronized void acquireEwoks(List<Integer> serials){
        for (Integer i:serials){
            while(!ewokList.elementAt(i-1).available){
                try{
                    wait();
                }catch(InterruptedException ignore){}
            }
            ewokList.elementAt(i-1).acquire();
        }
    }


    public synchronized void releaseEwoks(List<Integer> serials){
        for (Integer i:serials){
            ewokList.elementAt(i-1).release();
            notifyAll();
        }
    }
}
