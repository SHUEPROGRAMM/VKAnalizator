package org.example.Data;

import org.example.Console.Input;
import org.example.Remove;

import javax.print.attribute.standard.DateTimeAtCompleted;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class IDHistory {
    public static class Node implements Comparable<Node> {
        public final long date;
        public int id;

        public Node(long date, int id) {
            this.date = date;
            this.id = id;
        }

        public Node(DataInputStream dataInputStream) throws IOException {
            date = dataInputStream.readLong();
            id = dataInputStream.readInt();
        }

        public void save(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeLong(date);
            dataOutputStream.writeInt(id);
        }

        @Override
        public int compareTo(Node o) {
            return Long.compare(this.date, o.date);
        }
    }

    public ArrayList<Node> data = new ArrayList<>();

    public boolean update(long date, int id) {
        if (data.isEmpty()) {
            data.add(new Node(date, id));
            return true;
        } else if (data.getLast().id != id) {
            data.add(new Node(date, id));
            return true;
        } else return false;
    }

    public static TreeSet<Node> loadNodes(DataInputStream dataInputStream, int count) throws IOException {
        TreeSet<Node> buffer = new TreeSet<>();
        for (int index = 0; index < count; ++index)
            buffer.add(new Node(dataInputStream));
        return buffer;
    }

    public static TreeSet<Node> loadNodes(DataInputStream dataInputStream, int count, int[] array) throws IOException {
        TreeSet<Node> buffer = new TreeSet<>();
        for (int index = 0; index < count; ++index)
            buffer.add(new Node(dataInputStream.readLong(), array[dataInputStream.readInt()]));
        return buffer;
    }

    public void load(DataInputStream dataInputStream, int count) throws IOException {
        load(loadNodes(dataInputStream, count));
    }

    public void load(DataInputStream dataInputStream, int count, int[] array) throws IOException {
        load(loadNodes(dataInputStream, count, array));
    }

    public void load(TreeSet<Node> set) {
        if (!data.isEmpty()) {
            set.addAll(data);
            data.clear();
        }

        Iterator<Node> iterator = set.iterator();
        data.add(iterator.next());

        while(iterator.hasNext()) {
            Node temp = iterator.next();
            if (temp.id != data.getLast().id)
                data.add(temp);
        }
    }

    public void save(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(data.size());
        for (Node element : data)
            element.save(dataOutputStream);
    }

    public TreeSet<Integer> all() {
        TreeSet<Integer> buffer = new TreeSet<>();
        for (Node element : data)
            buffer.add(element.id);
        return buffer;
    }

    public int _get(long date) {
        int begin = 0, end = data.size();
        Node buffer = null;
        int index = 0;

        while (begin < end) {
            index = begin + ((end - begin) >> 1);
            buffer = data.get((index));

            if (date < buffer.date) end = index;
            else if (date > buffer.date) begin = index + 1;
            else return index;
        }
        return ((buffer.date < date) ? ((index == 0) ? -1 : index - 1) : index);
    }

    public Node get(long date) {
        int index = _get(date);
        return (index != -1) ? data.get(index) : null;
    }

    public void build(Remove.Node[] remove) {
        for (Node element : data)
            element.id = Remove.search(element.id, remove);
    }

    public boolean contain(int id, long one, long two) {
        int index = _get(one);
        if (index == -1) return false;

        while (index < data.size()) {
            if (data.get(index).date > two) break;
            if (data.get(index).id == id) return true;
            ++index;
        } return false;
    }
}
