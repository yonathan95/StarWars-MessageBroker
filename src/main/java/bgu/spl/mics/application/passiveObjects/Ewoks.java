package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;

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
        private static Ewoks instance = new Ewoks();
    }

    private static Ewoks ewoks = null;
    private final Vector<Ewok> ewokList;
    private static final Object noEwoksLock = new Object();
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
    public synchronized void addEwok(){
        if (ewoks == null){
            ewoks = get();
        }
        ewokList.add(new Ewok(freeSerialNum));
            ++freeSerialNum;
        notifyAll();
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
    public synchronized void releaseEwoks(List<Integer> serials){ // TODO why is this function is sync? if because of the notify isnt is better to put the sync only at this? since if he release the ewok that some else release no one can quurie him at hist time .
        for (Integer i:serials){
            ewokList.elementAt(i-1).release();
            notifyAll();
        }
    }
}
