package org.example.Algorithm;

import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.VKData.UserDB;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Probability {
    public static ArrayList<Integer> get(TreeMap<Integer, Integer> map, int count, int percent) {
        if (map.isEmpty()) return null;
        ArrayList<Integer> buffer = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (((100 * entry.getValue()) / count) >= percent)
                buffer.add(entry.getKey());
        } return (buffer.isEmpty()) ? null : buffer;
    }

    public static ArrayList<Integer> probabilityUserIds(int id, int indexIn, int indexTo, int percent) {
        UserDB userDB = General.users.get(id);
        if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[indexIn] == null || userDB.iDsHistories[indexIn].last.data == null) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : userDB.iDsHistories[indexIn].last.data) {
            UserDB temp = General.users.get(element);
            if (temp == null || temp.iDsHistories == null || temp.iDsHistories[indexTo] == null || temp.iDsHistories[indexTo].last.data == null) continue;

            for (int elem : temp.iDsHistories[indexTo].last.data)
                map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, userDB.iDsHistories[indexIn].last.data.length, percent);
    }

    public static ArrayList<Integer> probabilityUserIds(int id, int indexIn, int indexTo, int percent, long date) {
        UserDB userDB = General.users.get(id);
        if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[indexIn] == null) return null;

        TreeSet<Integer> tempIn = userDB.iDsHistories[indexIn].get(date);
        if (tempIn == null || tempIn.isEmpty()) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : tempIn) {
            UserDB temp = General.users.get(element);
            if (temp == null || temp.iDsHistories == null || temp.iDsHistories[indexTo] == null) continue;
            TreeSet<Integer> tempTo = temp.iDsHistories[indexTo].get(date);
            if (tempTo == null || tempTo.isEmpty()) continue;
            for (int elem : tempTo) map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, tempIn.size(), percent);
    }

    public static ArrayList<Integer> probabilityUserIds(int id, int indexIn, int indexTo, int percent, long one, long two) {
        UserDB userDB = General.users.get(id);
        if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[indexIn] == null) return null;

        TreeSet<Integer> tempIn = userDB.iDsHistories[indexIn].all(one, two);
        if (tempIn == null || tempIn.isEmpty()) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : tempIn) {
            UserDB temp = General.users.get(element);
            if (temp == null || temp.iDsHistories == null || temp.iDsHistories[indexTo] == null) continue;
            TreeSet<Integer> tempTo = temp.iDsHistories[indexTo].all(one, two);
            if (tempTo == null || tempTo.isEmpty()) continue;
            for (int elem : tempTo) map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, tempIn.size(), percent);
    }

    public static ArrayList<Integer> probabilityGenerate(int id, int indexIn, int indexTo, int percent) {
        TreeSet<Integer> tempIn = General.generateIds[indexIn].get(id);
        if (tempIn == null) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : tempIn) {
            TreeSet<Integer> tempTo = General.generateIds[indexTo].get(element);
            if (tempTo == null) continue;
            for (int elem : tempTo) map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, tempIn.size(), percent);
    }

    public static ArrayList<Integer> probabilityGenerateFriends(int id, long date, int percent) throws InterruptedException {
        TreeSet<Integer> tempIn = Generate.getGenerateUserIds(id, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
        if (tempIn == null) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : tempIn) {
            TreeSet<Integer> tempTo = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
            if (tempTo == null) continue;
            for (int elem : tempTo) map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, tempIn.size(), percent);
    }

    public static ArrayList<Integer> probabilityGenerateFriends(int id, long one, long two, int percent) throws InterruptedException {
        TreeSet<Integer> tempIn = Generate.getGenerateUserIds(id, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), one, two);
        if (tempIn == null) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : tempIn) {
            TreeSet<Integer> tempTo = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), one, two);
            if (tempTo == null) continue;
            for (int elem : tempTo) map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, tempIn.size(), percent);
    }

    public static ArrayList<Integer> probabilityGenerateSubscribers(int id, long date, int percent) throws InterruptedException {
        TreeSet<Integer> tempIn = Generate.getGenerateUserIds(id, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
        if (tempIn == null) return null;
        TreeMap<Integer, Integer> map = new TreeMap<>();

        for (int element : tempIn) {
            TreeSet<Integer> tempTo = Generate.getGenerateUserIds(element, GenerateIDsEnum.SUBSCRIBERS.ordinal(), UserIDsEnum.SUBSCRIBERS.ordinal(), date);
            if (tempTo == null) continue;
            for (int elem : tempTo) map.put(elem, map.getOrDefault(elem, 0) + 1);
        } return get(map, tempIn.size(), percent);
    }
}
