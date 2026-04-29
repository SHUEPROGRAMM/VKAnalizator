package org.example.Algorithm;

import org.example.Data.IDsHistory;
import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.VKData.UserDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

public class Generate {
    public static TreeSet<Integer> getGenerateUserFriends(int id) throws InterruptedException {
        TreeSet<Integer> set = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(id);
        TreeSet<Integer> buffer = new TreeSet<>();

        for (int element : set) {
            UserDB userDB = General.users.get(element);
            if (userDB != null && userDB.iDsHistories != null && userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] != null && userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].containAdded(id))
                buffer.add(element);
        }

        if (buffer.isEmpty()) return null;
        return buffer;
    }

    public static TreeSet<Integer> getGenerateUserIds(int id, int indexGenerate, int index, long date) throws InterruptedException {
        TreeSet<Integer> set = General.generateIds[indexGenerate].get(id);
        if (set == null) return null;
        TreeSet<Integer> buffer = new TreeSet<>();

        for (int element : set) {
            UserDB userDB = General.users.get(element);
            if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[index] == null) continue;
            if (userDB.iDsHistories[index].contain(id, date)) buffer.add(element);
        }

        if (buffer.isEmpty()) return null;
        return buffer;
    }

    public static TreeSet<Integer> getGenerateUserIds(int id, int indexGenerate, int index, long one, long two) {
        TreeSet<Integer> set = General.generateIds[indexGenerate].get(id);
        if (set == null) return null;
        TreeSet<Integer> buffer = new TreeSet<>();

        for (int element : set) {
            UserDB userDB = General.users.get(element);
            if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[index] == null) continue;
            if (userDB.iDsHistories[index].contain(id, one, two)) buffer.add(element);
        } return (buffer.isEmpty()) ? null : buffer;
    }

    public TreeSet<Integer> getGeneralGenerate(int indexGenerate, ArrayList<Integer> ids) throws InterruptedException {
        General.lock.lock1();
        TreeSet<Integer> temp = General.generateIds[indexGenerate].get(ids.getFirst());
        if (temp == null) return null;
        TreeSet<Integer> buffer = new TreeSet<>(temp);

        for (int index = 1; index < ids.size(); ++index) {
            temp = General.generateIds[indexGenerate].get(ids.get(index));
            if (temp == null) return null;
            buffer.retainAll(temp);
        }

        General.lock.unlock1();
        return ((buffer.isEmpty()) ? null : buffer);
    }

    public static ArrayList<Integer> getGeneral(int[][] data) {
        int ind = 0;
        for (int index = 1; index < data.length; ++index)
            if (data[index].length < data[ind].length) ind = index;

        int[] temp = data[ind];
        data[ind] = data[0];
        data[0] = temp;

        ArrayList<Integer> buffer = new ArrayList<>(data[0].length);
    q:  for (int element : data[0]) {
            for (int index = 1; index < data.length; ++index)
                if (Arrays.binarySearch(data[index], element) < 0)
                    continue q;
            buffer.add(element);
        } return (buffer.isEmpty()) ? null : buffer;
    }

    public static ArrayList<Integer> getGeneralUserIds(ArrayList<Integer> data, int indexIds) throws InterruptedException {
        int[][] buffer = new int[data.size()][];
        int index = 0;

        General.lock.lock1();
        for (int element : data) {
            UserDB userDB = General.users.get(element);
            if (userDB == null) return null;
            if (userDB.iDsHistories == null) return null;
            if (userDB.iDsHistories[indexIds] == null) return null;
            int[] temp = userDB.iDsHistories[indexIds].last.data;
            if (temp == null) return null;
            buffer[index++] = temp;
        }

        General.lock.unlock1();
        return getGeneral(buffer);
    }
}
