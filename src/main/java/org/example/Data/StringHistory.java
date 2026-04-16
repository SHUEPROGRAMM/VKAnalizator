package org.example.Data;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.example.General;
import org.example.Remove;
import org.example.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class StringHistory {
    public static class Node {
        public final String lower;
        public ArrayList<Integer> indices = new ArrayList<>();

        public Node(String lower) {
            this.lower = lower;
        }
    }

    public ArrayList<Node> nodes = new ArrayList<>();
    public TreeMap<Integer, ArrayList<Integer>> map = new TreeMap<>();
    public ArrayList<String> strings = new ArrayList<>();
    public ArrayList<Integer> backIndices = new ArrayList<>();

    public void clear() {
        nodes.clear();
        map.clear();
        strings.clear();
        backIndices.clear();
    }

    public int add(String string) {
        String lower = string.toLowerCase();
        ArrayList<Integer> indices = map.computeIfAbsent(lower.hashCode(), s -> new ArrayList<>());

        for (int index : indices) {
            for (int element : nodes.get(index).indices)
                if (strings.get(element).equals(string))
                    return element;

            int buffer = strings.size();
            strings.add(string);
            backIndices.add(index);
            nodes.get(index).indices.add(buffer);
            return buffer;
        }

        indices.add(nodes.size());
        backIndices.add(nodes.size());
        nodes.add(new Node(lower));
        nodes.getLast().indices.add(strings.size());
        int buffer = strings.size();
        strings.add(string);
        return buffer;
    }

    public int[] load(DataInputStream dataInputStream, int count) throws IOException {
        int[] buffer = new int[count];
        for (int index = 0; index < count; ++index)
            buffer[index] = add(dataInputStream.readUTF());
        return buffer;
    }

    public void save(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(strings.size());
        for (String element : strings)
            dataOutputStream.writeUTF(element);
    }

    public int search(String string) {
        String lower = string.toLowerCase();
        ArrayList<Integer> indices = map.get(lower.hashCode());
        if (indices == null) return -1;

        for (int index : indices)
            if (nodes.get(index).lower.equals(lower))
                return index;
        return -1;
    }

    public ArrayList<Integer> searchFuzzy(String string, int percent) {
        ArrayList<Integer> buffer = new ArrayList<>();
        String lower = string.toLowerCase();

        for (int index = 0; index < nodes.size(); ++index) {
            int result = FuzzySearch.partialRatio(lower, nodes.get(index).lower);
            if (result >= percent) buffer.add(index);
        } return buffer;
    }

    private static class SearchBaseThread extends Thread {
        public final String lower;
        public final int percent;
        public final ArrayList<Integer> buffer = new ArrayList<>();
        int begin, end;

        public SearchBaseThread(String lower, int percent, int begin, int end) {
            this.lower = lower;
            this.percent = percent;
            this.begin = begin;
            this.end = end;
        }
    }

    private class SearchThread extends SearchBaseThread {
        public SearchThread(String lower, int percent, int begin, int end) {
            super(lower, percent, begin, end);
        }

        @Override
        public void run() {
            for (int index = begin; index < end; ++index) {
                int result = FuzzySearch.partialRatio(lower, nodes.get(index).lower);
                if (result >= percent) buffer.add(index);
            }
        }
    }

    private class SearchIndicesThread extends SearchBaseThread {
        public final ArrayList<Integer> search;

        public SearchIndicesThread(String upper, int percent, int begin, int end, ArrayList<Integer> search) {
            super(upper, percent, begin, end);
            this.search = search;
        }

        @Override
        public void run() {
            for (int index = begin; index < end; ++index) {
                int result = FuzzySearch.partialRatio(lower, nodes.get(search.get(index)).lower);
                if (result >= percent) buffer.add(search.get(index));
            }
        }
    }

    public ArrayList<Integer> search(String string, int percent, int thread) throws InterruptedException {
        ArrayList<Integer> buffer = new ArrayList<>();
        thread = Math.min(nodes.size(), thread);
        int length = nodes.size() / thread;
        int fix = nodes.size() % thread;
        String upper = string.toUpperCase();
        int begin = 0;
        int end = 0;

        SearchThread[] searchThreads = new SearchThread[thread];
        for (int index = 0; index < thread; ++index) {
            end += length;
            if (index < fix) end++;
            searchThreads[index] = new SearchThread(upper, percent, begin, end);
            searchThreads[index].start();
            begin = end;
        }

        for (SearchThread element : searchThreads) {
            element.join();
            buffer.addAll(element.buffer);
        } return buffer;
    }

    public ArrayList<Integer> getIndices(TreeSet<Integer> data) {
        TreeSet<Integer> buffer = new TreeSet<>();
        for (int element : data) {
            buffer.add(backIndices.get(element));
        } return new ArrayList<>(buffer);
    }

    public ArrayList<Integer> search(String string, int percent, int thread, ArrayList<Integer> search) throws InterruptedException {
        ArrayList<Integer> buffer = new ArrayList<>();
        Utils.DecomposeTheQuantity decomposeTheQuantity = new Utils.DecomposeTheQuantity(search.size(), thread);
        SearchIndicesThread[] searchIndicesThreads = new SearchIndicesThread[decomposeTheQuantity.thread];
        String lower = string.toLowerCase();

        for (int index = 0; index < thread; ++index) {
            decomposeTheQuantity.next0();
            searchIndicesThreads[index] = new SearchIndicesThread(lower, percent, decomposeTheQuantity.begin, decomposeTheQuantity.end, search);
            searchIndicesThreads[index].start();
            decomposeTheQuantity.next1();
        }

        for (SearchIndicesThread element : searchIndicesThreads) {
            element.join();
            buffer.addAll(element.buffer);
        } return buffer;
    }

    public void remove(ArrayList<Integer> data, Remove.Node[] buffer) {
        strings = Utils.removeIndices(strings, data);
        ArrayList<Integer> indices = new ArrayList<>();

        for (int index = 0; index < nodes.size(); ++index) {
            Node element = nodes.get(index);
            element.indices = Remove.remove(element.indices, buffer);
            if (element.indices.isEmpty()) indices.add(index);
        }

        Remove.Node[] buffer2 = Remove.build(indices);
        nodes = Utils.removeIndices(nodes, indices);
        backIndices = Remove.remove(backIndices, buffer2);
    }
}
