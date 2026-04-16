package org.example.Algorithm;

import org.example.Colors;
import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.IOStream;
import org.example.Utils;
import org.example.VKData.UserDB;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class Chains {
    public static class Base extends Thread {
        public record Node(int id, int[] array) {}

        public class DB {
            public TreeSet<Integer> done = new TreeSet<>();
            public ArrayList<TreeMap<Integer, TreeSet<Integer>>> data = new ArrayList<>();
            public TreeSet<Integer> scanned = new TreeSet<>();

            public DB() {
                data.add(new TreeMap<>());
            }
        }

        public ArrayList<Integer> in;
        public ArrayList<Integer> to;
        public Node[][] data;
        public long date;

        public DB genNoDate() { return null; }
        public DB genDate() throws InterruptedException { return null; }

        protected Base() { }

        public Base(ArrayList<Integer> in, ArrayList<Integer> to, long date) throws InterruptedException {
            this.in = in;
            this.to = to;
            this.date = date;

            General.lock.lock1();
            DB data = ((date == 0) ? genNoDate() : genDate());
            General.lock.unlock1();

            if (!data.done.isEmpty()) {
                in = new ArrayList<>(data.done);
                generate(data.data);
            }
        }

        public ArrayList<Integer> getIds() {
            ArrayList<Integer> buffer = new ArrayList<>(in);
            for (Node[] array : data)
                for (Node element : array)
                    buffer.add(element.id);

            Collections.sort(buffer);
            return buffer;
        }

        public void generate(ArrayList<TreeMap<Integer, TreeSet<Integer>>> data) {
            while (true) {
                data.getLast().entrySet().removeIf(s -> !Utils.binSearch(to, s.getKey()));
                if (data.getLast().isEmpty()) data.removeLast();
                else break;
            }

            for (int index = data.size() - 2; index > -1; --index) {
                TreeMap<Integer, TreeSet<Integer>> back = data.get(index + 1);
                TreeMap<Integer, TreeSet<Integer>> db = data.get(index);
                TreeSet<Integer> set = new TreeSet<>();

                for (Map.Entry<Integer, TreeSet<Integer>> entry : back.entrySet())
                    set.addAll(entry.getValue());
                db.entrySet().removeIf(s -> !set.contains(s.getKey()));
            }

            for (int index = data.size() - 2; index > -1; --index) {
                TreeMap<Integer, TreeSet<Integer>> back = data.get(index + 1);
                TreeMap<Integer, TreeSet<Integer>> db = data.get(index);

                for (Map.Entry<Integer, TreeSet<Integer>> entry : back.entrySet())
                    entry.getValue().removeIf(s -> !db.containsKey(s) && !Utils.binSearch(in, entry.getKey()));
            } this.data = new Node[data.size()][];

            for (int index = data.size() - 2; index > -1; --index) {
                TreeMap<Integer, TreeSet<Integer>> back = data.get(index + 1);
                TreeMap<Integer, TreeSet<Integer>> db = data.get(index);
                ArrayList<Integer> temp = new ArrayList<>(db.keySet());
                this.data[index + 1] = new Node[back.size()];
                int ind = 0;

                for (Map.Entry<Integer, TreeSet<Integer>> entry : back.entrySet()) {
                    ArrayList<Integer> temp2 = new ArrayList<>();
                    for (int element : entry.getValue()) {
                        int result = Utils.binSearchIndexOf(temp, element);
                        if (result != -1) temp2.add(result);
                    } this.data[index + 1][ind++] = new Node(entry.getKey(), temp2.stream().mapToInt(Integer::intValue).toArray());
                }
            }

            TreeSet<Integer> temp = new TreeSet<>();
            for (Map.Entry<Integer, TreeSet<Integer>> entry : data.getFirst().entrySet())
                temp.addAll(entry.getValue());

            this.data[0] = new Node[data.getFirst().size()];
            in = new ArrayList<>(temp);
            int index = 0;

            for (Map.Entry<Integer, TreeSet<Integer>> entry : data.getFirst().entrySet()) {
                ArrayList<Integer> temp2 = new ArrayList<>();
                for (int element : entry.getValue()) {
                    int result = Utils.binSearchIndexOf(in, element);
                    if (result != -1) temp2.add(result);
                } this.data[0][index++] = new Node(entry.getKey(), temp2.stream().mapToInt(Integer::intValue).toArray());
            }
        }

        public void outConsole() throws InterruptedException {
            System.out.println("Chain in: ");
            General.lock.lock1();

            for (int index = 0; index < in.size(); ++index)
                System.out.println("\t" + Integer.toString(index) + "\t:" + Integer.toString(in.get(index)) + ':' + General.users.get(in.get(index)));

            for (int a = 0; a < data.length; ++a) {
                System.out.println(Colors.ANSI_BLUE + "Level: " + Integer.toString(a) + Colors.ANSI_RESET);
                for (int b = 0; b < data[a].length; ++b) {
                    System.out.println("\t" + ((Utils.binSearch(to, data[a][b].id)) ? Colors.ANSI_YELLOW : "") + Integer.toString(b) + Colors.ANSI_RESET + "\t:" + Integer.toString(data[a][b].id) + ':' + General.users.get(data[a][b].id));
                    System.out.println("\t" + ((Utils.binSearch(to, data[a][b].id)) ? Colors.ANSI_YELLOW : "") + Integer.toString(b) + Colors.ANSI_RESET + "\t:" + Colors.ANSI_RED + Utils.arrayToString(data[a][b].array) + Colors.ANSI_RESET);
                }
            } General.lock.unlock1();
        }

        public void outConsole(long date) throws InterruptedException {
            System.out.println("Chain in: ");
            General.lock.lock1();

            for (int index = 0; index < in.size(); ++index)
                System.out.println("\t" + Integer.toString(index) + "\t:" + Integer.toString(in.get(index)) + ':' + ((General.users.get(in.get(index)) != null) ? General.users.get(in.get(index)).toString(date) : General.users.get(in.get(index))));

            for (int a = 0; a < data.length; ++a) {
                System.out.println(Colors.ANSI_BLUE + "Level: " + Integer.toString(a) + Colors.ANSI_RESET);
                for (int b = 0; b < data[a].length; ++b) {
                    System.out.println("\t" + ((Utils.binSearch(to, data[a][b].id)) ? Colors.ANSI_YELLOW : "") + Integer.toString(b) + Colors.ANSI_RESET + "\t:" + Integer.toString(data[a][b].id) + ':' + ((General.users.get(in.get(data[a][b].id)) != null) ? General.users.get(in.get(data[a][b].id)).toString(date) : General.users.get(in.get(data[a][b].id))));
                    System.out.println("\t" + ((Utils.binSearch(to, data[a][b].id)) ? Colors.ANSI_YELLOW : "") + Integer.toString(b) + Colors.ANSI_RESET + "\t:" + Colors.ANSI_RED + Utils.arrayToString(data[a][b].array) + Colors.ANSI_RESET);
                }
            } General.lock.unlock1();
        }

        public Base(DataInputStream dataInputStream) throws IOException {
            this.in = IOStream.readIntArrayList(dataInputStream);
            this.to = IOStream.readIntArrayList(dataInputStream);
            this.data = new Node[dataInputStream.readInt()][];

            for (int a = 0; a < this.data.length; ++a) {
                int count = dataInputStream.readInt();
                for (int b = 0; b < count; ++b)
                    this.data[a][b] = new Node(dataInputStream.readInt(), IOStream.readIntArray(dataInputStream));
            }
        }

        public void out(DataOutputStream dataOutputStream) throws IOException {
            IOStream.writeIntArrayList(this.in, dataOutputStream);
            IOStream.writeIntArrayList(this.to, dataOutputStream);

            dataOutputStream.writeInt(this.data.length);
            for (Node[] nodes : this.data) {
                dataOutputStream.writeInt(nodes.length);
                for (Node element : nodes) {
                    dataOutputStream.writeInt(element.id());
                    IOStream.writeIntArray(element.array(), dataOutputStream);
                }
            }
        }

        public void outStream(DataOutputStream dataOutputStream) throws IOException {
            IOStream.writeIntArrayList(this.in, dataOutputStream);
            IOStream.writeIntArrayList(this.to, dataOutputStream);

            for (int id : this.in) {
                UserDB userDB = General.users.get(id);
                if (userDB != null) {
                    dataOutputStream.writeBoolean(true);
                    if (this.date == 0) userDB.out(dataOutputStream);
                    else userDB.out(dataOutputStream, this.date);
                } else dataOutputStream.writeBoolean(false);
            }

            dataOutputStream.writeInt(this.data.length);
            for (Node[] element : this.data) {
                dataOutputStream.writeInt(element.length);
                for (Node user : element) {
                    dataOutputStream.writeInt(user.id());
                    UserDB userDB = General.users.get(user.id());

                    if (userDB != null) {
                        dataOutputStream.writeBoolean(true);
                        if (this.date == 0) userDB.out(dataOutputStream);
                        else userDB.out(dataOutputStream, this.date);
                    } else dataOutputStream.writeBoolean(false);
                    IOStream.writeIntArray(user.array(), dataOutputStream);
                }
            }
        }

        public static void outConsole(DataInputStream dataInputStream) throws IOException {
            int[] in = IOStream.readIntArray(dataInputStream);
            int[] to = IOStream.readIntArray(dataInputStream);

            System.out.println("in users");
            for (int index = 0; index < in.length; ++index) {
                System.out.print(index + "\t");
                if (dataInputStream.readBoolean()) System.out.print(UserDB.toString(dataInputStream));
                System.out.println();
            }

            int countArrays = dataInputStream.readInt();
            for (int a = 0; a < countArrays; ++a) {
                System.out.println(Colors.ANSI_CYAN + "level: " + (a + 1) + Colors.ANSI_RESET);
                int count = dataInputStream.readInt();
                for (int b = 0; b < count; ++b) {
                    int id = dataInputStream.readInt();

                    System.out.print(((Arrays.binarySearch(to, id) > -1) ? Colors.ANSI_BLUE : "") + b + "\t" + Colors.ANSI_RESET);
                    if (dataInputStream.readBoolean()) System.out.print(UserDB.toString(dataInputStream));
                    System.out.print("\t" + ((Arrays.binarySearch(to, id) > -1) ? Colors.ANSI_BLUE : "") + b + "\t" + Colors.ANSI_RESET);
                    Utils.outConsole(IOStream.readIntArray(dataInputStream));
                }
            }
        }
    }

    public static class ChainGenerate extends Base {
        @Override
        public DB genNoDate() {
            DB buffer = new DB();
            for (int element : in) {
                TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(element);
                if (temp == null) continue;

                for (int id : temp) {
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(entry.getKey());
                    if (temp == null) continue;

                    for (int element : temp) {
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        @Override
        public DB genDate() throws InterruptedException {
            DB buffer = new DB();
            for (int element : in) {
                TreeSet<Integer> temp = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                if (temp == null) continue;

                for (int id : temp) {
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    TreeSet<Integer> temp = Generate.getGenerateUserIds(entry.getKey(), GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                    if (temp == null) continue;

                    for (int element : temp) {
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        public ChainGenerate(ArrayList<Integer> in, ArrayList<Integer> to, long date) throws InterruptedException {
            super(in, to, date);
        }
    }

    public static class Chain extends Base {
        @Override
        public DB genNoDate() {
            DB buffer = new DB();
            for (int element : in) {
                UserDB userDB = General.users.get(element);
                if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                for (int id : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data) {
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    UserDB userDB = General.users.get(entry.getKey());
                    if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                    for (int element : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data) {
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        @Override
        public DB genDate() {
            DB buffer = new DB();
            for (int element : in) {
                UserDB userDB = General.users.get(element);
                if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                TreeSet<Integer> set = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                if (set == null) continue;

                for (int id : set) {
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    UserDB userDB = General.users.get(entry.getKey());
                    if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                    TreeSet<Integer> set = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                    if (set == null) continue;

                    for (int element : set) {
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        public Chain(ArrayList<Integer> in, ArrayList<Integer> to, long date) throws InterruptedException {
            super(in, to, date);
        }
    }

    public static class ChainUsersBase extends Base {
        public ArrayList<Integer> users;

        public DB genNoDate(ArrayList<Integer> data) { return null; }
        public DB genDate(ArrayList<Integer> data) throws InterruptedException { return null; }

        public ChainUsersBase(ArrayList<Integer> in, ArrayList<Integer> to, long date, ArrayList<Integer> data) throws InterruptedException {
            this.in = in;
            this.to = to;
            this.users = data;
        }

        @Override
        public void run() {
            try {
                General.lock.lock1();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            DB buffer = null;
            try {
                buffer = ((date == 0) ? genNoDate(users) : genDate(users));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            General.lock.unlock1();
            if (!buffer.done.isEmpty()) generate(buffer.data);
            this.users = null;
        }
    }

    public static class ChainGenerateUsers extends ChainUsersBase {
        public DB genNoDate(ArrayList<Integer> data) {
            DB buffer = new DB();
            for (int element : in) {
                TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(element);
                if (temp == null) continue;

                for (int id : temp) {
                    if (!Utils.binSearch(data, id)) continue;
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(entry.getKey());
                    if (temp == null) continue;

                    for (int element : temp) {
                        if (!Utils.binSearch(data, element)) continue;
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        @Override
        public DB genDate(ArrayList<Integer> data) throws InterruptedException {
            DB buffer = new DB();
            for (int element : in) {
                TreeSet<Integer> temp = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                if (temp == null) continue;

                for (int id : temp) {
                    if (!Utils.binSearch(data, id)) continue;
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    TreeSet<Integer> temp = Generate.getGenerateUserIds(entry.getKey(), GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                    if (temp == null) continue;

                    for (int element : temp) {
                        if (!Utils.binSearch(data, element)) continue;
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        public ChainGenerateUsers (ArrayList<Integer> in, ArrayList<Integer> to, long date, ArrayList<Integer> data) throws InterruptedException {
            super(in, to, date, data);
        }
    }

    public static class ChainUsers extends ChainUsersBase {
        public DB genNoDate(ArrayList<Integer> data) {
            DB buffer = new DB();
            for (int element : in) {
                UserDB userDB = General.users.get(element);
                if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                for (int id : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data) {
                    if (!Utils.binSearch(data, id)) continue;
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    UserDB userDB = General.users.get(entry.getKey());
                    if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                    for (int element : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data) {
                        if (!Utils.binSearch(data, element)) continue;
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        public DB genDate(ArrayList<Integer> data) {
            DB buffer = new DB();
            for (int element : in) {
                UserDB userDB = General.users.get(element);
                if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                TreeSet<Integer> set = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                if (set == null) continue;

                for (int id : set) {
                    if (!Utils.binSearch(data, id)) continue;
                    buffer.data.getFirst().computeIfAbsent(id, s -> new TreeSet<>()).add(element);
                    if (Utils.binSearch(to, id)) buffer.done.add(id);
                    buffer.scanned.add(id);
                }
            }

            while (true) {
                TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();
                for (Map.Entry<Integer, TreeSet<Integer>> entry : buffer.data.getLast().entrySet()) {
                    UserDB userDB = General.users.get(entry.getKey());
                    if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                    TreeSet<Integer> set = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                    if (set == null) continue;

                    for (int element : set) {
                        if (!Utils.binSearch(data, element)) continue;
                        if (!buffer.scanned.add(element)) continue;
                        map.computeIfAbsent(element, s -> new TreeSet<>()).add(entry.getKey());
                        if (Utils.binSearch(to, element)) buffer.done.add(element);
                    }
                }

                if (!map.isEmpty()) buffer.data.add(map);
                else break;
                if (buffer.done.size() == to.size()) break;
            } return buffer;
        }

        public ChainUsers(ArrayList<Integer> in, ArrayList<Integer> to, long date, ArrayList<Integer> data) throws InterruptedException {
            super(in, to, date, data);
        }
    }
}
