package org.example.Console;

import org.example.Colors;
import org.example.DB.Users;
import org.example.DataRemove;
import org.example.Enum.UserIDEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.IO;
import org.example.VKData.UserDB;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class DataBase {
    public static boolean run() throws IOException, InterruptedException {
        if (General.input.strings.size() - General.input.index < 2) {
            System.out.println(Colors.ANSI_RED + "Error not dataBase command" + Colors.ANSI_RESET);
            return false;
        } ++General.input.index;

        switch(General.input.strings.get(General.input.index)) {
            case "load" -> {
                ++General.input.index;
                ArrayList<String> strings = General.input.getStrings();
                if (strings == null) return false;
                ArrayList<DataInputStream> dataInputStreams = new ArrayList<>();

                for (String element : strings) {
                    try {
                        dataInputStreams.add(new DataInputStream(new FileInputStream(element)));
                    } catch (FileNotFoundException e) {
                        for (DataInputStream dataInputStream : dataInputStreams)
                            dataInputStream.close();

                        System.out.println(Colors.ANSI_RED + "Error not file: " + element + Colors.ANSI_RESET);
                        return false;
                    }
                }

                for (DataInputStream element : dataInputStreams) {
                    IO.load(element);
                    element.close();
                }
            }
            case "save" -> {
                if (General.input.strings.size() - General.input.index < 1) {
                    System.out.println(Colors.ANSI_RED + "Error not file name in save" + Colors.ANSI_RESET);
                    return false;
                } ++General.input.index;

                try {
                    DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(General.input.strings.get(General.input.index)));
                    IO.save(dataOutputStream);
                    dataOutputStream.close();
                } catch (IOException | InterruptedException e) {
                    System.out.println(Colors.ANSI_RED + e.toString() + Colors.ANSI_RESET);
                    return false;
                }
            }
            case "info" -> {
                int[] userId = new int[UserIDEnum.values().length - General.userStringCount];
                int[] userIds = new int[UserIDsEnum.values().length];
                int onlineHistoryCount = 0;
                int phone = 0, phoneIn = 0;

                for (Map.Entry<Integer, UserDB> entry : General.users.entrySet()) {
                    if (entry.getValue().idHistories != null)
                        for (int index = General.userStringCount; index < UserIDEnum.values().length; ++index)
                            if (entry.getValue().idHistories[index] != null) ++userId[index - General.userStringCount];

                    if (entry.getValue().iDsHistories != null)
                        for (int index = 0; index < UserIDsEnum.values().length; ++index)
                            if (entry.getValue().iDsHistories[index] != null) ++userIds[index];
                    if (entry.getValue().onlineHistory != null) ++onlineHistoryCount;

                    if (entry.getValue().phoneNumberLong != -1) {
                        if (entry.getValue().phoneNumberLong == 0) ++phoneIn;
                        else ++phone;
                    }
                }

                System.out.println(
                        "Users count: " + Integer.toString(General.users.size())
                        + "\n\tFirstName: " + Integer.toString(General.userStrings[UserIDsEnum.FRIENDS.ordinal()].strings.size())
                        + "\n\tLastName: " + Integer.toString(General.userStrings[UserIDEnum.LAST_NAME.ordinal()].strings.size())
                        + "\n\tNickName: " + Integer.toString(General.userStrings[UserIDEnum.NICK_NAME.ordinal()].strings.size())
                                + "\n\tStatus: " + Integer.toString(General.userStrings[UserIDEnum.STATUS.ordinal()].strings.size())
                                + "\n\tDomain: " + Integer.toString(General.userStrings[UserIDEnum.DOMAIN.ordinal()].strings.size())
                                + "\n\tOnline: " + Integer.toString(onlineHistoryCount)
                                + "\n\tType: " + Integer.toString(userId[UserIDEnum.TYPE.ordinal() - General.userStringCount])
                                + "\n\tSex: " + Integer.toString(userId[UserIDEnum.SEX.ordinal() - General.userStringCount])
                                + "\n\tBdate: " + Integer.toString(userId[UserIDEnum.BDATE.ordinal() - General.userStringCount])
                                + "\n\tCity: " + Integer.toString(userId[UserIDEnum.CITY.ordinal() - General.userStringCount])
                                + "\n\tFriends: " + Integer.toString(userIds[UserIDsEnum.FRIENDS.ordinal()])
                                + "\n\tSubscribers: " + Integer.toString(userIds[UserIDsEnum.SUBSCRIBERS.ordinal()])
                                + "\n\tGroups: " + Integer.toString(userIds[UserIDsEnum.GROUPS.ordinal()])
                                + "\n\tPhone: " + Integer.toString(phone)
                                + "\n\tPhoneInComplete: " + Integer.toString(phoneIn)

                                + "\nGroups count: " + Integer.toString(General.groups.size())
                );
            }
            case "remove" -> {
                if (General.input.strings.size() - General.input.index > 2) {
                    String command = General.input.strings.get(++General.input.index);
                    ++General.input.index;
                    String element = General.input.strings.get(General.input.index);
                    Other.Node base = Other.getBase(false);

                    if (base == null || base.data == null || base.data.data.isEmpty()) {
                        System.out.println(Colors.ANSI_RED + "Error not element: " + element +  "or empty" + Colors.ANSI_RESET);
                        return false;
                    }

                    switch (command) {
                        case "users" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeUsers(users.data);
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete users " + element + " Not users" + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "friends" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeUserIds(users.data, UserIDsEnum.FRIENDS.ordinal());
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete friends " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "groups" -> {

                        }
                        case "groupsUsers" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeUserIds(users.data, UserIDsEnum.GROUPS.ordinal());
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete groups users " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "subscribes" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeUserIds(users.data, UserIDsEnum.SUBSCRIBERS.ordinal());
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete subscribers " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "ids" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeUserIds(users.data);
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete ids " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "id" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeUserId(users.data);
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete id " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "onlineHistory" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removeOnlineHistory(users.data);
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete online history " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        case "phone" -> {
                            if (base.data instanceof Users users) {
                                DataRemove.removePhoneNumber(users.data);
                            } else {
                                System.out.println(Colors.ANSI_RED + "Error delete phone " + element + " Not users"  + Colors.ANSI_RESET);
                                return false;
                            }
                        }
                        default -> {
                            System.out.println(Colors.ANSI_RED + "Error not dataBase delete command: " + command + Colors.ANSI_RESET);
                            return false;
                        }
                    } --General.input.index;
                } else {
                    System.out.println(Colors.ANSI_RED + "Error not dataBase remove command" + Colors.ANSI_RESET);
                    return false;
                }
            }
            default -> {
                System.out.println(Colors.ANSI_RED + "Error not dataBase command: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                return false;
            }
        }

        ++General.input.index;
        return true;
    }
}
