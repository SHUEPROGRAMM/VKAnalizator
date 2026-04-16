package org.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Remove {
    public record Node (int index, int poz) { }

    public static Node[] build(ArrayList<Integer> data) {
        Node[] buffer = new Node[data.size()];
        int poz = 1;

        for (int index = 0; index < data.size(); ++index)
            buffer[index] = new Node(data.get(index), poz++);
        return buffer;
    }

    public static Node[] build(TreeSet<Integer> data) {
        Node[] buffer = new Node[data.size()];
        int index = 0;
        int poz = 1;

        for (int element : data)
            buffer[index++] = new Node(element, poz++);
        return buffer;
    }

    public static int search(int element, Node[] data) {
        int begin = 0, end= data.length, index = 0;
        Node mid = data[index];

        while (begin < end) {
            index = begin + ((end - begin) >> 1);
            mid = data[index];

            if (element < mid.index) end = index;
            else if (element > mid.index) begin = index + 1;
            else return -1;
        } return ((element < mid.index) ? ((index == 0) ? element : element - data[index - 1].poz) : element - mid.poz);
    }

    public static ArrayList<Integer> remove(ArrayList<Integer> arrayList, Node[] data) {
        ArrayList<Integer> buffer = new ArrayList<>();
        for (int element : arrayList) {
            int result = search(element, data);
            if (result != -1) buffer.add(element);
        } return buffer;
    }

    public static <T> TreeMap<Integer, T> remove(TreeMap<Integer, T> map, Node[] node) {
        TreeMap<Integer, T> buffer = new TreeMap<>();
        for (Map.Entry<Integer, T> entry : map.entrySet()) {
            int result = search(entry.getKey(), node);
            if (result != -1) buffer.put(result, entry.getValue());
        } return buffer;
    }
}
