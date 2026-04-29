package org.example.Console;

import org.example.Algorithm.Chains;
import org.example.Algorithm.Generate;
import org.example.Clients.ScanClasses;
import org.example.Colors;
import org.example.DB.Base;
import org.example.DB.Groups;
import org.example.DB.IDsBase;
import org.example.DB.Users;
import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.Utils;
import org.example.VKData.UserDB;

import java.net.CookieHandler;
import java.util.*;

public class Other {
    public static HashMap<String, Base> map = new HashMap<>();

    public static class Node {
        public boolean copy;
        public Base data;

        public Node(boolean copy, Base data) {
            this.copy = copy;
            this.data = data;
        }
    }

    public static Node getBase(boolean copy) throws InterruptedException {
        Base data = null;
        long date = 0;
        String name = General.input.strings.get(General.input.index);
        ++General.input.index;

        int type = switch (name) {
            case "users" -> 0;
            case "friends" -> 1;
            case "friendsGenerate" -> 2;
            case "groups" -> 3;
            case "groupsIn" -> 4;
            case "groupsInGenerate" -> 5;
            case "subscribers" -> 6;
            case "subscribersIn" -> 7;
            case "usersAll" -> 8;
            default -> -1;
        };

        if (type == -1) {
            if (Utils.isInteger(name)) return null;
            return new Node(true, (copy) ? map.get(name).copy() : map.get(name));
        }

        Long dateTemp = General.input.getTime();
        if (dateTemp == null) return null;
        date = dateTemp;

        General.lock.lock1();
        switch (type) {
            case 8 -> { data = new Users(new ArrayList<>(General.users.keySet())); }
            default -> {
                ArrayList<Integer> ids = General.input.getIntegers();
                if (ids == null) return null;

                switch (type) {
                    case 0 -> { data = new Users(ids); }
                    case 1 -> {
                        TreeSet<Integer> set = new TreeSet<>();
                        if (date == 0) {
                            for (int element : ids) {
                                UserDB userDB = General.users.get(element);
                                if (userDB == null) continue;
                                if (userDB.iDsHistories == null) continue;
                                if (userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                                if (userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;
                                for (int id : userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data) set.add(id);
                            }
                        } else {
                            for (int element : ids) {
                                UserDB userDB = General.users.get(element);
                                if (userDB == null) continue;
                                if (userDB.iDsHistories == null) continue;
                                if (userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) continue;
                                if (userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].last.data == null) continue;

                                TreeSet<Integer> temp = userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].get(date);
                                if (temp == null) continue;
                                set.addAll(temp);
                            }
                        } data = new Users(new ArrayList<>(set));
                    }
                    case 2 -> {
                        TreeSet<Integer> set = new TreeSet<>();
                        if (date == 0) {
                            for (int element : ids) {
                                TreeSet<Integer> temp = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].get(element);
                                if (temp == null) continue;
                                set.addAll(temp);
                            }
                        } else {
                            for (int element : ids) {
                                TreeSet<Integer> temp = Generate.getGenerateUserIds(element, GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), date);
                                if (temp == null) continue;
                                set.addAll(temp);
                            }
                        } data = new Users(new ArrayList<>(set));
                    }
                    case 3 -> {  }
                    case 4 -> {  }
                    case 5 -> {  }
                }
            }
        }

        General.lock.unlock1();
        return new Node(false, data);
    }

    public static boolean isValue(String string) {
        return switch (string) {
            case "to", "token", "dataBase" -> false;
            default -> true;
        };
    }

    public static boolean run() throws InterruptedException {
        String name = General.input.strings.get(General.input.index);
        Base data = null;
        int valueType;

        switch (name) {
            case "remove" -> {
                ++General.input.index;
                ArrayList<String> strings = General.input.getStrings();
                if (strings == null) return false;

                for (String element : strings)
                    map.remove(element);
                return true;
            }
            case "clear" -> {
                map.clear();
                ++General.input.index;
                return true;
            }
            case "list" -> {
                System.out.println(Colors.ANSI_BLUE + "value count: " + Integer.toString(map.size()) + Colors.ANSI_RESET);
                for (Map.Entry<String, Base> entry : map.entrySet())
                    System.out.println(Colors.ANSI_GREEN + ":" + entry.getKey() + ":\t" +
                            switch (entry.getValue()) {
                                case Users users -> "Users";
                                case Groups groups -> "Groups";
                                case org.example.DB.Chains chains -> "Chains";
                                default -> throw new IllegalStateException("Unexpected value: " + entry.getValue());
                            }
                            + Colors.ANSI_RESET + "\t" + ((entry.getValue().data == null) ? 0 : entry.getValue().data.size()));
                ++General.input.index;
                return true;
            }
            default -> {
                if (Utils.isInteger(General.input.strings.get(General.input.index))) {
                    System.out.println(Colors.ANSI_RED + "Error is integer: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                    return false;
                }

                Node node = getBase(false);
                if (node == null) return false;
                if (node.data == null) {
                    valueType = 0;
                }
                else {
                    valueType = ((node.copy) ? 1 : 2);
                    data = node.data;
                }
            }
        }

        if (General.input.strings.size() - General.input.index == 0) {
            System.out.println(Colors.ANSI_RED + "Error not command: " + name + Colors.ANSI_RESET);
            return false;
        }

    q:  switch (General.input.strings.get(General.input.index)) {
            case "=" -> {
                if (valueType == 2) {
                    System.out.println(Colors.ANSI_RED + "Error not value: " + name + Colors.ANSI_RESET);
                    return false;
                }

                ++General.input.index;
                switch (General.input.strings.get(General.input.index)) {
                    case "filter", "remove", "chain", "out" -> { --General.input.index; }
                    case "dataBase", "token", "about", "help", "exit" -> {
                        System.out.println(Colors.ANSI_RED + "Error align command: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                        return false;
                    }
                    default -> {
                        String name2 = General.input.strings.get(General.input.index);
                        Node node = getBase(true);

                        if (node == null) return false;
                        if (node.data == null) {
                            System.out.println(Colors.ANSI_RED + "Error not value: " + name2 + Colors.ANSI_RESET);
                            return false;
                        }

                        valueType = 2;
                        map.put(name, node.data);
                        data = node.data;
                    }
                }
            }
            case "<" -> {
                if (valueType == 2) {
                    System.out.println(Colors.ANSI_RED + "Error not value: " + name + Colors.ANSI_RESET);
                    return false;
                }

                ++General.input.index;
                if (data == null) {
                    Node node = getBase(true);
                    if (node == null) return false;
                    if (node.data == null) {
                        System.out.println("Error 228");
                        return false;
                    }

                    valueType = 2;
                    data = node.data;
                    map.put(name, data);
                    if (!(General.input.strings.size() - General.input.index > 1 && General.input.strings.get(General.input.index).equals(","))) break;
                    else ++General.input.index;
                }

                TreeSet<Object> set = new TreeSet<>();
                while (General.input.strings.size() - General.input.index > 0) {
                    Node node = getBase(false);
                    if (node == null || node.data == null) {
                        System.out.println("Error 1488");
                        return false;
                    }

                    if (data.getClass() == node.data.getClass()) {
                        set.addAll(node.data.data);
                    } else {
                        System.out.println("Error 1337");
                        return false;
                    }

                    if (General.input.strings.size() - General.input.index > 1 && General.input.strings.get(General.input.index).equals(",")) ++General.input.index;
                    else break;
                } data.append(set);
            }
            default -> {
                if (valueType == 0) {
                    System.out.println("Error not command: " + name);
                    return false;
                }
            }
        } boolean backCopy = true;

        while (General.input.strings.size() - General.input.index > 1) {
            if (data == null || data.data == null || data.data.isEmpty()) {
                System.out.println(Colors.ANSI_RED + "error data is empty" + Colors.ANSI_RESET);
                return false;
            }

            boolean equals = false;
            if (General.input.strings.get(General.input.index).equals("=")){
                if (valueType != 2) {
                    System.out.println("Error not value align: " + name);
                    return false;
                } equals = true;
            } else if (!General.input.strings.get(General.input.index).equals(">")) break;
            ++General.input.index;

            Input.TwoDate twoDate = General.input.getTwoDate();
            if (twoDate == null) return false;

            if (equals) {
                map.put(name, data);
                backCopy = true;
            }

            switch (General.input.strings.get(General.input.index)) {
                case "out" -> {
                    System.out.println(Colors.ANSI_CYAN + "values count: " + data.data.size() + Colors.ANSI_RESET);
                    if (twoDate.one == 0) data.outConsole();
                    else data.outConsole(twoDate.one);
                }
                case "count" -> { System.out.println(Colors.ANSI_CYAN + Integer.toString(data.data.size()) + Colors.ANSI_RESET); }
                case "history" -> {
                    if (General.input.strings.size() - General.input.index < 2) {
                        System.out.println("Error not command of history");
                        return false;
                    }

                    if (data instanceof Users users) {
                        switch (General.input.strings.get(++General.input.index)) {
                            case "friends" -> { users.outHistoryIds(UserIDsEnum.FRIENDS.ordinal(), twoDate.one); }
                            case "online" -> { users.outOnlineHistory(); }
                            default -> {
                                System.out.println("Error not command history: " + General.input.strings.get(General.input.index));
                                return false;
                            }
                        }
                    }
                }
                case "contain" -> {
                    Boolean generate = General.input.getGenerate();
                    if (generate == null) return false;

                    if (General.input.strings.size() - General.input.index < 2) {
                        System.out.println(Colors.ANSI_RED + "Error not contain argument" + Colors.ANSI_RESET);
                        return false;
                    }

                    switch (data) {
                        case Users users -> {
                            int type = switch (General.input.strings.get(++General.input.index)) {
                                case "friends" -> 0;
                                case "subscribers" -> 1;
                                case "groups" -> 2;
                                default -> -1;
                            };

                            if (type == -1) {
                                System.out.println(Colors.ANSI_RED + "Error not contain argument: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                                return false;
                            }

                            if (General.input.strings.size() - General.input.index < 2) {
                                System.out.println(Colors.ANSI_RED + "Error not contain data" + Colors.ANSI_RESET);
                                return false;
                            } ++General.input.index;

                            ArrayList<Integer> ids;
                            if (type != 2) {
                                if (Utils.isInteger(General.input.strings.get(General.input.index))) {
                                    ids = General.input.getIntegers();
                                    if (ids == null) return false;
                                } else {
                                    Node node = getBase(false);
                                    if (node == null || !(node.data instanceof Users)) {
                                        System.out.println(Colors.ANSI_RED + "Error contain data is empty or data is not users" + Colors.ANSI_RESET);
                                        return false;
                                    } ids = node.data.data;
                                }


                                if (generate) {
                                    //Доделать
                                } else {
                                    if (twoDate.isDate()) {
                                        if (twoDate.range()) users.contain(type, ids, twoDate.one, twoDate.two);
                                        else users.contain(type, ids, twoDate.one);
                                    } else users.contain(type, ids);
                                }
                            } else {
                                //Доделать
                            }
                        }
                        case Groups groups -> {

                        }
                        default -> {
                            System.out.println(Colors.ANSI_RED + "Error not contain in chain" + Colors.ANSI_RESET);
                            return false;
                        }
                    }
                }
                default -> {
                    if (!equals && valueType != 2) {
                        if (!backCopy) {
                            data = data.copy();
                            backCopy = true;
                        } else backCopy = false;
                    }

                    if (General.input.strings.get(General.input.index).equals("chain")) {
                        if (data instanceof Users users) {
                            ++General.input.index;
                            ArrayList<Chains.Base> chains = new ArrayList<>();

                            while (General.input.strings.size() - General.input.index > 2) {
                                Boolean generate = General.input.getGenerate();
                                if (generate == null) return false;
                                ArrayList<Integer> in;
                                ArrayList<Integer> to;

                                if (Utils.isInteger(General.input.strings.get(General.input.index))) {
                                    in = General.input.getIntegers();
                                    if (in == null) return false;
                                } else {
                                    String nameIn = General.input.strings.get(General.input.index);
                                    Node node = getBase(true);
                                    if (node == null || !(node.data instanceof Users)) {
                                        System.out.println("Error not value or value is not users in chain in: " + nameIn);
                                        return false;
                                    } in = new ArrayList<>(node.data.data);
                                }

                                if (!General.input.strings.get(General.input.index).equals("to")) {
                                    System.out.println("Error not to in chain");
                                    return false;
                                } ++General.input.index;

                                if (Utils.isInteger(General.input.strings.get(General.input.index))) {
                                    to = General.input.getIntegers();
                                    if (to == null) return false;
                                } else {
                                    String nameTo = General.input.strings.get(General.input.index);
                                    Node node = getBase(true);
                                    if (node == null || !(node.data instanceof Users)) {
                                        System.out.println("Error not value or value is not users in chain to: " + nameTo);
                                        return false;
                                    } to = new ArrayList<>(node.data.data);
                                }

                                TreeSet<Integer> set = new TreeSet<>(in);
                                set.addAll(to);

                                if (set.size() < in.size() + to.size()) {
                                    System.out.println("Error in and to equals elements");
                                    return false;
                                }

                                Chains.Base chain = ((generate) ? new Chains.ChainGenerateUsers(in, to, twoDate.one, data.data) : new Chains.ChainUsers(in, to, twoDate.one, data.data));
                                chains.add(chain);
                                chain.start();

                                if (General.input.strings.size() - General.input.index > 0 && General.input.strings.get(General.input.index).equals(",")) ++General.input.index;
                                else break;
                            }

                            for (Chains.Base element : chains)
                                element.join();

                            chains.removeIf(s -> s.data == null);
                            data = new org.example.DB.Chains(chains);
                            if (equals) {
                                map.put(name, data);
                                backCopy = true;
                            } --General.input.index;
                        } else {
                            System.out.println("Error data not users");
                            return false;
                        }
                    } else {
                        if (!equals && valueType != 2) {
                            if (!backCopy) {
                                data = data.copy();
                                backCopy = true;
                            } else backCopy = false;
                        }

                        switch (General.input.strings.get(General.input.index)) {
                            case "probability" -> {
                                if (data instanceof Users users) {
                                    ++General.input.index;
                                    Input.Probability probability = General.input.getProbability();
                                    if (probability == null) return false;

                                    if (General.input.strings.size() - General.input.index > 1) {
                                        switch (General.input.strings.get(General.input.index)) {
                                            case "friends" -> {
                                                if (probability.generate()) {
                                                    if (twoDate.isDate()) {
                                                        if (twoDate.range()) users.probabilityFriendsGenerateTwoDate(probability.percent(), General.threadCount, twoDate.one, twoDate.two);
                                                        else users.probabilityFriendsGenerateDate(probability.percent(), General.threadCount, twoDate.one);
                                                    } else users.filterProbabilityGenerate(GenerateIDsEnum.FRIENDS.ordinal(), GenerateIDsEnum.FRIENDS.ordinal(), probability.percent(), General.threadCount);
                                                } else {
                                                    if (twoDate.isDate()) {
                                                        if (twoDate.range()) users.filterProbabilityFrinedsTwoDate(probability.percent(), General.threadCount, twoDate.one, twoDate.two);
                                                        else users.filterProbabilityFriendsDate(probability.percent(), General.threadCount, twoDate.one);
                                                    } else users.filterProbabilityIds(GenerateIDsEnum.FRIENDS.ordinal(), GenerateIDsEnum.FRIENDS.ordinal(), probability.percent(), General.threadCount);
                                                }
                                            }
                                            case "subscribers" -> {

                                            }
                                            case "groups" -> {

                                            }
                                            default -> {
                                                System.out.println(Colors.ANSI_RED + "Error not probability argument: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                                                return false;
                                            }
                                        }
                                    } else {
                                        System.out.println(Colors.ANSI_RED + "Error not probability argument" + Colors.ANSI_RESET);
                                        return false;
                                    }
                                } else {
                                    System.out.println(Colors.ANSI_RED + "Error not probability no user data" + Colors.ANSI_RESET);
                                    return false;
                                }
                            }
                            case "filter" -> {
                                if (General.input.strings.size() - General.input.index < 3) {
                                    System.out.println("Error not command of filter");
                                    return false;
                                }

                                switch (General.input.strings.get(++General.input.index)) {
                                    case "online" -> {
                                        if (data instanceof Users users) {
                                            if (General.input.strings.size() - General.input.index < 1) {
                                                System.out.println("error online not yes or no");
                                                return false;
                                            }

                                            switch (General.input.strings.get(++General.input.index)) {
                                                case "yes" -> {
                                                    if (twoDate.range()) users.filterOnline(true, twoDate.one, twoDate.two);
                                                    else users.filterOnline(twoDate.one);
                                                }
                                                case "no" -> {
                                                    if (twoDate.range()) users.filterOnline(false, twoDate.one, twoDate.two);
                                                    else users.filterNoOnline(twoDate.one);
                                                }
                                                default -> {
                                                    System.out.println("Error online type: " + General.input.strings.get(General.input.index));
                                                    return false;
                                                }
                                            }
                                        } else {
                                            System.out.println("Error online is not users");
                                            return false;
                                        }
                                    }
                                    case "bdate" -> {
                                        if (data instanceof Users users) {
                                            if (General.input.strings.get(++General.input.index).equals("range")) {
                                                if (General.input.strings.size() - General.input.index > 2) {
                                                    if (Utils.isBDate(General.input.strings.get(++General.input.index)) && Utils.isBDate(General.input.strings.get(General.input.index + 1))) {
                                                        int one = Utils.addBDate(General.input.strings.get(General.input.index));
                                                        int two = Utils.addBDate(General.input.strings.get(++General.input.index));

                                                        if (Utils.isCorrectBDateRange(one, two)) {
                                                            if (!twoDate.range()) {
                                                                if (twoDate.isDate()) users.filterBDate(one, two, twoDate.one);
                                                                else users.filterBDate(one, two);
                                                            } else users.filterBDate(one, two, twoDate.one, twoDate.two);
                                                        } else {
                                                            System.out.println(Colors.ANSI_RED + "Error not correct bDate range" + Colors.ANSI_RESET);
                                                            return false;
                                                        }
                                                    } else {
                                                        System.out.println(Colors.ANSI_RED + "Error bDate range format" + Colors.ANSI_RESET);
                                                        return false;
                                                    }
                                                } else {
                                                    System.out.println(Colors.ANSI_RED + "Error not bDate range" + Colors.ANSI_RESET);
                                                    return false;
                                                }
                                            } else {
                                                if (!Utils.isBDate(General.input.strings.get(General.input.index))) return false;
                                                int bdate = Utils.addBDate(General.input.strings.get(General.input.index));
                                                users.filterId(UserIDEnum.BDATE.ordinal(), bdate, twoDate.one);
                                            }
                                        } else {
                                            System.out.println("Error syntax bdate");
                                            return false;
                                        }
                                    }
                                    case "name" -> {
                                        if (data instanceof Users users) {
                                            ++General.input.index;
                                            int percent = General.input.getPercent();
                                            if (percent < 0) return false;

                                            int type = switch (General.input.strings.get(General.input.index)) {
                                                case "first" -> UserIDEnum.FIRST_NAME.ordinal();
                                                case "last" -> UserIDEnum.LAST_NAME.ordinal();
                                                case "nick" -> UserIDEnum.NICK_NAME.ordinal();
                                                case "status" -> UserIDEnum.STATUS.ordinal();
                                                case "domain" -> UserIDEnum.DOMAIN.ordinal();
                                                default -> -1;
                                            };

                                            if (type == -1) {
                                                System.out.println(Colors.ANSI_RED + "Error not name: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                                                return false;
                                            } ++General.input.index;

                                            ArrayList<String> strings = General.input.getStrings();
                                            if (strings == null) return false;
                                            if (twoDate.range()) {
                                                if (percent == 100) users.filterName(strings, type, twoDate.one, twoDate.two);
                                                else users.filterName(strings, type, twoDate.one, twoDate.two, percent, General.threadCount);
                                            } else users.filterName(type, strings, percent, twoDate.one, General.threadCount);
                                            --General.input.index;
                                        }
                                    }
                                    case "names" -> {
                                        if (data instanceof Users users) {
                                            ++General.input.index;
                                            int percent = General.input.getPercent();

                                            ArrayList<Input.Names> buffer = General.input.getNames();
                                            if (buffer == null) return false;
                                            if (twoDate.range()) {
                                                if (percent == 100) users.filterNames(buffer, twoDate.one, twoDate.two);
                                                else users.filterNames(buffer, twoDate.one, twoDate.two, percent, General.threadCount);
                                            } else users.filterNames(buffer, percent, twoDate.one, General.threadCount);
                                            --General.input.index;
                                        }
                                    }
                                    case "id" -> {
                                        if (data instanceof Users users) {
                                            int id = Integer.parseInt(General.input.strings.get(General.input.index + 2));
                                            switch (General.input.strings.get(General.input.index + 1)) {
                                                case "city" -> {
                                                    if (twoDate.range()) users.filterId(id, UserIDEnum.CITY.ordinal(), twoDate.one, twoDate.two);
                                                    else users.filterId(UserIDEnum.CITY.ordinal(), id, twoDate.one);
                                                }
                                                case "sex" -> {
                                                    if (twoDate.range()) users.filterId(id, UserIDEnum.SEX.ordinal(), twoDate.one, twoDate.two);
                                                    else users.filterId(UserIDEnum.SEX.ordinal(), id, twoDate.one);
                                                }
                                                case "type" -> {
                                                    if (twoDate.range()) users.filterId(id, UserIDEnum.TYPE.ordinal(), twoDate.one, twoDate.two);
                                                    else users.filterId(UserIDEnum.TYPE.ordinal(), id, twoDate.one);
                                                }
                                                default -> {
                                                    System.out.println("Error not filter in: " + General.input.strings.get(General.input.index + 1));
                                                    return false;
                                                }
                                            } General.input.index += 2;
                                        }

                                    }
                                    case "idGenerate" -> {
                                        if (data instanceof Users users) {
                                            ++General.input.index;
                                            Input.GenerateAndLevel generateAndLevel = General.input.getGenerateAndLevel();
                                            if (generateAndLevel == null) return false;

                                            int id;
                                            try {
                                                id = Integer.parseInt(General.input.strings.get(General.input.index + 1));
                                            } catch (NumberFormatException e) {
                                                System.out.println("Error unconvert id: " + General.input.strings.get(General.input.index + 1));
                                                return false;
                                            }

                                            switch (General.input.strings.get(General.input.index)) {
                                                case "city" -> { users.filterIdGenerate(UserIDEnum.CITY.ordinal(), id, generateAndLevel.generate(), generateAndLevel.level(), twoDate.one); }
                                                case "type" -> { users.filterIdGenerate(UserIDEnum.TYPE.ordinal(), id, generateAndLevel.generate(), generateAndLevel.level(), twoDate.one); }
                                                case "sex" -> { { users.filterIdGenerate(UserIDEnum.SEX.ordinal(), id, generateAndLevel.generate(), generateAndLevel.level(), twoDate.one); } }
                                                default -> {
                                                    System.out.println("Error is generate command: " + General.input.strings.get(General.input.index));
                                                    return false;
                                                }
                                            } ++General.input.index;
                                        } else {
                                            System.out.println("Error io element is not users");
                                            return false;
                                        }
                                    }
                                }
                            }
                            case "remove" -> {
                                if (General.input.strings.size() - General.input.index < 3) {
                                    System.out.println("Error not command of filter");
                                    return false;
                                }

                                switch (General.input.strings.get(++General.input.index)) {
                                    case "online" -> {
                                        if (data instanceof Users users) {
                                            if (General.input.strings.size() - General.input.index < 1) {
                                                System.out.println("error online not yes or no");
                                                return false;
                                            }

                                            switch (General.input.strings.get(++General.input.index)) {
                                                case "yes" -> {
                                                    if (twoDate.range()) users.removeOnline(true, twoDate.one, twoDate.two);
                                                    else users.removeOnline(twoDate.one);
                                                }
                                                case "no" -> {
                                                    if (twoDate.range()) users.removeOnline(false, twoDate.one, twoDate.two);
                                                    else users.removeNoOnline(twoDate.one);
                                                }
                                                default -> {
                                                    System.out.println("Error online type: " + General.input.strings.get(General.input.index));
                                                    return false;
                                                }
                                            }
                                        } else {
                                            System.out.println("Error online is not users");
                                            return false;
                                        }
                                    }
                                    case "bdate" -> {
                                        if (data instanceof Users users) {
                                            if (General.input.strings.get(++General.input.index).equals("range")) {
                                                if (General.input.strings.size() - General.input.index > 2) {
                                                    if (Utils.isBDate(General.input.strings.get(++General.input.index)) && Utils.isBDate(General.input.strings.get(General.input.index + 1))) {
                                                        int one = Utils.addBDate(General.input.strings.get(General.input.index));
                                                        int two = Utils.addBDate(General.input.strings.get(++General.input.index));

                                                        if (Utils.isCorrectBDateRange(one, two)) {
                                                            if (!twoDate.range()) {
                                                                if (twoDate.isDate()) users.removeBDate(one, two, twoDate.one);
                                                                else users.removeBDate(one, two);
                                                            } else users.removeBDate(one, two, twoDate.one, twoDate.two);
                                                        } else {
                                                            System.out.println(Colors.ANSI_RED + "Error not correct bDate range" + Colors.ANSI_RESET);
                                                            return false;
                                                        }
                                                    } else {
                                                        System.out.println(Colors.ANSI_RED + "Error bDate range format" + Colors.ANSI_RESET);
                                                        return false;
                                                    }
                                                } else {
                                                    System.out.println(Colors.ANSI_RED + "Error not bDate range" + Colors.ANSI_RESET);
                                                    return false;
                                                }
                                            } else {
                                                if (!Utils.isBDate(General.input.strings.get(General.input.index))) return false;
                                                int bdate = Utils.addBDate(General.input.strings.get(General.input.index));
                                                users.filterIdRemove(UserIDEnum.BDATE.ordinal(), bdate, twoDate.one);
                                            }
                                        } else {
                                            System.out.println("Error syntax bdate");
                                            return false;
                                        }
                                    }
                                    case "name" -> {
                                        if (data instanceof Users users) {
                                            ++General.input.index;
                                            int percent = General.input.getPercent();
                                            if (percent < 0) return false;

                                            int type = switch (General.input.strings.get(General.input.index)) {
                                                case "first" -> UserIDEnum.FIRST_NAME.ordinal();
                                                case "last" -> UserIDEnum.LAST_NAME.ordinal();
                                                case "nick" -> UserIDEnum.NICK_NAME.ordinal();
                                                case "status" -> UserIDEnum.STATUS.ordinal();
                                                case "domain" -> UserIDEnum.DOMAIN.ordinal();
                                                default -> -1;
                                            };

                                            if (type == -1) {
                                                System.out.println("Error not name: " + General.input.strings.get(General.input.index));
                                                return false;
                                            } ++General.input.index;

                                            ArrayList<String> strings = General.input.getStrings();
                                            if (strings == null) return false;
                                            if (twoDate.range()) {
                                                if (percent == 100) users.filterName(strings, type, twoDate.one, twoDate.two);
                                                else users.filterName(strings, type, twoDate.one, twoDate.two, percent, General.threadCount);
                                            } else users.filterName(type, strings, percent, twoDate.one, General.threadCount);
                                            --General.input.index;
                                        }
                                    }
                                    case "names" -> {
                                        if (data instanceof Users users) {
                                            ++General.input.index;
                                            int percent = General.input.getPercent();
                                            if (percent < 0) return false;

                                            ArrayList<Input.Names> buffer = General.input.getNames();
                                            if (buffer == null) return false;
                                            if (twoDate.range()) {
                                                if (percent == 100) users.removeNames(buffer, twoDate.one, twoDate.two);
                                                else users.removeNames(buffer, twoDate.one, twoDate.two, percent, General.threadCount);
                                            } else users.removeNames(buffer, percent, twoDate.one, General.threadCount);
                                            --General.input.index;
                                        }
                                    }
                                    case "id" -> {
                                        if (data instanceof Users users) {
                                            int id = Integer.parseInt(General.input.strings.get(General.input.index + 2));
                                            switch (General.input.strings.get(General.input.index + 1)) {
                                                case "city" -> {
                                                    if (twoDate.range()) users.removeId(id, UserIDEnum.CITY.ordinal(), twoDate.one, twoDate.two);
                                                    else users.filterIdRemove(UserIDEnum.CITY.ordinal(), id, twoDate.one);
                                                }
                                                case "sex" -> {
                                                    if (twoDate.range()) users.removeId(id, UserIDEnum.SEX.ordinal(), twoDate.one, twoDate.two);
                                                    else users.filterIdRemove(UserIDEnum.SEX.ordinal(), id, twoDate.one);
                                                }
                                                case "type" -> {
                                                    if (twoDate.range()) users.removeId(id, UserIDEnum.TYPE.ordinal(), twoDate.one, twoDate.two);
                                                    else users.filterIdRemove(UserIDEnum.TYPE.ordinal(), id, twoDate.one);
                                                }
                                                default -> {
                                                    System.out.println("Error not filter in: " + General.input.strings.get(General.input.index + 1));
                                                    return false;
                                                }
                                            } General.input.index += 2;
                                        }
                                    }
                                    case "idGenerate" -> {
                                        if (data instanceof Users users) {
                                            ++General.input.index;
                                            Input.GenerateAndLevel generateAndLevel = General.input.getGenerateAndLevel();
                                            if (generateAndLevel == null) return false;

                                            int id;
                                            try {
                                                id = Integer.parseInt(General.input.strings.get(General.input.index + 1));
                                            } catch (NumberFormatException e) {
                                                System.out.println("Error unconvert id: " + General.input.strings.get(General.input.index + 1));
                                                return false;
                                            }

                                            switch (General.input.strings.get(General.input.index)) {
                                                case "city" -> { users.removeIdGenerate(UserIDEnum.CITY.ordinal(), id, generateAndLevel.generate(), generateAndLevel.level(), twoDate.one); }
                                                case "type" -> { users.removeIdGenerate(UserIDEnum.TYPE.ordinal(), id, generateAndLevel.generate(), generateAndLevel.level(), twoDate.one); }
                                                case "sex" -> { { users.removeIdGenerate(UserIDEnum.SEX.ordinal(), id, generateAndLevel.generate(), generateAndLevel.level(), twoDate.one); } }
                                                default -> {
                                                    System.out.println(Colors.ANSI_RED + "Error is generate command: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                                                    return false;
                                                }
                                            } ++General.input.index;
                                        } else {
                                            System.out.println(Colors.ANSI_RED + "Error io element is not users" + Colors.ANSI_RESET);
                                            return false;
                                        }
                                    }
                                }
                            }
                            case "chain" -> {

                            }
                            case "general" -> {
                                ++General.input.index;
                                Boolean generate = General.input.getGenerate();
                                if (generate == null) return false;

                                if (data instanceof Users users) {
                                    switch (General.input.strings.get(General.input.index)) {
                                        case "friends" -> {
                                            users.generalIds(GenerateIDsEnum.FRIENDS.ordinal(), UserIDsEnum.FRIENDS.ordinal(), generate, twoDate.one);
                                        }
                                        case "subscribers" -> {
                                            users.generalIds(GenerateIDsEnum.SUBSCRIBERS.ordinal(), UserIDsEnum.SUBSCRIBERS.ordinal(), generate, twoDate.one);
                                        }
                                        case "groups" -> {
                                            users.generalIds(GenerateIDsEnum.GROUPS.ordinal(), UserIDsEnum.GROUPS.ordinal(), generate, twoDate.one);
                                            data = new Groups(users.data);
                                            if (equals) {
                                                map.put(name, data);
                                            }
                                        }
                                        default -> {
                                            System.out.println("Error not general command: " + General.input.strings.get(General.input.index));
                                            return false;
                                        }
                                    }
                                }
                            }
                            default -> {
                                System.out.println("Error not command io: " + General.input.strings.get(General.input.index));
                                return false;
                            }
                        }
                    }
                }
            } ++General.input.index;
        } return true;
    }
}
