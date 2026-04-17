package org.example.Networks;

import org.example.Colors;
import org.example.Console.Input;
import org.example.Networks.Client.Client;
import org.example.Commands.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Data {
    public static class DB {
        public static class Node {
            public final int type;
            public final long date;
            public final ArrayList<Integer> data;

            public Node(int type, long date, ArrayList<Integer> data) {
                this.type = type;
                this.date = date;
                this.data = data;
            }

            public Node(DataInputStream dataInputStream) throws IOException {
                type = dataInputStream.readInt();
                date = dataInputStream.readLong();
                int count = dataInputStream.readInt();
                data = new ArrayList<>(count);

                for (int a = 0; a < count; ++a)
                    data.add(dataInputStream.readInt());
            }

            public int getType() {
                return switch (type) {
                    case 0, 1, 2 -> 0;
                    default -> 1;
                };
            }

            public void out(DataOutputStream dataOutputStream) throws IOException {
                dataOutputStream.writeInt(type);
                dataOutputStream.writeLong(date);
                dataOutputStream.writeInt(data.size());

                for (int element : data)
                    dataOutputStream.writeInt(element);
            }
        }

        public String name;
        public Node data;
        public boolean newValue;
        public int id;
        public boolean error = false;

        public DB(Input input, Client.Data data) {
            if (input.strings.size() - input.index < 1) {
                error = true;
                return;
            }

            name = input.strings.get(input.index);
            ++input.index;

            int type = switch (name) {
                case "users" -> 0;
                case "friends" -> 1;
                case "friendsGenerate" -> 2;
                case "groups" -> 3;
                case "groupsById" -> 4;
                case "groupsByIdGenerate" -> 5;
                default -> -1;
            }; newValue = type != -1;

            if (newValue) {
                if (input.strings.size() - input.index < 1) {
                    error = true;
                    return;
                }

                Long date = input.getTime();
                if (date == null) {
                    error = true;
                    return;
                }

                ArrayList<Integer> temp = input.getIntegers();
                if (temp == null) {
                    error = true;
                    return;
                }
                this.data = new Node(type, date, temp);
            } else {
                Client.IdDB db = data.map.get(name);
                this.id = ((db == null) ? data.index : db.index());
            }
        }

        public DB(DataInputStream dataInputStream) throws IOException {
            newValue = dataInputStream.readBoolean();
            if (newValue) this.data = new Node(dataInputStream);
            else this.id = dataInputStream.readInt();
        }

        public Integer getType(Client.Data data) {
            if (newValue) {
                return this.data.getType();
            } else {
                if (data.index == id) return null;
                return data.map.get(data.mapBack.get(name)).type();
            }
        }

        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(newValue);
            if (newValue) this.data.out(dataOutputStream);
            else dataOutputStream.writeInt(this.id);
        }
    }



    public DB main;
    public DB to;
    public ArrayList<DB> alignDB;
    public boolean align = false;
    public boolean error = false;
    public ArrayList<Command> commands;

    public Data(Input input, Client.Data data) {
        switch (input.strings.get(input.index)) {
            case "out", "filter", "count", "remove", "general", "scan", "chain" -> {
                System.out.println(Colors.ANSI_RED + "Error command value" + Colors.ANSI_RESET);
                System.out.println("error 228");
                error = true;
                return;
            }default -> {}
        }
        main = new DB(input, data);

        switch (input.strings.get(input.index)) {
            case "=" -> {
                if (input.strings.size() - input.index < 2) {
                    if (main.newValue) {
                        error = true;
                        return;
                    }
                    ++input.index;

                    switch (input.strings.get(input.index)) {
                        case "out", "count", "filter", "remove", "chain", "general", "scan" -> {
                            to = main;
                            input.index -= 2;
                        }
                        default -> {
                            to = new DB(input, data);
                            if (to.error || (!to.newValue && data.index == to.id)) {
                                error = true;
                                return;
                            }
                        }
                    }
                } else {
                    System.out.println(Colors.ANSI_RED + "Error not = arguments" + Colors.ANSI_RESET);
                    error = true;
                    return;
                }
            }
            case "<" -> {
                if (main.newValue) {
                    error = true;
                    return;
                }

                align = true;
                alignDB = new ArrayList<>();
                ++input.index;

                while (input.strings.size() - input.index > 0) {
                    DB temp = new DB(input, data);
                    if (temp.error) {
                        error = true;
                        return;
                    }
                    alignDB.add(temp);

                    if (input.strings.size() - input.index > 1 && input.strings.get(input.index).equals("<")) ++input.index;
                    else break;
                }

                if (alignDB.isEmpty()) {
                    error = true;
                    return;
                }

                if ((!alignDB.getFirst().newValue && alignDB.getFirst().id == data.index) || (main.id != data.index && !Objects.equals(alignDB.getFirst().getType(data), main.getType(data)))) {
                    System.out.println(Colors.ANSI_RED + "Align error" + Colors.ANSI_RESET);
                    error = true;
                    return;
                }

                for (int a = 1; a < alignDB.size(); ++a) {
                    if ((!alignDB.get(a).newValue && alignDB.get(a).id == data.index) || !Objects.equals(alignDB.getFirst().getType(data), alignDB.get(a).getType(data))) {
                        System.out.println(Colors.ANSI_RED + "Align error" + Colors.ANSI_RESET);
                        error = true;
                        return;
                    }
                }

                return;
            }
        }

        if (!main.newValue && main.id == data.index && to == null) {
            System.out.println("Error not value: " + main.name);
            error = true;
            return;
        }

        int type = (to != null) ? to.getType(data) : main.getType(data);
        commands = new ArrayList<>();

    q:  while (input.strings.size() - input.index > 1) {
            Command command = null;
            switch (input.strings.get(input.index)) {
                case ">" -> { ++input.index; command = new Command(input, type, data, false); }
                case "=" -> {
                    if (!main.newValue) {
                        System.out.println(Colors.ANSI_RED + "Error ravenstvo command new value" + Colors.ANSI_RESET);
                        error = true;
                        return;
                    } command = new Command(input, type, data, true);
                }
                default -> { return; }
            }

            if (command.error) {
                error = true;
                break q;
            }

            type = command.dbtype;
            commands.add(command);

            if (input.strings.size() - input.index > 1 && input.strings.get(input.index).equals(",")) ++input.index;
            else break;
        }
    }
}