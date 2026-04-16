package org.example.Algorithm;

import org.example.Data.IDHistory;
import org.example.General;
import org.example.VKData.UserDB;

import java.util.*;

public class IDUsersGenerate {
    public static int getMax(TreeMap<Integer, Integer> data) {
        int id = 0, max = 0;
        for (Map.Entry<Integer, Integer> entry : data.entrySet())
            if (entry.getValue() > max) { id = entry.getKey(); max = entry.getValue(); }
        return id;
    }

    public static int getGenerateUserIdGenerate(int id, int indexGenerate, int index) throws InterruptedException {
        General.lock.lock1();
        TreeSet<Integer> generate = General.generateIds[indexGenerate].get(id);
        if (generate == null) return -1;

        TreeMap<Integer, Integer> buffer = new TreeMap<>();
        for (int element : generate) {
            UserDB userDB = General.users.get(element);
            if (userDB == null) continue;
            if (userDB.idHistories == null) continue;
            if (userDB.idHistories[index] == null) continue;
            buffer.put(userDB.idHistories[index].data.getLast().id, buffer.getOrDefault(userDB.idHistories[index].data.getLast().id, 0) + 1);
        }

        General.lock.unlock1();
        if (buffer.isEmpty()) return -2;
        return getMax(buffer);
    }

    public static int getGenerateUserIdGenerate(int id, int indexGenerate, int index, long date) throws InterruptedException {
        General.lock.lock1();
        TreeSet<Integer> generate = Generate.getGenerateUserIds(id, indexGenerate, index, date);
        if (generate == null || generate.isEmpty()) return -1;

        TreeMap<Integer, Integer> buffer = new TreeMap<>();
        for (int element : generate) {
            UserDB userDB = General.users.get(element);
            if (userDB == null) continue;
            if (userDB.idHistories == null) continue;
            if (userDB.idHistories[index] == null) continue;

            IDHistory.Node node = userDB.idHistories[index].get(date);
            if (node == null) continue;
            buffer.put(node.id, buffer.getOrDefault(node.id, 0) + 1);
        }

        General.lock.unlock1();
        if (buffer.isEmpty()) return -2;
        return getMax(buffer);
    }

    public static int getGenerateUserId(int userId, int indexIds, int indexId) throws InterruptedException {
        General.lock.lock1();
        UserDB userDB = General.users.get(userId);
        if (userDB == null) return -1;
        if (userDB.iDsHistories == null) return -2;
        if (userDB.iDsHistories[indexIds] == null) return -3;
        if (userDB.iDsHistories[indexIds].last.data == null) return -4;

        TreeMap<Integer, Integer> buffer = new TreeMap<>();
        for (int element : userDB.iDsHistories[indexIds].last.data) {
            UserDB user = General.users.get(element);
            if (user == null) continue;
            if (user.idHistories == null) continue;
            if (user.idHistories[indexId] == null) continue;
            buffer.put(user.idHistories[indexId].data.getLast().id, buffer.getOrDefault(user.idHistories[indexId].data.getLast().id, 0) + 1);
        }

        General.lock.unlock1();
        if (buffer.isEmpty()) return -5;
        return getMax(buffer);
    }

    public static int getGenerateUserId(int userId, int indexIds, int indexId, long date) throws InterruptedException {
        General.lock.lock1();
        UserDB userDB = General.users.get(userId);
        if (userDB == null) return -1;
        if (userDB.iDsHistories == null) return -2;
        if (userDB.iDsHistories[indexIds] == null) return -3;
        TreeSet<Integer> set = userDB.iDsHistories[indexIds].get(date);
        if (set == null) return -4;
        if (set.isEmpty()) return -5;

        TreeMap<Integer, Integer> buffer = new TreeMap<>();
        for (int element : userDB.iDsHistories[indexIds].last.data) {
            UserDB user = General.users.get(element);
            if (user == null) continue;
            if (user.idHistories == null) continue;
            if (user.idHistories[indexId] == null) continue;

            IDHistory.Node node = user.idHistories[indexId].get(date);
            buffer.put(node.id, buffer.getOrDefault(node.id, 0) + 1);
        }

        General.lock.unlock1();
        if (buffer.isEmpty()) return -6;
        return getMax(buffer);
    }
}
