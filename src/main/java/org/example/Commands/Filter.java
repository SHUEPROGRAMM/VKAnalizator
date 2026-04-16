package org.example.Commands;

import org.example.Colors;
import org.example.Console.Input;
import org.example.Enum.UserIDEnum;
import org.example.Enum.UserIDsEnum;
import org.example.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Filter {
    //Online boolean

    public record Id(int type, int id) {
        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.write(type);
            dataOutputStream.writeInt(id);
        }
    }

    public record BDate (int one, int two) {
        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeInt(one);
            dataOutputStream.writeInt(two);
        }
    }

    public static class Names {
        public int type;
        public Object data;

        public Names() {}

        public Names(DataInputStream dataInputStream) throws IOException {
            type = dataInputStream.read();
            int count = dataInputStream.readInt();

            if (type == -1) {
                data = new ArrayList<Input.Names>(count);
                ArrayList<Input.Names> names = (ArrayList<Input.Names>) this.data;
                for (int a = 0; a < count; ++a)
                    names.add(new Input.Names(dataInputStream.readUTF(), dataInputStream.readUTF()));
            } else {
                data = new ArrayList<String>(count);
                ArrayList<String> names = (ArrayList<String>) this.data;
                for (int a = 0; a < count; ++a)
                    names.add(dataInputStream.readUTF());
            }
        }

        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.write(type);
            if (type == -1) {
                ArrayList<Input.Names> names = (ArrayList<Input.Names>) this.data;
                dataOutputStream.writeInt(names.size());
                for (Input.Names element : names) {
                    dataOutputStream.writeUTF(element.first());
                    dataOutputStream.writeUTF(element.last());
                }
            } else {
                ArrayList<String> names = (ArrayList<String>) this.data;
                dataOutputStream.writeInt(names.size());
                for (String element : names)
                    dataOutputStream.writeUTF(element);
            }
        }
    }

    public boolean remove;
    public Object data;
    public boolean error = false;

    Filter(Input input, int type, boolean remove) {
        this.remove = remove;
        switch (type) {
            case 0 -> {
                switch (input.strings.get(++input.index)) {
                    case "online" -> {
                        if (input.strings.size() - input.index < 2) {
                            System.out.println(Colors.ANSI_RED + "Error not online type" + Colors.ANSI_RESET);
                            error = true;
                            return;
                        }

                        switch (input.strings.get(++input.index)) {
                            case "yes" -> { this.data = true; }
                            case "no" -> { this.data = false; }
                            default -> {
                                System.out.println(Colors.ANSI_RED + "Error not online type: " + input.strings.get(input.index) + Colors.ANSI_RESET);
                                error = true;
                                return;
                            }
                        }
                    }
                    case "names" -> {
                        this.data = new Names();
                        ++input.index;
                        Names temp = (Names) this.data;
                        temp.type = - 1;
                        temp.data = input.getNames();
                        ArrayList<Input.Names> temp2 = (ArrayList<Input.Names>) temp.data;
                        if (temp2 == null) error = true;
                    }
                    case "name" -> {
                        if (input.strings.size() - input.index < 3) {
                            System.out.println(Colors.ANSI_RED + "Error not name filter or remove" + Colors.ANSI_RESET);
                            error = true;
                            return;
                        }

                        int tempType = switch (input.strings.get(++input.index)) {
                            case "first" -> 0;
                            case "last" -> 1;
                            case "nick" -> 2;
                            case "status" -> 3;
                            case "domain" -> 4;
                            default -> -1;
                        };

                        if (tempType == -1) {
                            System.out.println(Colors.ANSI_RED + "Error not name: " + input.strings.get(input.index) + Colors.ANSI_RESET);
                            error = true;
                            return;
                        }

                        this.data = new Names();
                        Names temp = (Names) this.data;
                        temp.type = tempType;
                        temp.data = input.getStrings();
                        ArrayList<String> temp2 = (ArrayList<String>) temp.data;
                        if (temp2 == null) error = true;
                    }
                    case "dbate" -> {
                        if (Utils.isBDate(input.strings.get(++input.index)) && Utils.isBDate(input.strings.get(input.index + 1))) {
                            this.data = new BDate(Utils.addBDate(input.strings.get(input.index)), Utils.addBDate(input.strings.get(input.index) + 1));
                            ++input.index;
                        } else {
                            System.out.println(Colors.ANSI_RED + "Error not dbate format" + Colors.ANSI_RESET);
                            error = true;
                        }
                    }
                    default -> {
                        int tempType = switch (input.strings.get(++input.index)) {
                            case "type" -> UserIDEnum.TYPE.ordinal();
                            case "sex" -> UserIDEnum.SEX.ordinal();
                            case "city" -> UserIDEnum.CITY.ordinal();
                            default -> -1;
                        };

                        if (tempType == -1) {
                            System.out.println(Colors.ANSI_RED + "Error not type" + Colors.ANSI_RESET);
                            error = true;
                            return;
                        } int id;

                        try {
                            id = Integer.parseInt(input.strings.get(++input.index));
                        } catch (NumberFormatException e) {
                            System.out.println(e.toString());
                            error = true;
                            return;
                        }

                        switch (type) {
                            case 5 -> {
                                if (id < 0 || id > 3) {
                                    System.out.println("Error id < 0 or id > 3 of type");
                                    error = true;
                                    return;
                                }
                            }
                            case 6 -> {
                                if (id < 1 || id > 2) {
                                    System.out.println("Error id < 1 or id > 2 of type");
                                    error = true;
                                    return;
                                }
                            }
                        }; this.data = new Id(tempType, id);
                    }
                }
            }
            case 1 -> {

            }
        }
    }

    public Filter(DataInputStream dataInputStream) throws IOException {
        this.remove = dataInputStream.readBoolean();

        switch (dataInputStream.read()) {
            case 0 -> { this.data = new Id(dataInputStream.read(), dataInputStream.readInt()); }
            case 1 -> { this.data = dataInputStream.readBoolean(); }
            case 2 -> { this.data = new Names(dataInputStream); }
            case 3 -> { this.data = new BDate(dataInputStream.readInt(), dataInputStream.readInt()); }
        }
    }

    public void out(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(remove);

        switch (data) {
            case Id id -> {
                dataOutputStream.write(0);
                id.out(dataOutputStream);
            }
            case Boolean bool -> {
                dataOutputStream.write(1);
                dataOutputStream.writeBoolean(bool);
            }
            case Names names -> {
                dataOutputStream.write(2);
                names.out(dataOutputStream);
            }
            case BDate bDate -> {
                dataOutputStream.write(3);
                bDate.out(dataOutputStream);
            }
            default -> throw new IllegalStateException("Unexpected value: " + data);
        }
    }
}