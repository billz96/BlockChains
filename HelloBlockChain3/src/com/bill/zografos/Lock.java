package com.bill.zografos;

/**
 * Created by vasilis on 05/12/2019.
 */
public class Lock {
    int lockHoldCount;

    //Id of thread which is currently holding the lock.
    long lockingThread;

    /**
     * Creates an instance of Lock.
     * Initially lock hold count is 0.
     */
    public Lock(){
        lockHoldCount = 0;
    }

    public synchronized void lock() {

        //Acquires the lock if it is not held by another thread.
        // And sets lock hold count to 1.
        if(lockHoldCount == 0) {
            lockHoldCount++;
            lockingThread = Thread.currentThread().getId();
        } else if(lockHoldCount > 0 && lockingThread == Thread.currentThread().getId()) {
            //If current thread already holds lock then lock hold
            // count is increased by 1.
            lockHoldCount++;
        } else {
            //If the lock is held by another thread then the current
            // thread waits for another thread to release lock.
            try {
                wait();
                lockHoldCount++;
                lockingThread = Thread.currentThread().getId();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void unlock() {
        //current thread is not holding the lock, throw IllegalMonitorStateException.
        if(lockHoldCount == 0) throw new IllegalMonitorStateException();

        lockHoldCount--; //decrement lock hold count by 1

        //if lockHoldCount is 0, lock is released, and
        //one waiting thread is notified.
        if(lockHoldCount == 0) notify();
    }

    public synchronized boolean tryLock() {
        //Acquires the lock if it is not held by another thread and
        //returns true
        if(lockHoldCount == 0){
            lock();
            return true;
        }
        else {
            //If lock is held by another thread then method return false.
            return false;
        }
    }
}
