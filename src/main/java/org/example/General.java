package org.example;

import org.example.Clients.VKTokens;
import org.example.Console.Input;
import org.example.Data.StringHistory;
import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.GroupIDEnum;
import org.example.Enum.UserIDEnum;
import org.example.Networks.Server.Server;
import org.example.VKData.GroupDB;
import org.example.VKData.UserDB;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class General {
    public final static int userStringCount = 5;
    public final static int groupStringCount = 2;

    public final static RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    public final static VKTokens vkTokens = new VKTokens();
    public final static Lock lock = new Lock();
    public static int threadCount = 4;
    public static final int port = 228;
    public static Server server = null;
    public static final Scanner scanner = new Scanner(System.in);
    public static final Input input = new Input();

    public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss:mm:HH dd/MM/yyyy");
    public static final String help =
            "dataBase { load { file, ... } / save { file } / info }" +
                    "\ntoken { add { id accessToken, ... } / remove { id, ... } / info }" +
                    "\nscan { --level -l, --rerty -r, --rertyTime -t, --tokens -t ( friends / users / groups / groupsBy) 228, ... }, ..." +
                    "\nhelp\nabout\nexit\n" +
                    "\nIO commands base argument" +
                    "\nusers, friends, groups, usersAll, friendsGenerate"
            ;

    public static final StringHistory[] userStrings = new StringHistory[userStringCount];
    public static final StringHistory[] groupStrings = new StringHistory[groupStringCount];

    public static final TreeMap<Integer, UserDB> users = new TreeMap<>();
    public static final TreeMap<Integer, GroupDB> groups = new TreeMap<>();

    public static final TreeMap<Integer, TreeSet<Integer>>[] idGenerateUsers = new TreeMap[UserIDEnum.values().length];
    public static final TreeMap<Integer, TreeSet<Integer>>[] idGenerateGroups = new TreeMap[GroupIDEnum.values().length];
    public static final TreeMap<Integer, TreeSet<Integer>>[] generateIds = new TreeMap[GenerateIDsEnum.values().length];

    public static void init(){
        for (int index = 0; index < userStringCount; ++index)
            userStrings[index] = new StringHistory();
        for (int index = 0; index < groupStringCount; ++index)
            groupStrings[index] = new StringHistory();

        for (int index = 0; index < UserIDEnum.values().length; ++index)
            idGenerateUsers[index] = new TreeMap<>();
        for (int index = 0; index < GroupIDEnum.values().length; ++index)
            idGenerateGroups[index] = new TreeMap<>();
        for (int index = 0; index < GenerateIDsEnum.values().length; ++index)
            generateIds[index] = new TreeMap<>();

        threadCount = Runtime.getRuntime().availableProcessors();
    }

    void clear() {
        users.clear();
        groups.clear();

        for (StringHistory element : userStrings)
            element.clear();
        for (StringHistory element : groupStrings)
            element.clear();

        for (TreeMap<Integer, TreeSet<Integer>> element : idGenerateUsers)
            element.clear();
        for (TreeMap<Integer, TreeSet<Integer>> element : idGenerateGroups)
            element.clear();
        for (TreeMap<Integer, TreeSet<Integer>> element : generateIds)
            element.clear();
    }
}
