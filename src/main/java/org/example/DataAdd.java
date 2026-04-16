package org.example;

import com.google.gson.JsonObject;
import org.example.Data.IDHistory;
import org.example.Data.IDsHistory;
import org.example.Data.OnlineHistory;
import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.TypeEnum;
import org.example.Enum.UserIDEnum;
import org.example.Enum.UserIDsEnum;
import org.example.VKData.UserDB;
import org.example.VKRequest.RequestsGet;
import org.example.VKRequest.RequestsGetID;

import java.util.TreeSet;

import static org.example.Utils.addBDate;

public class DataAdd {
    public static final String[] names = { "first_name", "last_name",  "nickname", "status" };

    public static void addDomain(UserDB userDB, long date, String str, int id) {
        if (str.isEmpty()) return;
        if (str.startsWith("id")) {
            for (char element : str.substring(2).toCharArray())
                if (!Character.isDigit(element)) addName(userDB, date, str, UserIDEnum.DOMAIN.ordinal(), id);
            return;
        } addName(userDB, date, str, UserIDEnum.DOMAIN.ordinal(), id);
    }



    private static void addName(UserDB userDB, long date, String str, int index, int id) {
        if (str.isEmpty()) return;
        if (userDB.idHistories[index] == null) userDB.idHistories[index] = new IDHistory();

        int ind = General.userStrings[index].add(str);
        userDB.idHistories[index].update(date, ind);
        General.idGenerateUsers[index].computeIfAbsent(ind, s -> new TreeSet<>()).add(id);
    }

    public static int addUser(long date, JsonObject jsonObject) {
        int id = jsonObject.get("id").getAsInt();
        UserDB userDB = General.users.computeIfAbsent(id, s -> new UserDB());
        if (userDB.idHistories == null) userDB.idHistories = new IDHistory[UserIDEnum.values().length];

        //ОткладОЧКА
        //System.out.println(jsonObject);

        if (!jsonObject.has("deactivated")) {
            for (int index = 0; index < 4; ++index)
                if (jsonObject.has(names[index]))
                    addName(userDB, date, jsonObject.get(names[index]).getAsString(), index, id);

            if (jsonObject.has("bdate")) {
                if (userDB.idHistories[UserIDEnum.BDATE.ordinal()] == null) userDB.idHistories[UserIDEnum.BDATE.ordinal()] = new IDHistory();
                int bdate = addBDate(jsonObject.get("bdate").getAsString());
                General.idGenerateUsers[UserIDEnum.BDATE.ordinal()].computeIfAbsent(bdate, s -> new TreeSet<>()).add(id);
                userDB.idHistories[UserIDEnum.BDATE.ordinal()].update(date, bdate);
            }

            if (jsonObject.has("city")) {
                if (userDB.idHistories[UserIDEnum.CITY.ordinal()] == null) userDB.idHistories[UserIDEnum.CITY.ordinal()] = new IDHistory();
                int city = jsonObject.get("city").getAsJsonObject().get("id").getAsInt();
                General.idGenerateUsers[UserIDEnum.CITY.ordinal()].computeIfAbsent(city, s -> new TreeSet<>()).add(id);
                userDB.idHistories[UserIDEnum.CITY.ordinal()].update(date, city);
            }

            if (jsonObject.has("is_closed")) {
                if (userDB.idHistories[UserIDEnum.TYPE.ordinal()] == null) userDB.idHistories[UserIDEnum.TYPE.ordinal()] = new IDHistory();
                int type = (jsonObject.get("is_closed").getAsBoolean() ? TypeEnum.PRIVATE.ordinal() : TypeEnum.PUBLIC.ordinal());
                General.idGenerateUsers[UserIDEnum.TYPE.ordinal()].computeIfAbsent(type, s -> new TreeSet<>()).add(id);
                userDB.idHistories[UserIDEnum.TYPE.ordinal()].update(date, type);
            }

            if (jsonObject.has("online")) {
                if (jsonObject.get("online").getAsInt() == 1) {
                    if (userDB.onlineHistory == null) userDB.onlineHistory = new OnlineHistory();
                    userDB.onlineHistory.update(date, 0, (jsonObject.has("online_app")) ? jsonObject.get("online_app").getAsInt() : 0);
                } else {
                    if (jsonObject.has("last_seen")) {
                        if (userDB.onlineHistory == null) userDB.onlineHistory = new OnlineHistory();
                        userDB.onlineHistory.update(
                                date, (jsonObject.has("last_seen")) ? jsonObject.get("last_seen").getAsJsonObject().get("time").getAsLong() : date,
                                ((jsonObject.get("last_seen").getAsJsonObject().has("platform")) ?jsonObject.get("last_seen").getAsJsonObject().get("platform").getAsInt() : 0)
                        );
                    }
                }
            }

            if (jsonObject.has("mobile_phone")) {
                String str = jsonObject.get("mobile_phone").getAsString();
                if (!str.isEmpty()) userDB.addPhoneNumber(str);
            }
        } else {
            if (userDB.idHistories[UserIDEnum.TYPE.ordinal()] == null) userDB.idHistories[UserIDEnum.TYPE.ordinal()] = new IDHistory();
            int type = (jsonObject.get("deactivated").getAsString().equals("banned") ? TypeEnum.BANNED.ordinal() : TypeEnum.DELETED.ordinal());
            General.idGenerateUsers[UserIDEnum.TYPE.ordinal()].computeIfAbsent(type, s -> new TreeSet<>()).add(id);
            userDB.idHistories[UserIDEnum.TYPE.ordinal()].update(date, type);
        }

        if (jsonObject.has("domain")) addDomain(userDB, date, jsonObject.get("domain").getAsString(), id);



        if (jsonObject.has("sex")) {
            if (userDB.idHistories[UserIDEnum.SEX.ordinal()] == null) userDB.idHistories[UserIDEnum.SEX.ordinal()] = new IDHistory();
            int sex = jsonObject.get("sex").getAsInt();
            General.idGenerateUsers[UserIDEnum.SEX.ordinal()].computeIfAbsent(sex, s -> new TreeSet<>()).add(id);
            userDB.idHistories[UserIDEnum.SEX.ordinal()].update(date, sex);
        }









        return id;
    }

    public static int[] addUsers(RequestsGet requestsGet) {
        TreeSet<Integer> buffer = new TreeSet<>();
        for (RequestsGet.Node element : requestsGet.data)
            for (int index = 0; index < element.data.size(); ++index)
                buffer.add(addUser(element.date, element.data.get(index).getAsJsonObject()));
        return ((buffer.isEmpty()) ? null : buffer.stream().mapToInt(Integer::intValue).toArray());
    }

    public static int[] addFriends(RequestsGetID data) {
        int[] array = addUsers(data);
        if (data.full) {
            UserDB userDB = General.users.computeIfAbsent(data.id, s -> new UserDB());
            if (userDB.iDsHistories == null) userDB.iDsHistories = new IDsHistory[UserIDsEnum.values().length];
            if (userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] == null) userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()] = new IDsHistory();
            userDB.iDsHistories[UserIDsEnum.FRIENDS.ordinal()].update(data.date, array);
        }

        if (array != null) {
            TreeSet<Integer> set = General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].computeIfAbsent(data.id, s -> new TreeSet<>());
            for (int id : array) {
                General.generateIds[GenerateIDsEnum.FRIENDS.ordinal()].computeIfAbsent(id, s -> new TreeSet<>()).add(data.id);
                set.add(id);
            }
        } return array;
    }

    public static int[] addSubscribers(RequestsGetID data) {
        int[] array = addUsers(data);
        if (data.full) {
            UserDB userDB = General.users.computeIfAbsent(data.id, s -> new UserDB());
            if (userDB.iDsHistories == null) userDB.iDsHistories = new IDsHistory[UserIDsEnum.values().length];
            if (userDB.iDsHistories[UserIDsEnum.SUBSCRIBERS.ordinal()] == null) userDB.iDsHistories[UserIDsEnum.SUBSCRIBERS.ordinal()] = new IDsHistory();
            userDB.iDsHistories[UserIDsEnum.SUBSCRIBERS.ordinal()].update(data.date, array);

            if (array != null) {
                TreeSet<Integer> set = General.generateIds[GenerateIDsEnum.SUBSCRIBERS.ordinal()].computeIfAbsent(data.id, s -> new TreeSet<>());
                for (int id : array) {
                    General.generateIds[GenerateIDsEnum.SUBSCRIBERS_IN.ordinal()].computeIfAbsent(id, s -> new TreeSet<>()).add(data.id);
                    set.add(id);
                }
            }
        } return array;
    }
}
