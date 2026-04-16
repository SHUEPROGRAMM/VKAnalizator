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

    public void outConsole() throws InterruptedException {}
    public void outConsole(long date) throws InterruptedException {}
}
