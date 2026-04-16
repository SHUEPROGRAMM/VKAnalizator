package org.example.Data;

import org.example.Console.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class OnlineHistory {
    public static class Node implements Comparable<Node> {
        public final long date;
        public final long back;
        public final int type;

        public Node(long date, long back, int type) {
            this.date = date;
            this.back = back;
            this.type = type;
        }

        public Node(DataInputStream dataInputStream) throws IOException {
            date = dataInputStream.readLong();
            back = dataInputStream.readLong();
            type = dataInputStream.readInt();
        }

        public void save(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeLong(date);
            dataOutputStream.writeLong(back);
            dataOutputStream.writeInt(type);
        }

        @Override
        public int compareTo(Node o) {
            return Long.compare(date, o.date);
        }
    }

    public ArrayList<Node> data = new ArrayList<>();

    public boolean update(long date, long back, int type) {
        if (data.isEmpty()) {
            data.add(new Node(date, back, type));
            return true;
        } else if (data.getLast().back != back || data.getLast().type != type) {
            data.add(new Node(date, back, type));
            return true;
        } else return false;
    }

    public void load(DataInputStream dataInputStream, int count) throws IOException {
        TreeSet<Node> set = new TreeSet<>();
        for (int index = 0; index < count; ++index)
            set.add(new Node(dataInputStream));

        if (!data.isEmpty()) {
            set.addAll(data);
            data.clear();
        }

        Iterator<Node> iterator = set.iterator();
        data.add(iterator.next());

        while (iterator.hasNext()) {
            Node temp = iterator.next();
            if (data.getLast().back != temp.back || data.getLast().type != temp.type) data.add(temp);
        }
    }

    public void save(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(data.size());
        for (Node element : data) element.save(dataOutputStream);
    }

    public Node get(long date) {
        int begin = 0, end = data.size(), index = 0;
        Node buffer = null;

        while (begin < end) {
            index = begin + ((end - begin) >> 1);
            buffer = data.get(index);

            if (date < buffer.date) end = index;
            else if (date > buffer.date) begin = index + 1;
            else return buffer;
        } return ((buffer.date > date) ? ((index == 0) ? null : data.get(index - 1)) : buffer);
    }

    public int _isOnline(long date) {
        int begin = 0, end = data.size(), index = 0;
        Node buffer = null;

        while (begin < end) {
            index = begin + ((end - begin) >> 1);
            buffer = data.get(index);

            if (date < buffer.date) end = index;
            else if (date > buffer.date) begin = index + 1;
            else return index;
        }

        if (buffer.date > date){
            if (index == 0) return -1;
            else --index;
        }

        while (index < data.size()) {
            if (data.get(index).date > date) break;
            ++index;
        } return index;
    }

    public Boolean isOnline(long date) {
        int index = _isOnline(date);
        if (index == -1) return null;
        return data.get(index).back == 0;
    }

    public Boolean isOnline(long one, long two) {
        int index = _isOnline(one);
        if (index == -1) return null;

        while (index < data.size()) {
            if (data.get(index).date > two) break;
            if (data.get(index).back == 0) return true;
            ++index;
        } return false;
    }
}
