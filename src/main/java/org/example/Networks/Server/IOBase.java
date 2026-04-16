package org.example.Networks.Server;

import org.example.Algorithm.Generate;
import org.example.Commands.Command;
import org.example.DB.Base;
import org.example.DB.Chains;
import org.example.DB.Groups;
import org.example.DB.Users;
import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.Networks.Data;
import org.example.VKData.UserDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class IOBase {
    public TreeMap<Integer, Base> map = new TreeMap<>();
    public int index = 0;

    private Base getBase(Data.DB data, boolean copy) throws InterruptedException {
        if (!data.newValue) return (data.id == index) ? null : (copy) ? map.get(data.id).copy() : map.get(data.id);
        switch (data.data.type) {
            case 0 -> {
                return new Users(data.data.data);
            }
            case 1 -> {
                TreeSet<Integer> ids = new TreeSet<>();
                General.lock.lock1();

                if (data.data.date == 0) {
                    for (int element : data.data.data) {
                        UserDB user = General.users.get(element);
                        if (user == null || user.iDsHistories == null || user.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || user.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                        for (int id : user.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data)
                            ids.add(id);
                    }
                } else {
                    for (int element : data.data.data) {
                        UserDB user = General.users.get(element);
                        if (user == null || user.iDsHistories == null || user.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || user.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;
                        TreeSet<Integer> temp = user.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(data.data.date);
                        if (temp == null) continue;
                        ids.addAll(temp);
                    }
                }

                General.lock.unlock1();
                Users buffer = new Users(new ArrayList<>(ids));
                return buffer;
            }
            case 2 -> {
                TreeSet<Integer> ids = new TreeSet<>();
                General.lock.lock1();

                if (data.data.date == 0) {
                    for (int element : data.data.data) {
                        TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(element);
                        if (temp != null) ids.addAll(temp);
                    }
                } else {
                    for (int element : data.data.data) {
                        TreeSet<Integer> temp = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), data.data.date);
                        if (temp != null) ids.addAll(temp);
                    }
                }

                General.lock.unlock0();
                Users buffer = new Users(new ArrayList<>(ids));
                return buffer;
            }
            default -> { return null; }
        }
    }

    public boolean run(Server.Node client) throws IOException, InterruptedException {
        switch (client.dataInputStream.read()) {
            case 0 -> {
                map.clear();
                index = 0;
            }
            case 1 -> {
                int count = client.dataInputStream.readInt();
                for (int index = 0; index < count; ++index)
                    map.remove(client.dataInputStream.readInt());
            }
            case 2 -> {

                System.out.println(map.size());

                for (Map.Entry<Integer, Base> entry : map.entrySet())
                    client.dataOutputStream.writeInt(entry.getValue().data.size());
            }
            case 3 -> {
                Data.DB main = new Data.DB(client.dataInputStream);
                int count = client.dataInputStream.readInt();
                TreeSet<Object> objects = new TreeSet<>();
                Base mainBase;
                int a;

                if (!main.newValue && main.id == index) {
                    mainBase = getBase(new Data.DB(client.dataInputStream), true);
                    map.put(index, mainBase);
                    ++index; a = 1;
                } else { mainBase = getBase(main, false); a = 0; }

                while (a < count) { objects.addAll(getBase(new Data.DB(client.dataInputStream), false).data); ++a; }
                mainBase.append(objects);

                System.out.println(map.size());
            }
            case 4 -> {
                Data.DB main = new Data.DB(client.dataInputStream);
                int index;
                boolean backCopy = true;
                Base baseMain;

                if (!main.newValue) {
                    index = main.id;
                    if (main.id == this.index) {
                        baseMain = getBase(new Data.DB(client.dataInputStream), false);
                        map.put(index, baseMain);
                    } else baseMain = getBase(main, false);
                } else baseMain = getBase(main, false);
                int count = client.dataInputStream.readInt();

                for (int a = 0; a < count; ++a) {
                    if (baseMain.data == null || baseMain.data.isEmpty()) {
                        client.dataOutputStream.writeBoolean(false);
                        client.dataOutputStream.flush();
                        return true;
                    }

                    client.dataOutputStream.writeBoolean(true);
                    client.dataOutputStream.flush();

                    Command command = new Command(client.dataInputStream, !main.newValue && client.dataInputStream.readBoolean());
                    General.lock.lock1();

                    switch (command.data) {
                        case Integer integer -> {
                            client.dataOutputStream.writeInt(baseMain.data.size());
                            if (integer == 1) break;

                            switch (baseMain) {
                                case Users users -> {
                                    for (int id : users.data) {
                                        client.dataOutputStream.writeInt(id);
                                        UserDB userDB = General.users.get(id);

                                        if (userDB != null) {
                                            client.dataOutputStream.writeBoolean(true);
                                            if (command.date.one == 0) userDB.out(client.dataOutputStream);
                                            else userDB.out(client.dataOutputStream, command.date.one);
                                        } else client.dataOutputStream.writeBoolean(false);
                                    }
                                }
                                case Groups groups -> {

                                }
                                case Chains chains -> {

                                }
                                default -> throw new IllegalStateException("Unexpected value: " + baseMain);
                            }
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + command.data);
                    }

                    General.lock.unlock1();
                    client.dataOutputStream.flush();
                }
            }
        }

        client.dataOutputStream.flush();
        return true;
    }
}
