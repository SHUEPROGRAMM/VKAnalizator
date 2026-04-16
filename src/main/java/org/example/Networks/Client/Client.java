package org.example.Networks.Client;

import org.example.Algorithm.Chains;
import org.example.Colors;
import org.example.Commands.Filter;
import org.example.Console.Input;
import org.example.General;
import org.example.Commands.Command;
import org.example.VKData.UserDB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Client extends Thread {
    public record IdDB(int index, int type) {
    }

    public static class Data {
        public final HashMap<String, IdDB> map = new HashMap<>();
        public final TreeMap<Integer, String> mapBack = new TreeMap<>();
        public int index = 0;
    }

    public final Socket socket;
    public final DataInputStream dataInputStream;
    public final DataOutputStream dataOutputStream;
    public final Data data = new Data();

    public Client(String address) throws IOException {
        this.socket = new Socket(address, General.port);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        Input input = new Input();
    q:  while (true) {
            if (!input.next(Colors.ANSI_GREEN + "Net> " + Colors.ANSI_RESET)) continue;
            try {
                while (input.strings.size() - input.index > 0) {
                    switch (input.strings.get(input.index)) {
                        case "clear" -> {
                            data.map.clear();
                            data.mapBack.clear();
                            data.index = 0;

                            dataOutputStream.write(0);
                        }
                        case "remove" -> {
                            ArrayList<String> strings = input.getStrings();
                            if (strings == null) continue q;

                            ArrayList<Integer> ids = new ArrayList<>();
                            for (String element : strings) {
                                IdDB id = data.map.get(element);
                                if (id != null) {
                                    data.map.remove(element);
                                    data.mapBack.remove(id.index);
                                    ids.add(id.index);
                                }
                            }
                            if (ids.isEmpty()) break;

                            dataOutputStream.write(1);
                            dataOutputStream.writeInt(ids.size());
                            for (int element : ids)
                                dataOutputStream.writeInt(element);
                        }
                        case "list" -> {
                            dataOutputStream.write(2);
                            dataOutputStream.flush();

                            System.out.println(Colors.ANSI_CYAN + "values count: " + data.map.size() + Colors.ANSI_RESET);
                            for (Map.Entry<Integer, String> entry : data.mapBack.entrySet()) {
                                System.out.println(entry.getValue() + "\t" + switch (data.map.get(data.mapBack.get(entry.getKey())).type) {
                                            case 0 -> "users";
                                            case 1 -> "groups";
                                            case 2 -> "chain";
                                            default ->
                                                    throw new IllegalStateException("Unexpected value: " + dataInputStream.readInt());
                                        } + ":" + dataInputStream.readInt()
                                );
                            }
                        }
                        case "exit" -> {
                            try {
                                dataInputStream.close();
                                dataOutputStream.close();
                                socket.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } break q;
                        }
                        default -> {
                            org.example.Networks.Data temp = new org.example.Networks.Data(input, data);
                            if (temp.error) continue q;

                            if (temp.align) {
                                dataOutputStream.write(3);
                                temp.main.out(dataOutputStream);

                                data.map.put(temp.main.name, new IdDB(temp.main.id, temp.alignDB.getFirst().getType(data)));
                                data.mapBack.put(temp.main.id, temp.main.name);
                                if (temp.main.id == data.index) ++data.index;

                                dataOutputStream.writeInt(temp.alignDB.size());
                                for (org.example.Networks.Data.DB element : temp.alignDB)
                                    element.out(dataOutputStream);
                            } else {
                                dataOutputStream.write(4);
                                temp.main.out(dataOutputStream);
                                if (!temp.main.newValue) {
                                    dataOutputStream.writeBoolean(temp.to != null);
                                    if (temp.to != null) temp.to.out(dataOutputStream);
                                }

                                if (temp.commands == null || temp.commands.isEmpty()) {
                                    dataOutputStream.writeInt(0);
                                    break;
                                }

                                dataOutputStream.writeInt(temp.commands.size());
                                dataOutputStream.flush();
                                int type;

                                if (temp.to != null) {
                                    type = temp.to.getType(data);
                                    data.map.put(temp.main.name, new IdDB(temp.main.id, type));
                                    data.mapBack.put(temp.main.id, temp.main.name);
                                    if (temp.main.id == data.index) ++data.index;
                                } else type = temp.main.getType(data);

                                for (Command element : temp.commands) {
                                    if (dataInputStream.readBoolean()) {
                                        if (!temp.main.newValue) dataOutputStream.writeBoolean(element.copy);
                                        element.out(dataOutputStream);
                                        dataOutputStream.flush();

                                        switch (element.data) {
                                            case Integer integer -> {
                                                int count = dataInputStream.readInt();
                                                System.out.println(Colors.ANSI_CYAN + "Values count: " + count + Colors.ANSI_RESET);
                                                if (integer == 1) break;

                                                switch (type) {
                                                    case 0 -> {
                                                        for (int a = 0; a < count; ++a) {
                                                            System.out.print(a + "\t" + dataInputStream.readInt() + ":");
                                                            if (dataInputStream.readBoolean()) System.out.print(UserDB.toString(dataInputStream));
                                                            System.out.println();
                                                        }
                                                    }
                                                    case 1 -> {

                                                    }
                                                    case 2 -> {
                                                        for (int index = 0; index < count; ++index)
                                                            Chains.Chain.outConsole(dataInputStream);
                                                    }
                                                }
                                            }
                                            default -> {}
                                        }
                                    } else {
                                        System.out.println("Error value is empty");
                                        continue q;
                                    }

                                    type = element.dbtype;
                                    if (element.copy) data.map.put(temp.main.name, new IdDB(temp.main.id, type));
                                }
                            }
                        }
                    }

                    dataOutputStream.flush();
                    ++input.index;

                    if (input.strings.size() - input.index < 3 && input.strings.get(input.index).equals("&")) ++input.index;
                    else break;
                }
            } catch (IOException e) {
                try {
                    dataInputStream.close();
                    dataOutputStream.close();
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
    }
}
