package com.bill.zografos;

/**
 * Created by vasilis on 05/12/2019.
 * The generic type 'T' is a primitive type
 */
public class Atomic<T> {
    private T value;
    private Lock lock = new Lock();

    public Atomic (T initialValue) {
        value = initialValue;
    }

    public T get() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    public void set(T newValue) {
        lock.lock();
        this.value = newValue;
        lock.unlock();
    }

    // change the old value with a new one and return the old value
    public T swap(T newValue) {
        lock.lock();
        try {
            T oldValue = value;
            value = newValue;
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

    // is input different from value
    public boolean compareWith(T input) {
        lock.lock();
        try {
            return this.value == input;
        } finally {
            lock.unlock();
        }
    }

    // check if input is different from current value and if it is change the current value
    public boolean compareAndSet(T input) {
        lock.lock();
        try {
            if (value != input) {
                value = input;
                return true; // value changed!
            } else {
                return false; // value didn't change!
            }
        } finally{
            lock.unlock();
        }
    }

    // it's not thread safe method! must be used inside modify method!
    public T unsafeGet() {
        return value;
    }

    // it's not thread safe method! must be used inside modify method!
    public void unsafeSet(T value) {
        this.value = value;
    }

    public void modify(Func func) {
        lock.lock();
        try {
            func.run(this);
        } finally {
            lock.unlock();
        }
    }
}
