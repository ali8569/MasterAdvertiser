package ir.markazandroid.masteradvertiser.downloader;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coded by Ali on 6/22/2019.
 */
public class MappedLinkedBlockingQueue<K,E extends UniqueIdentifier<K>> extends LinkedBlockingQueue<E> {

    private final ReentrantLock mapLock;
    private final Map<K,E> map;

    public MappedLinkedBlockingQueue() {
        this.map = new HashMap<>();
        mapLock = new ReentrantLock();

    }

    @Override
    public void put(@NonNull E e) throws InterruptedException {
       try {
           mapLock.lock();
           map.put(e.getId(),e);
           super.put(e);
       }finally {
           mapLock.unlock();
       }
    }

    @NonNull
    @Override
    public E take() throws InterruptedException {
       try {
           E element = super.take();
           mapLock.lock();
           map.remove(element.getId());
           return element;
       }
       finally {
           mapLock.unlock();
       }
    }

    public E find(K key){
        try {
            mapLock.lock();
            return map.get(key);
        }finally {
            mapLock.unlock();
        }
    }

    public void lock(){
        mapLock.lock();
    }

    public void unlock(){
        mapLock.unlock();
    }
}
