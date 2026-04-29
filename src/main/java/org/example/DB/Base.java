package org.example.DB;

import org.example.Colors;

import java.util.ArrayList;
import java.util.TreeSet;

public class Base <T> {
    public ArrayList<T> data;

    public Base(ArrayList<T> data) { this.data = data; }

    public void outNoDate() throws InterruptedException { }
    public void outDate() throws InterruptedException { }

    public Base<T> copy() {
        return new Base<T>(new ArrayList<T>(data));
    }

    public void append(TreeSet<T> elements) {
        elements.addAll(this.data);
        this.data = new ArrayList<>(elements);
    }

    public void remove(TreeSet<T> elements) {
        this.data.removeAll(elements);
        if (this.data.isEmpty()) this.data = null;
    }

    static class ProbabilityMain extends Thread {
        public final int id;
        public final int indexIn;
        public final int indexTo;
        public final int percent;
        public ArrayList<Integer> buffer;

        public ProbabilityMain(int id, int indexIn, int indexTo, int percent) {
            this.id = id;
            this.indexIn = indexIn;
            this.indexTo = indexTo;
            this.percent = percent;
        }
    }

    public void outConsole() throws InterruptedException {}
    public void outConsole(long date) throws InterruptedException {}
}
