package org.example.Data;

import org.example.Console.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class IDsHistory {
    public static class Node{
        final public long date;
        final public int[] data;

        public Node(long date, int[] data) {
            this.date = date;
            this.data = data;
        }

        public Node(DataInputStream dataInputStream) throws IOException {
            date = dataInputStream.readLong();
            int count = dataInputStream.readInt();
            if (count != 0) {
                data = new int[count];
                for (int index = 0; index < count; ++index)
                    data[index] = dataInputStream.readInt();
            } else data = null;
        }

        public void save(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeLong(date);
            if (data != null) {
                dataOutputStream.writeInt(data.length);
                for (int id : data)
                    dataOutputStream.writeInt(id);
            } else dataOutputStream.writeInt(0);
        }
    }

    public static class LoadNodes {
        public final TreeMap<Long, TreeSet<Integer>> added;
        public final TreeMap<Long, TreeSet<Integer>> deleted;

        public LoadNodes(TreeMap<Long, TreeSet<Integer>> added, TreeMap<Long, TreeSet<Integer>> deleted) {
            this.added = added;
            this.deleted = deleted;
        }

        private TreeMap<Long, TreeSet<Integer>> loadNodes(DataInputStream dataInputStream, int count) throws IOException {
            TreeMap<Long, TreeSet<Integer>> buffer = new TreeMap<>();
            for (int index = 0; index < count; ++index) {
                TreeSet<Integer> set = buffer.computeIfAbsent(dataInputStream.readLong(), s -> new TreeSet<>());
                int size = dataInputStream.readInt();

                for (int a = 0; a < size; ++a)
                    set.add(dataInputStream.readInt());
            } return buffer;
        }

        public LoadNodes(DataInputStream dataInputStream, int count) throws IOException {
            added = loadNodes(dataInputStream, count);
            deleted = loadNodes(dataInputStream, dataInputStream.readInt());
        }
    }

    public Node last = null;
    public ArrayList<Node> added = new ArrayList<>();
    public ArrayList<Node> deleted = null;

    public boolean update(long date, int[] data) {
        if (last == null) {
            last = new Node(date, data);
            added = new ArrayList<>();
            added.add(last);
            return true;
        } else {
            if (last.data == null) {
                if (data == null) return false;
                last = new Node(date, data);
                added.add(last);
                return true;
            } else if (data == null) {
                if (deleted == null) deleted = new ArrayList<>();
                deleted.add(new Node(date, last.data));
                last = new Node(date, null);
                return true;
            } else {
                int[] buffer = new int[Math.max(last.data.length, data.length)];
                boolean update = false;
                int index = 0;

                for (int element : data)
                    if (Arrays.binarySearch(last.data, element) > -1)
                        buffer[index++] = element;

                if (index != 0) {
                    if (index == buffer.length) {
                        added.add(new Node(date, buffer));
                        buffer = new int[last.data.length];
                    } else added.add(new Node(date, Arrays.copyOf(buffer, index)));

                    update = true;
                    index = 0;
                }

                for (int element : last.data)
                    if (Arrays.binarySearch(data, element) > -1)
                        buffer[index++] = element;

                if (index != 0) {
                    if (deleted == null) deleted = new ArrayList<>();
                    deleted.add(new Node(date, (index == buffer.length) ? buffer : Arrays.copyOf(buffer, index)));
                    update = true;
                }

                if (update) {
                    last = new Node(date, data);
                    return true;
                } return false;
            }
        }
    }

    private ArrayList<Node> loadNodes(DataInputStream dataInputStream, int size) throws IOException {
        if (size == 0) return null;
        ArrayList<Node> buffer = new ArrayList<>(size);

        for (int index = 0; index < size; ++index)
            buffer.add(new Node(dataInputStream));
        return buffer;
    }

    private void loadOne(DataInputStream dataInputStream, int count) throws IOException {
        added = loadNodes(dataInputStream, count);
        deleted = loadNodes(dataInputStream, dataInputStream.readInt());
        TreeSet<Integer> buffer = new TreeSet<>();

        if (deleted == null) {
            for (Node element : added)
                if (element.data != null)
                    for (int id : element.data)
                        buffer.add(id);
        } else {
            Iterator<Node> addedIterator = added.iterator();
            Iterator<Node> deletedIterator = deleted.iterator();
            Node addedNode = addedIterator.next();
            Node deletedNode = deletedIterator.next();

            while (addedNode != null && deletedNode != null) {
                if (addedNode.date > deletedNode.date) {
                    if (addedNode.data != null)
                        for (int element : addedNode.data)
                            buffer.add(element);
                    addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
                } else if (addedNode.date < deletedNode.date) {
                    for (int element : deletedNode.data)
                        buffer.remove(element);
                    deletedNode = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                } else {
                    if (addedNode.data != null)
                        for (int element : addedNode.data)
                            buffer.add(element);

                    for (int element : deletedNode.data)
                        buffer.remove(element);

                    addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
                    deletedNode = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                }
            }

            while (addedNode != null) {
                if (addedNode.data != null)
                    for (int element : addedNode.data)
                        buffer.add(element);
                addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
            }

            while (deletedNode != null) {
                for (int element : deletedNode.data)
                    buffer.remove(element);
                deletedNode = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
            }
        }

        if ((added.getLast().data == null && buffer.isEmpty()) || (added.getLast().data != null && added.getLast().data.length == buffer.size()))
            last = added.getLast();
        else last = new Node((deleted != null && deleted.getLast().date > added.getLast().date) ? deleted.getLast().date : added.getLast().date , buffer.stream().mapToInt(Integer::intValue).toArray());
    }

    private TreeMap<Long, TreeSet<Integer>> loadNodeMaps(DataInputStream dataInputStream, int count) throws IOException {
        TreeMap<Long, TreeSet<Integer>> buffer = new TreeMap<>();
        for (int index = 0; index < count; ++index) {
            TreeSet<Integer> arrayList = buffer.computeIfAbsent(dataInputStream.readLong(), s -> new TreeSet<>());
            int size = dataInputStream.readInt();

            for (int a = 0; a < size; ++a)
                arrayList.add(dataInputStream.readInt());
        } return buffer;
    }

    private void addToMap(TreeMap<Long, TreeSet<Integer>> map, ArrayList<Node> arrayList) {
        for (Node element : arrayList) {
            TreeSet<Integer> set = map.computeIfAbsent(element.date, s -> new TreeSet<>());
            if (element.data != null)
                for (int elem : element.data)
                    set.add(elem);
        }
    }

    public void load(LoadNodes loadNodes) {
        addToMap(loadNodes.added, added);
        added.clear();

        if (deleted != null) {
            addToMap(loadNodes.deleted, deleted);
            deleted.clear();
        } else deleted = new ArrayList<>();

        Iterator<Map.Entry<Long, TreeSet<Integer>>> addedIterator = loadNodes.added.entrySet().iterator();
        Iterator<Map.Entry<Long, TreeSet<Integer>>> deletedIterator = loadNodes.deleted.entrySet().iterator();
        Map.Entry<Long, TreeSet<Integer>> addedEntry = addedIterator.next();
        Map.Entry<Long, TreeSet<Integer>> deletedEntry = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
        TreeSet<Integer> buffer = new TreeSet<>();
        int[] array = null;
        int index;

        if (addedEntry.getValue().isEmpty()) {
            added.add(new Node(addedEntry.getKey(), null));
            addedEntry = (addedIterator.hasNext()) ? addedIterator.next() : null;
        }

        while (addedEntry != null && deletedEntry != null) {
            if (addedEntry.getKey() > deletedEntry.getKey()) {
                if (addedEntry.getValue().isEmpty()) {
                    if (!buffer.isEmpty()) {
                        deleted.add(new Node(addedEntry.getKey(), buffer.stream().mapToInt(Integer::intValue).toArray()));
                        buffer.clear();
                    }
                } else {
                    array = new int[addedEntry.getValue().size()];
                    index = 0;

                    for (int element : addedEntry.getValue())
                        if (buffer.add(element))
                            array[index++] = element;
                    if (index != 0) added.add(new Node(addedEntry.getKey(), (index == array.length) ? array : Arrays.copyOf(array, index)));
                } addedEntry = (addedIterator.hasNext()) ? addedIterator.next() : null;
            } else if (addedEntry.getKey() < deletedEntry.getKey()) {
                array = new int[deletedEntry.getValue().size()];
                index = 0;

                for (int element : deletedEntry.getValue())
                    if (buffer.remove(element))
                        array[index++] = element;

                if (index != 0) deleted.add(new Node(deletedEntry.getKey(), (index == array.length) ? array : Arrays.copyOf(array, index)));
                deletedEntry = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
            } else {
                if (addedEntry.getValue().isEmpty()) {
                    if (!buffer.isEmpty()) {
                        deleted.add(new Node(addedEntry.getKey(), buffer.stream().mapToInt(Integer::intValue).toArray()));
                        buffer.clear();
                    }
                } else {
                    array = new int[addedEntry.getValue().size()];
                    index = 0;

                    for (int element : addedEntry.getValue())
                        if (buffer.add(element))
                            array[index++] = element;
                    if (index != 0) added.add(new Node(addedEntry.getKey(), (index == array.length) ? array : Arrays.copyOf(array, index)));

                    array = new int[deletedEntry.getValue().size()];
                    index = 0;

                    for (int element : deletedEntry.getValue())
                        if (buffer.remove(element))
                            array[index++] = element;
                    if (index != 0) deleted.add(new Node(deletedEntry.getKey(), (index == array.length) ? array : Arrays.copyOf(array, index)));
                }

                addedEntry = (addedIterator.hasNext()) ? addedIterator.next() : null;
                deletedEntry = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
            }
        }

        while (addedEntry != null) {
            if (addedEntry.getValue().isEmpty()) {
                if (!buffer.isEmpty()) {
                    deleted.add(new Node(addedEntry.getKey(), buffer.stream().mapToInt(Integer::intValue).toArray()));
                    buffer.clear();
                }
            } else {
                array = new int[addedEntry.getValue().size()];
                index = 0;

                for (int element : addedEntry.getValue())
                    if (buffer.add(element))
                        array[index++] = element;
                if (index != 0) added.add(new Node(addedEntry.getKey(), (index == array.length) ? array : Arrays.copyOf(array, index)));
            } addedEntry = (addedIterator.hasNext()) ? addedIterator.next() : null;
        }

        while (deletedEntry != null) {
            array = new int[deletedEntry.getValue().size()];
            index = 0;

            for (int element : deletedEntry.getValue())
                if (buffer.remove(element))
                    array[index++] = element;

            if (index != 0) deleted.add(new Node(deletedEntry.getKey(), (index == array.length) ? array : Arrays.copyOf(array, index)));
            deletedEntry = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
        }

        if (deleted.isEmpty() || (!deleted.isEmpty() && deleted.getLast().date < added.getLast().date))
            last = added.getLast();
        else last = new Node(
                (deleted.isEmpty() || (!deleted.isEmpty() && deleted.getLast().date < added.getLast().date)) ? added.getLast().date : deleted.getLast().date
                , buffer.stream().mapToInt(Integer::intValue).toArray()
        );
        if (deleted.isEmpty()) deleted = null;
    }

    public void load(DataInputStream dataInputStream, int count) throws IOException {
        if (last != null) load(new LoadNodes(dataInputStream, count));
        else loadOne(dataInputStream, count);
    }

    private void save(ArrayList<Node> nodes, DataOutputStream dataOutputStream) throws IOException {
        if (nodes != null) {
            dataOutputStream.writeInt(nodes.size());
            for (Node element : nodes)
                element.save(dataOutputStream);
        } else dataOutputStream.writeInt(0);
    }

    public void save(DataOutputStream dataOutputStream) throws IOException {
        save(added, dataOutputStream);
        save(deleted, dataOutputStream);
    }

    public TreeSet<Integer> get(long date) {
        if (added.getFirst().date > date)
            return null;

        TreeSet<Integer> buffer = new TreeSet<>();
        if (deleted == null) {
            for (Node element : added)
                if (element.data != null)
                    for (int id : element.data)
                        buffer.add(id);
        } else {
            Iterator<Node> addedIterator = added.iterator();
            Iterator<Node> deletedIterator = deleted.iterator();
            Node addedNode = addedIterator.next();
            Node deletedNode = deletedIterator.next();

            while (addedNode != null && deletedNode != null) {
                if (addedNode.date > deletedNode.date) {
                    if (addedNode.data != null)
                        for (int id : addedNode.data)
                            buffer.add(id);
                    addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
                } else if (addedNode.date < deletedNode.date) {
                    for (int id : deletedNode.data)
                        buffer.remove(id);
                    deletedNode = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                } else {
                    if (addedNode.data != null)
                        for (int id : addedNode.data)
                            buffer.add(id);

                    for (int id : deletedNode.data)
                        buffer.remove(id);

                    addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
                    deletedNode = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                }
            }

            while (addedNode != null) {
                if (addedNode.data != null)
                    for (int id : addedNode.data)
                        buffer.add(id);
                addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
            }

            while (deletedNode != null) {
                for (int id : deletedNode.data)
                    buffer.remove(id);
                deletedNode = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
            }
        } return buffer;
    }

    private boolean containNode(ArrayList<Node> nodes, int id) {
        if (nodes == null) return false;
        for (Node element : nodes)
            if (element.data != null && Arrays.binarySearch(element.data, id) > -1)
                return true;
        return false;
    }

    public boolean containAdded(int id) { return containNode(added, id); }
    public boolean containDeleted(int id) { return containNode(deleted, id); }

    public int size(long date) {
        if (last.date <= date) return ((last.data == null) ? 0 : last.data.length);
        if (added.getFirst().date > date) return -1;
        int buffer = 0;

        if (deleted == null) {
            for (Node element : added) {
                if (element.date > date) return buffer;
                if (element.data == null) continue;
                buffer += element.data.length;
            }
        } else {
            Iterator<Node> addedIterator = added.iterator();
            Iterator<Node> deletedIterator = deleted.iterator();
            Node addedNode = addedIterator.next();
            Node deletedNode = deletedIterator.next();

            if (addedNode.data == null) addedNode = ((addedIterator.hasNext()) ? addedIterator.next() : null);
            while (addedNode != null && deletedNode != null) {
                if (addedNode.date < deletedNode.date) {
                    if (addedNode.date > date) return buffer;
                    buffer += addedNode.data.length;
                    addedNode = ((addedIterator.hasNext()) ? addedIterator.next() : null);
                } else if (addedNode.date > deletedNode.date) {
                    if (deletedNode.date > date) return buffer;
                    buffer -= deletedNode.data.length;
                    deletedNode = ((deletedIterator.hasNext()) ? deletedIterator.next() : null);
                } else {
                    if (addedNode.date > date) return buffer;
                    buffer += addedNode.data.length;
                    buffer -= deletedNode.data.length;

                    addedNode = ((addedIterator.hasNext()) ? addedIterator.next() : null);
                    deletedNode = ((deletedIterator.hasNext()) ? deletedIterator.next() : null);
                }
            }

            while (addedNode != null) {
                if (addedNode.date > date) return buffer;
                buffer += addedNode.data.length;
                addedNode = ((addedIterator.hasNext()) ? addedIterator.next() : null);
            }

            while (deletedNode != null) {
                if (deletedNode.date > date) return buffer;
                buffer -= deletedNode.data.length;
                deletedNode = ((deletedIterator.hasNext()) ? deletedIterator.next() : null);
            }
        } return buffer;
    }

    public TreeSet<Integer> all() {
        TreeSet<Integer> buffer = new TreeSet<>();
        for (Node element : added)
            if (element.data != null)
                for (int id : element.data)
                    buffer.add(id);
        return buffer;
    }

    public boolean contain(int id, long date) {
        if (last.date >= date) return (last.data != null && Arrays.binarySearch(last.data, id) > -1);
        if (added.getFirst().date < date) return false;

        if (deleted == null) {
            for (Node element : added) {
                if (element.date > date) return false;
                if (element.data == null) continue;
                if (Arrays.binarySearch(element.data, id) > -1) return true;
            } return false;
        } else {
            boolean buffer = false;
            Iterator<Node> addedIterator = added.iterator();
            Iterator<Node> deletedIterator = deleted.iterator();
            Node added = addedIterator.next();
            Node deleted = deletedIterator.next();

            while (added != null && deleted != null) {
                if (added.date < deleted.date) {
                    if (added.date > date) return buffer;
                    if (added.data != null && Arrays.binarySearch(added.data, id) > -1) buffer = true;
                    added = (addedIterator.hasNext()) ? addedIterator.next() : null;
                } else if (added.date > deleted.date) {
                    if (deleted.date > date) return buffer;
                    if (Arrays.binarySearch(deleted.data, id) > -1) buffer = false;
                    deleted = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                } else {
                    if (added.date > date) return buffer;
                    if (added.data != null && Arrays.binarySearch(added.data, id) > -1) buffer = true;
                    if (Arrays.binarySearch(deleted.data, id) > -1) buffer = false;

                    added = (addedIterator.hasNext()) ? addedIterator.next() : null;
                    deleted = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                }
            }

            while (added != null) {
                if (added.date > date) return buffer;
                if (added.data != null && Arrays.binarySearch(added.data, id) > -1) buffer = true;
                added = (addedIterator.hasNext()) ? addedIterator.next() : null;
            }

            while (deleted != null) {
                if (deleted.date > date) return buffer;
                if (Arrays.binarySearch(deleted.data, id) > -1) buffer = false;
                deleted = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
            } return buffer;
        }
    }

    public boolean contain(int id, long one, long two) {
        if (last.date >= one) return (last.data != null && Arrays.binarySearch(last.data, id) > -1);
        if (added.getFirst().date < one) return false;

        boolean buffer = false;
        if (deleted == null) {
            for (Node element : added) {
                if (element.date > two) return buffer;
                if (element.data != null && Arrays.binarySearch(element.data, id) > -1) buffer = true;
                if (buffer && one < element.date && element.date < two) return true;
            }
        } else {
            Iterator<Node> addedIterator = added.iterator();
            Iterator<Node> deletedIterator = deleted.iterator();
            Node added = addedIterator.next();
            Node deleted = deletedIterator.next();

            while (added != null && deleted != null) {
                if (added.date < deleted.date) {
                    if (added.date > two) return buffer;
                    if (added.data != null && Arrays.binarySearch(added.data, id) > -1) buffer = true;
                    if (buffer && one < added.date && added.date < two) return true;
                    added = (addedIterator.hasNext()) ? addedIterator.next() : null;
                } else if (added.date > deleted.date) {
                    if (deleted.date > two) return buffer;
                    if (Arrays.binarySearch(deleted.data, id) > -1 && deleted.date < one) buffer = false;
                    deleted = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                } else {
                    if (added.date > two) return buffer;
                    if (added.data != null && Arrays.binarySearch(added.data, id) > -1) buffer = true;
                    if (buffer && one < added.date && added.date < two) return true;
                    if (Arrays.binarySearch(deleted.data, id) > -1 && deleted.date < one) buffer = false;

                    added = (addedIterator.hasNext()) ? addedIterator.next() : null;
                    deleted = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
                }
            }

            while (added != null) {
                if (added.date > two) return buffer;
                if (added.data != null && Arrays.binarySearch(added.data, id) > -1) buffer = true;
                if (buffer && one < added.date && added.date < two) return true;
                added = (addedIterator.hasNext()) ? addedIterator.next() : null;
            }

            while (deleted != null) {
                if (deleted.date > two) return buffer;
                if (Arrays.binarySearch(deleted.data, id) > -1 && deleted.date < one) buffer = false;
                deleted = (deletedIterator.hasNext()) ? deletedIterator.next() : null;
            }
        } return buffer;
    }

    public TreeSet<Integer> all(long one, long two) {
        TreeSet<Integer> buffer = new TreeSet<>();
        if (deleted == null) {
            for (Node element : added) {
                if (element.date > two) break;
                if (element.data != null)
                    for (int id : element.data)
                        buffer.add(id);
            }
        } else {
            Iterator<Node> addedIterator = added.iterator();
            Iterator<Node> deletedIterator = deleted.iterator();
            Node addedNode = addedIterator.next();
            Node deletedNode = deletedIterator.next();
            if (addedNode.data == null) addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;

            while (addedNode != null && deletedNode != null) {
                if (addedNode.date < deletedNode.date) {
                    if (addedNode.date > two) break;
                    for (int element : addedNode.data)
                        buffer.add(element);
                    addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
                } else if (addedNode.date > deletedNode.date) {
                    if (deletedNode.date > two) break;
                    if (deletedNode.date < one)
                        for (int element : deletedNode.data)
                            buffer.remove(element);
                    deletedNode = ((deletedIterator.hasNext()) ? deletedIterator.next() : null);
                } else {
                    if (addedNode.date > two) break;
                    for (int element : addedNode.data)
                        buffer.add(element);
                    if (deletedNode.date < one)
                        for (int element : deletedNode.data)
                            buffer.remove(element);
                    addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
                    deletedNode = ((deletedIterator.hasNext()) ? deletedIterator.next() : null);
                }
            }

            while (addedNode != null) {
                if (addedNode.date > two) break;
                for (int element : addedNode.data)
                    buffer.add(element);
                addedNode = (addedIterator.hasNext()) ? addedIterator.next() : null;
            }

            while (deletedNode != null) {
                if (deletedNode.date > two) break;
                if (deletedNode.date < one)
                    for (int element : deletedNode.data)
                        buffer.remove(element);
                deletedNode = ((deletedIterator.hasNext()) ? deletedIterator.next() : null);
            }
        } return buffer;
    }
}
