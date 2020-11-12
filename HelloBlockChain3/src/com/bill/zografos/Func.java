package com.bill.zografos;

/**
 * Created by vasilis on 05/12/2019.
 */
public interface Func<T> {
    void run (Atomic<T> atom);
}