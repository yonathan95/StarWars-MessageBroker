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

/**
 * Ewoks is a thread-safe singleton shared object
 * used for getting ewoks for attack.
 * @code Ewoks ewoks - The only instance of Ewoks.
 * @code Vector<Ewok> ewokList - A vector that holds ewoks.
 * @code int freeSerialNum - holds the next free serial num for creating a new Ewok.
 */
public class Ewoks {
    private static class EwoksHolder{
        private static final Ewoks instance = new Ewoks();
    }

    private final Vector<Ewok> ewokList;
    private int freeSerialNum;

    /**
     * Constructs the only ewoks instance of this class.
     */
    private Ewoks(){
        ewokList = new Vector<>();
        freeSerialNum = 1;
    }

    /**
     * Get the only ewoks instance if exists, else creates it first.
     */
    public static Ewoks get(){
        return EwoksHolder.instance;
    }

    /**
     * Creates a new Ewok and adds it to the ewok list.
     */
    public void addEwok(){
        ewokList.add(new Ewok(freeSerialNum));
            ++freeSerialNum;
        synchronized (this){
            notifyAll();
        }
    }

    /**
     * Acquire the ewoks according to their serial numbers in serials.
     * {@param serials} - a list containing the serial numbers of the required ewoks.
     */
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

    /**
     * Release the ewoks according to their serial numbers in serials.
     * {@param serials} - a list containing the serial numbers of the to be released ewoks.
     */
    public void releaseEwoks(List<Integer> serials){
        for (Integer i:serials){
            ewokList.elementAt(i-1).release();
            synchronized (this){
                notifyAll();
            }
        }
    }
}
