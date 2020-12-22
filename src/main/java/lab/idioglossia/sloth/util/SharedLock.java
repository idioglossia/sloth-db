package lab.idioglossia.sloth.util;

import java.util.HashSet;
import java.util.Set;

public class SharedLock {
    private final Set<Integer> lockedKeys = new HashSet<>();

    public void lock(Integer key) throws InterruptedException {
        synchronized (lockedKeys) {
            while (!lockedKeys.add(key)) {
                lockedKeys.wait();
            }
        }
    }

    public void unlock(Integer key) {
        synchronized (lockedKeys) {
            lockedKeys.remove(key);
            lockedKeys.notifyAll();
        }
    }

    private static SharedLock sharedLock;

    public synchronized static SharedLock getInstance(){
        if(sharedLock == null){
            sharedLock = new SharedLock();
        }
        return sharedLock;
    }

    private SharedLock() {
    }
}
