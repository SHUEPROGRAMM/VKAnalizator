package org.example;

public class Lock {
    private boolean one = false;
    private int two = 0;

    public synchronized void lock0() throws InterruptedException {
        while (one) wait();
        one = true;
    }

    public synchronized void unlock0() {
        one = false;
    }

    public synchronized void lock1() throws InterruptedException {
        if (two == 0) lock0();
        ++two;
    }

    public synchronized void unlock1() {
        --two;
        if (two == 0) unlock0();
    }
}
