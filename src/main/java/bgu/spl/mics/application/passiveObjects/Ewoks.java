package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.services.HanSoloMicroservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.lang.Thread.currentThread;

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
    private Vector<Ewok> ewokList;
    private volatile int freeEwoks;
    private static Object lock1 = new Object();
    private static Object lock2 = new Object();

    private Ewoks(){
        ewokList = new Vector<Ewok>();
        freeEwoks = 0;
    }

    public static Ewoks get(){
        synchronized (lock1){
            if (ewoks == null){
                ewoks = new Ewoks();
            }
            return ewoks;
        }
    }

    public synchronized void addEwoks(int numOfEwoks){
        while (ewoks == null){
            try{
                wait();
            }catch (InterruptedException ignored) {}
            }
        int serialNum = ewokList.size();
        for (int i = 0; i < numOfEwoks; ++i ){
            ewokList.add(new Ewok(serialNum));
            ++serialNum;
        }
        freeEwoks = freeEwoks + numOfEwoks;
        notifyAll();
    }

    public synchronized void acquireEwoks(int numOfEwoks){
        while (freeEwoks - numOfEwoks < 0){
            try{
                wait();
            }catch (InterruptedException ignored) {}
        }
        for (int i = 0; i < numOfEwoks; ++i){
            ewokList.get(ewokList.size() - freeEwoks + i).acquire();
        }
        freeEwoks = freeEwoks - numOfEwoks;
    }

    public synchronized void releaseEwoks(int numOfEwoks){
        for (int i = 0; i < numOfEwoks; ++i){
            ewokList.get(ewokList.size() - freeEwoks - i).release();
        }
        freeEwoks = freeEwoks + numOfEwoks;
        notifyAll();
    }
}
