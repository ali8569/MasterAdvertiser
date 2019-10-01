package ir.markazandroid.masteradvertiser.downloader;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Coded by Ali on 6/23/2019.
 */
public class BlockingMap<K,V> extends HashMap<K,V> {

    private final ReentrantLock mapLock;

    public BlockingMap() {
        mapLock = new ReentrantLock();
    }


    

    @Override
    public V put(K key, V value) {

        try {
            mapLock.lock();
            return super.put(key, value);
        }finally {
            mapLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        try {
            mapLock.lock();
            return super.get(key);
        }finally {
            mapLock.unlock();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        try {
            mapLock.lock();
            return super.remove(key, value);
        }finally {
            mapLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            mapLock.lock();
            return super.containsKey(key);
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
