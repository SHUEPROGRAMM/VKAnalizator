package org.example.Algorithm;

import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.VKData.UserDB;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ChainBase {
    public static ArrayList<TreeMap<Integer, TreeSet<Integer>>> getChainGenerate(ArrayList<Integer> data, int level) {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> buffer = new ArrayList<>();
        ArrayList<Integer> scanned = new ArrayList<>();
        TreeSet<Integer> scan = new TreeSet<>(data);

        for (int a = 0; a < level; a++) {
            if (scan.isEmpty()) return null;
            TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();

            for (int element : scan) {
                TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(element);
                if (temp == null) continue;

                for (int elem : temp)
                    if (!scanned.contains(elem)) map.computeIfAbsent(elem, s -> new TreeSet<>()).add(element);
            }

            if ((level - a) > 1) {
                scanned.addAll(map.keySet());
                scan.clear();

                for (Map.Entry<Integer, TreeSet<Integer>> entry : map.entrySet()) {
                    TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(entry.getKey());
                    if (temp != null) scan.addAll(temp);
                } scanned.forEach(scan::remove);
            }

            if (map.isEmpty()) return null;
            buffer.add(map);
        } return buffer;
    }

    public static ArrayList<TreeMap<Integer, TreeSet<Integer>>> getChainGenerate(ArrayList<Integer> data, int level, long date) throws InterruptedException {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> buffer = new ArrayList<>();
        ArrayList<Integer> scanned = new ArrayList<>();
        TreeSet<Integer> scan = new TreeSet<>(data);

        for (int a = 0; a < level; a++) {
            if (scan.isEmpty()) return null;
            TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();

            for (int element : scan) {
                TreeSet<Integer> temp = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                if (temp == null) continue;

                for (int elem : temp)
                    if (!scanned.contains(elem)) map.computeIfAbsent(elem, s -> new TreeSet<>()).add(element);
            }

            if ((level - a) > 1) {
                scanned.addAll(map.keySet());
                scan.clear();

                for (Map.Entry<Integer, TreeSet<Integer>> entry : map.entrySet()) {
                    TreeSet<Integer> temp = Generate.getGenerateUserIds(entry.getKey(), GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                    if (temp != null) scan.addAll(temp);
                } scanned.forEach(scan::remove);
            }

            if (map.isEmpty()) return null;
            buffer.add(map);
        } return buffer;
    }

    public static ArrayList<TreeMap<Integer, TreeSet<Integer>>> getChain(ArrayList<Integer> data, int level) {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> buffer = new ArrayList<>();
        ArrayList<Integer> scanned = new ArrayList<>();
        TreeSet<Integer> scan = new TreeSet<>(data);

        for (int a = 0; a < level; a++) {
            if (scan.isEmpty()) return null;
            TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();

            for (int element : scan) {
                UserDB userDB = General.users.get(element);
                if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                for (int elem : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data)
                    if (!scanned.contains(elem)) map.computeIfAbsent(elem, s -> new TreeSet<>()).add(element);
            }

            if ((level - a) > 1) {
                scanned.addAll(map.keySet());
                scan.clear();

                for (Map.Entry<Integer, TreeSet<Integer>> entry : map.entrySet()) {
                    UserDB userDB = General.users.get(entry.getKey());
                    if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                    for (int element : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data)
                        scan.add(element);
                } scanned.forEach(scan::remove);
            }

            if (map.isEmpty()) return null;
            buffer.add(map);
        } return buffer;
    }

    public static ArrayList<TreeMap<Integer, TreeSet<Integer>>> getChain(ArrayList<Integer> data, int level, long date) {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> buffer = new ArrayList<>();
        ArrayList<Integer> scanned = new ArrayList<>();
        TreeSet<Integer> scan = new TreeSet<>(data);

        for (int a = 0; a < level; a++) {
            if (scan.isEmpty()) return null;
            TreeMap<Integer, TreeSet<Integer>> map = new TreeMap<>();

            for (int element : scan) {
                UserDB userDB = General.users.get(element);
                if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                TreeSet<Integer> temp = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                if (temp == null) continue;

                for (int elem : temp)
                    if (!scanned.contains(elem)) map.computeIfAbsent(elem, s -> new TreeSet<>()).add(element);
            }

            if ((level - a) > 1) {
                scanned.addAll(map.keySet());
                scan.clear();

                for (Map.Entry<Integer, TreeSet<Integer>> entry : map.entrySet()) {
                    UserDB userDB = General.users.get(entry.getKey());
                    if (userDB == null || userDB.iDsHistories == null || userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                    TreeSet<Integer> temp = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                    if (temp != null) scan.addAll(temp);
                } scanned.forEach(scan::remove);
            }

            if (map.isEmpty()) return null;
            buffer.add(map);
        } return buffer;
    }
}
