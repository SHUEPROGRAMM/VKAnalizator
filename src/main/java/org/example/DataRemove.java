package org.example;

import org.example.Algorithm.Generate;
import org.example.Data.IDHistory;
import org.example.Data.StringHistory;
import org.example.Enum.GenerateIDsEnum;
import org.example.VKData.UserDB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataRemove {
    private static class GetRemoves extends Thread {
        public final TreeMap<Integer, TreeSet<Integer>> map;
        public final ArrayList<Integer> userIds;
        public final TreeSet<Integer> delete;
        public final int index;

        public final ArrayList<Integer> buffer = new ArrayList<>();
        public Remove.Node[] nodes;

        public GetRemoves(TreeMap<Integer, TreeSet<Integer>> map, ArrayList<Integer> userIds, TreeSet<Integer> delete, int index) {
            this.map = map;
            this.userIds = userIds;
            this.delete = delete;
            this.index = index;
        }

        @Override
        public void run() {
            for (int element : delete) {
                TreeSet<Integer> ids = map.get(element);
                userIds.forEach(ids::remove);
                if (ids.isEmpty()) {
                    map.remove(element);
                    buffer.add(element);
                }
            }

            if (!buffer.isEmpty()) {
                nodes = Remove.build(buffer);
                General.idGenerateUsers[index] = Remove.remove(map, nodes);
            }
        }
    }

    private static class RemoveStrings extends Thread {
        public final StringHistory stringHistory;
        public final ArrayList<Integer> data;
        public final Remove.Node[] nodes;

        public RemoveStrings(StringHistory stringHistory, ArrayList<Integer> data, Remove.Node[] nodes) {
            this.stringHistory = stringHistory;
            this.data = data;
            this.nodes = nodes;
        }

        @Override
        public void run() { stringHistory.remove(data, nodes); }
    }

    private static class RemoveIdUser extends Thread {
        public final int index;
        public final Remove.Node[] nodes;

        public RemoveIdUser(int index, Remove.Node[] nodes) {
            this.index = index;
            this.nodes = nodes;
        }

        @Override
        public void run() {
            for (Map.Entry<Integer, UserDB> entry : General.users.entrySet())
                if (entry.getValue().idHistories != null && entry.getValue().idHistories[index] != null)
                    entry.getValue().idHistories[index].build(nodes);
        }
    }

    private static class RemoveFriends extends Thread {
        public final ArrayList<Integer> userIds;
        public final int beign, end;

        public RemoveFriends(ArrayList<Integer> userIds, int begin, int end) {
            this.userIds = userIds;
            this.beign = begin;
            this.end = end;
        }

        @Override
        public void run() {
            for (int userId : userIds) {
                try {
                    TreeSet<Integer> generate = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(userId);
                    if (generate == null) continue;
                    TreeSet<Integer> temp = Generate.getGenerateUserFriends(userId);
                    ArrayList<Integer> delete = new ArrayList<>(generate);
                    delete.removeAll(temp);

                    synchronized (General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()]) {
                        if (temp == null) General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].remove(userId);
                        else General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].put(userId, temp);

                        for (int element : delete)
                            General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(element).remove(element);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static class RemoveSubscribers extends Thread {
        public final ArrayList<Integer> userIds;

        public RemoveSubscribers(ArrayList<Integer> userIds) {
            this.userIds = userIds;
        }

        @Override
        public void run() {
            for (int userId : userIds) General.generateIds[GenerateIDsEnum.SUBSCRIBERS.ordinal()].remove(userId);
            ArrayList<Integer> remove = new ArrayList<>();

            for (Map.Entry<Integer, TreeSet<Integer>> entry : General.generateIds[GenerateIDsEnum.SUBSCRIBERS_IN.ordinal()].entrySet()) {
                userIds.forEach(entry.getValue()::remove);
                if (entry.getValue().isEmpty()) remove.add(entry.getKey());
            }
            for (int element : remove) General.generateIds[GenerateIDsEnum.SUBSCRIBERS_IN.ordinal()].remove(element);
        }
    }

    public static void removeUsers(ArrayList<Integer> userIds) throws InterruptedException {
        TreeSet<Integer>[] removeUsersStrings = new TreeSet[General.userStringCount];
        for (int index = 0; index < General.userStringCount; ++index)
            removeUsersStrings[index] = new TreeSet<>();

        General.lock.lock1();
        for (int element : userIds) {
            UserDB userDB = General.users.get(element);
            if (userDB == null || userDB.idHistories == null) continue;

            for (int index = 0; index < General.userStringCount; ++index) {
                if (userDB.idHistories[index] == null) continue;
                for (IDHistory.Node node : userDB.idHistories[index].data)
                    removeUsersStrings[index].add(node.id);
            }
        }

        General.lock.unlock1();

        boolean empty = false;
        for (TreeSet<Integer> element : removeUsersStrings)
            if (!element.isEmpty()) empty = true;
        if (!empty) return;

        General.lock.lock0();

        // создание удаления удаления нод из idGenerate

        GetRemoves[] getRemoves = new GetRemoves[General.userStringCount];
        for (int index = 0; index < General.userStringCount; ++index)
            if (!removeUsersStrings[index].isEmpty())
                getRemoves[index] = new GetRemoves(General.idGenerateUsers[index], userIds, removeUsersStrings[index], index);

        for (GetRemoves element : getRemoves)
            if (element != null) element.start();

        for (GetRemoves element : getRemoves)
            if (element != null) element.join();

        //Удаление элэментов из StringHistory

        RemoveStrings[] removeStrings = new RemoveStrings[General.userStringCount];
        for (int index = 0; index < General.userStringCount; ++index)
            if (getRemoves[index] != null && getRemoves[index].nodes != null)
                removeStrings[index] = new RemoveStrings(General.userStrings[index], getRemoves[index].buffer, getRemoves[index].nodes);

        for (RemoveStrings element : removeStrings)
            if (element != null) element.start();

        for (RemoveStrings element : removeStrings)
            if (element != null) element.join();

        //фикс id users

        RemoveIdUser[] removeIdUsers = new RemoveIdUser[General.userStringCount];
        for (int index = 0; index < General.userStringCount; ++index)
            if (getRemoves[index] != null && getRemoves[index].nodes != null)
                removeIdUsers[index] = new RemoveIdUser(index, getRemoves[index].nodes);

        for (RemoveIdUser element : removeIdUsers)
            if (element != null) element.start();

        for (RemoveIdUser element : removeIdUsers)
            if (element != null) element.join();

        //удаление userIds

        RemoveSubscribers removeSubscribers = new RemoveSubscribers(userIds);
        removeSubscribers.start();
        removeSubscribers.join();

        for (int element : userIds)
            General.users.remove(element);

        Utils.DecomposeTheQuantity decomposeTheQuantity = new Utils.DecomposeTheQuantity(userIds.size(), General.threadCount);
        RemoveFriends[] removeFriends = new RemoveFriends[decomposeTheQuantity.thread];
        for (int index = 0; index < decomposeTheQuantity.thread; ++index) {
            decomposeTheQuantity.next0();
            removeFriends[index] = new RemoveFriends(userIds, decomposeTheQuantity.begin, decomposeTheQuantity.end);
            decomposeTheQuantity.next1();
            removeFriends[index].start();
        } decomposeTheQuantity.reset();


        for (RemoveFriends element : removeFriends)
            element.join();

        General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].entrySet().removeIf(s -> s.getValue().isEmpty());

        General.lock.unlock0();
    }

    public static void removeUserIds(ArrayList<Integer> userIds, int index) throws InterruptedException {
        General.lock.lock0();
        for (int element : userIds) {
            UserDB userDB = General.users.get(element);
            if (userDB != null && userDB.iDsHistories != null && userDB.iDsHistories[index] != null)
                userDB.iDsHistories[index] = null;
        }

        switch (index) {
            case 0 -> {
                Utils.DecomposeTheQuantity decomposeTheQuantity = new Utils.DecomposeTheQuantity(userIds.size(), General.threadCount);
                RemoveFriends[] removeFriends = new RemoveFriends[decomposeTheQuantity.thread];
                for (int a = 0; a < decomposeTheQuantity.thread; ++a) {
                    decomposeTheQuantity.next0();
                    removeFriends[a] = new RemoveFriends(userIds, decomposeTheQuantity.begin, decomposeTheQuantity.end);
                    decomposeTheQuantity.next1();
                    removeFriends[a].start();
                } decomposeTheQuantity.reset();

                for (RemoveFriends element : removeFriends)
                    element.join();
                General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].entrySet().removeIf(s -> s.getValue().isEmpty());
            }
            case 1 -> {
                RemoveSubscribers removeSubscribers = new RemoveSubscribers(userIds);
                removeSubscribers.start();
                removeSubscribers.join();
            }
            case 2 -> {

            }
        }

        General.lock.unlock0();
    }

    public static void removeUserIds(ArrayList<Integer> userIds) throws InterruptedException {
        General.lock.lock0();
        for (int element : userIds) {
            UserDB userDB = General.users.get(element);
            if (userDB != null) {
                userDB.iDsHistories = null;
                if (userDB.isEmpty()) General.users.remove(element);
            }
        } General.lock.unlock0();
    }

    public static void removeOnlineHistory(ArrayList<Integer> userIds) throws InterruptedException {
        General.lock.lock0();
        for (int element : userIds) {
            UserDB userDB = General.users.get(element);
            if (userDB != null) {
                if (userDB.onlineHistory != null) userDB.onlineHistory = null;
                if (userDB.isEmpty()) General.users.remove(element);
            }
        } General.lock.unlock0();
    }

    public static void removePhoneNumber(ArrayList<Integer> userIds) throws InterruptedException {
        General.lock.lock0();
        for (int element : userIds) {
            UserDB userDB = General.users.get(element);
            if (userDB != null) {
                userDB.phoneNumberLong = -1;
                userDB.phoneNumberString = null;
                if (userDB.isEmpty()) General.users.remove(element);
            }
        } General.lock.unlock0();
    }

    public static void removeUserId(ArrayList<Integer> userIds) throws InterruptedException {
        General.lock.lock0();
        for (int element : userIds) {
            UserDB userDB = General.users.get(element);
            if (userDB != null) {
                if (userDB.idHistories != null) userDB.idHistories = null;
                if (userDB.isEmpty()) General.users.remove(element);
            }
        } General.lock.unlock0();
    }

    public static void removeGroups(ArrayList<Integer> groupIds) throws InterruptedException {
        General.lock.lock0();

        General.lock.unlock0();
    }
}
