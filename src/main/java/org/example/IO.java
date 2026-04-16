package org.example;

import org.example.Data.StringHistory;
import org.example.VKData.UserDB;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class IO {
    public static void save(DataOutputStream dataOutputStream) throws IOException, InterruptedException {
        General.lock.lock1();
        for (StringHistory element : General.userStrings)
            element.save(dataOutputStream);

        dataOutputStream.writeInt(General.users.size());
        for (Map.Entry<Integer, UserDB> entry : General.users.entrySet()) {
            dataOutputStream.writeInt(entry.getKey());
            entry.getValue().save(dataOutputStream);
        } General.lock.unlock1();
    }

    public static void load(DataInputStream dataInputStream) throws IOException, InterruptedException {
        General.lock.lock0();
        int[][] arrays = new int[General.userStringCount][];
        for (int index = 0; index < arrays.length; ++index)
            arrays[index] = General.userStrings[index].load(dataInputStream, dataInputStream.readInt());

        int userCount = dataInputStream.readInt();
        for (int index = 0; index < userCount; ++index) {
            int id = dataInputStream.readInt();
            General.users.computeIfAbsent(id, s -> new UserDB()).load(dataInputStream, arrays, id);
        } General.lock.unlock0();
    }
}
