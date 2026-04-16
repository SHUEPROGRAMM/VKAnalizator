package org.example.Algorithm;

import org.example.Enum.GenerateIDsEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.Utils;
import org.example.VKData.UserDB;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class ChainsUserIDGenerate {

    //Потоки для счёта id

    public static class IdGenerateGenerate extends Thread {
        public final int begin, end;
        public final int id, index;

        public final ArrayList<Integer> data;
        public final ArrayList<Integer> buffer = new ArrayList<>();

        public IdGenerateGenerate(int begin, int end, int id, int index, ArrayList<Integer> data) {
            this.begin = begin;
            this.end = end;
            this.id = id;
            this.index = index;
            this.data = data;
        }

        @Override
        public void run() {
            for (int index = begin; index < end; ++index) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserIdGenerate(data.get(index), GenerateIDsEnum.FRIENDS.ordinal(), this.index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result == id) buffer.add(data.get(index));
            }
        }
    }

    public static class IdGenerateGenerateDate extends IdGenerateGenerate {
        public final long date;

        public IdGenerateGenerateDate(int begin, int end, int id, int index, ArrayList<Integer> data, long date) {
            super(begin, end, id, index, data);
            this.date = date;
        }

        @Override
        public void run() {
            for (int index = begin; index < end; ++index) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserIdGenerate(data.get(index), GenerateIDsEnum.FRIENDS.ordinal(), this.index, date);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result == id) buffer.add(data.get(index));
            }
        }
    }

    public static class IdGenerate extends IdGenerateGenerate {
        public IdGenerate(int begin, int end, int id, int index, ArrayList<Integer> data) {
            super(begin, end, id, index, data);
        }

        @Override
        public void run() {
            for (int index = begin; index < end; ++index) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserId(data.get(index), UserIDsEnum.FRIENDS.ordinal(), this.index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result == id) buffer.add(data.get(index));
            }
        }
    }

    public static class IdGenerateDate extends IdGenerateGenerateDate {
        public IdGenerateDate(int begin, int end, int id, int index, ArrayList<Integer> data, long date) {
            super(begin, end, id, index, data, date);
        }

        @Override
        public void run() {
            for (int index = begin; index < end; ++index) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserId(data.get(index), UserIDsEnum.FRIENDS.ordinal(), this.index, date);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result == id) buffer.add(data.get(index));
            }
        }
    }

    //Функции фильтрации id

    public static ArrayList<Integer> getUserIdGenerateGenerateFilter(int id, int index, ArrayList<Integer> data) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        IdGenerateGenerate[] idGenerateGenerates = new IdGenerateGenerate[db.thread];
        ArrayList<Integer> buffer = new ArrayList<>();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            idGenerateGenerates[a] = new IdGenerateGenerate(db.begin, db.end, id, index, data);
            db.next1();
        }

        for (IdGenerateGenerate element : idGenerateGenerates)
            element.start();

        for (IdGenerateGenerate element : idGenerateGenerates) {
            element.join();
            buffer.addAll(element.buffer);
        } return buffer;
    }

    public static ArrayList<Integer> getUserIdGenerateFilter(int id, int index, ArrayList<Integer> data) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        IdGenerate[] idGenerates = new IdGenerate[db.thread];
        ArrayList<Integer> buffer = new ArrayList<>();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            idGenerates[a] = new IdGenerate(db.begin, db.end, id, index, data);
            db.next1();
        }

        for (IdGenerate element : idGenerates)
            element.start();

        for (IdGenerate element : idGenerates) {
            element.join();
            buffer.addAll(element.buffer);
        } return buffer;
    }

    public static ArrayList<Integer> getUserIdGenerateGenerateFilter(int id, int index, ArrayList<Integer> data, long date) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        IdGenerateDate[] idGenerateDates = new IdGenerateDate[db.thread];
        ArrayList<Integer> buffer = new ArrayList<>();
        General.lock.lock1();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            idGenerateDates[a] = new IdGenerateDate(db.begin, db.end, id, index, data, date);
            db.next1();
        }

        for (IdGenerateDate element : idGenerateDates)
            element.start();

        for (IdGenerateDate element : idGenerateDates) {
            element.join();
            buffer.addAll(element.buffer);
        }

        General.lock.unlock1();
        return buffer;
    }

    public static ArrayList<Integer> getUserIdGenerateFilter(int id, int index, ArrayList<Integer> data, long date) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        IdGenerateDate[] idGenerateDates = new IdGenerateDate[db.thread];
        ArrayList<Integer> buffer = new ArrayList<>();
        General.lock.lock1();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            idGenerateDates[a] = new IdGenerateDate(db.begin, db.end, id, index, data, date);
            db.next1();
        }

        for (IdGenerateDate element : idGenerateDates)
            element.start();

        for (IdGenerateDate element : idGenerateDates) {
            element.join();
            buffer.addAll(element.buffer);
        }

        General.lock.unlock0();
        return buffer;
    }

    //Класс хранения id

    public record IdDB (int userId, int id, TreeSet<Integer> data) {  }

    //Потоки верхнего уровня фильтрации id

    public static class GetIdGenerateGenerate extends Thread {
        public final ArrayList<Map.Entry<Integer, TreeSet<Integer>>> data;
        public final int index;
        public final int begin, end;

        public ArrayList<IdDB> buffer = new ArrayList<>();

        public GetIdGenerateGenerate(int begin, int end, int index, ArrayList<Map.Entry<Integer, TreeSet<Integer>>> data) {
            this.data = data;
            this.index = index;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            for (int a = begin; a < end; ++a) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserIdGenerate(data.get(a).getKey(), GenerateIDsEnum.FRIENDS.ordinal(), index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result >= -1) buffer.add(new IdDB(data.get(a).getKey(), result, data.get(a).getValue()));
            }
        }
    }

    public static class GetIdGenerateGenerateDate extends GetIdGenerateGenerate {
        public final long date;

        public GetIdGenerateGenerateDate(int begin, int end, int index, ArrayList<Map.Entry<Integer, TreeSet<Integer>>> data, long date) {
            super(begin, end, index, data);
            this.date = date;
        }

        @Override
        public void run() {
            for (int a = begin; a < end; ++a) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserIdGenerate(data.get(a).getKey(), GenerateIDsEnum.FRIENDS.ordinal(), index, date);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result >= -1) buffer.add(new IdDB(data.get(a).getKey(), result, data.get(a).getValue()));
            }
        }
    }

    public static class GetIdGenerate extends GetIdGenerateGenerate {
        public GetIdGenerate(int begin, int end, int index, ArrayList<Map.Entry<Integer, TreeSet<Integer>>> data) {
            super(begin, end, index, data);
        }

        @Override
        public void run() {
            for (int a = begin; a < end; ++a) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserId(data.get(a).getKey(), GenerateIDsEnum.FRIENDS.ordinal(), index);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result >= -1) buffer.add(new IdDB(data.get(a).getKey(), result, data.get(a).getValue()));
            }
        }
    }

    public static class GetIdGenerateDate extends GetIdGenerateGenerateDate {
        public GetIdGenerateDate(int begin, int end, int index, ArrayList<Map.Entry<Integer, TreeSet<Integer>>> data, long date) {
            super(begin, end, index, data, date);
        }

        @Override
        public void run() {
            for (int a = begin; a < end; ++a) {
                int result = 0;
                try {
                    result = IDUsersGenerate.getGenerateUserId(data.get(a).getKey(), GenerateIDsEnum.FRIENDS.ordinal(), index, date);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (result >= -1) buffer.add(new IdDB(data.get(a).getKey(), result, data.get(a).getValue()));
            }
        }
    }

    //Функция получения массива верхнего уровня

    public static ArrayList<IdDB> getUserIdGenerateGenerateLast(int index, TreeMap<Integer, TreeSet<Integer>> data) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        GetIdGenerateGenerate[] getIdGenerateGenerates = new GetIdGenerateGenerate[db.thread];
        ArrayList<Map.Entry<Integer, TreeSet<Integer>>> array = new ArrayList<>(data.entrySet());
        ArrayList<IdDB> buffer = new ArrayList<>();
        General.lock.lock1();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            getIdGenerateGenerates[a] = new GetIdGenerateGenerate(db.begin, db.end, index, array);
            db.next1();
        }

        for (GetIdGenerateGenerate element : getIdGenerateGenerates)
            element.start();

        for (GetIdGenerateGenerate element : getIdGenerateGenerates) {
            element.join();
            buffer.addAll(element.buffer);
        }

        General.lock.unlock1();
        return buffer;
    }

    public static ArrayList<IdDB> getUserIdGenerateGenerateLast(int index, TreeMap<Integer, TreeSet<Integer>> data, long date) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        GetIdGenerateGenerateDate[] getIdGenerateGenerateDates = new GetIdGenerateGenerateDate[db.thread];
        ArrayList<Map.Entry<Integer, TreeSet<Integer>>> array = new ArrayList<>(data.entrySet());
        ArrayList<IdDB> buffer = new ArrayList<>();
        General.lock.lock1();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            getIdGenerateGenerateDates[a] = new GetIdGenerateGenerateDate(db.begin, db.end, index, array, date);
            db.next1();
        }

        for (GetIdGenerateGenerateDate element : getIdGenerateGenerateDates)
            element.start();

        for (GetIdGenerateGenerateDate element : getIdGenerateGenerateDates) {
            element.join();
            buffer.addAll(element.buffer);
        }

        General.lock.unlock1();
        return buffer;
    }

    public static ArrayList<IdDB> getUserIdGenerateLast(int index, TreeMap<Integer, TreeSet<Integer>> data) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        GetIdGenerate[] getIdGenerates = new GetIdGenerate[db.thread];
        ArrayList<Map.Entry<Integer, TreeSet<Integer>>> array = new ArrayList<>(data.entrySet());
        ArrayList<IdDB> buffer = new ArrayList<>();
        General.lock.lock1();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            getIdGenerates[a] = new GetIdGenerate(db.begin, db.end, index, array);
            db.next1();
        }

        for (GetIdGenerate element : getIdGenerates)
            element.start();

        for (GetIdGenerate element : getIdGenerates) {
            element.join();
            buffer.addAll(element.buffer);
        }

        General.lock.unlock1();
        return buffer;
    }

    public static ArrayList<IdDB> getUserIdGenerateLast(int index, TreeMap<Integer, TreeSet<Integer>> data, long date) throws InterruptedException {
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(data.size(), General.threadCount);
        GetIdGenerateDate[] getIdGenerateDates = new GetIdGenerateDate[db.thread];
        ArrayList<Map.Entry<Integer, TreeSet<Integer>>> array = new ArrayList<>(data.entrySet());
        ArrayList<IdDB> buffer = new ArrayList<>();
        General.lock.lock1();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            getIdGenerateDates[a] = new GetIdGenerateDate(db.begin, db.end, index, array, date);
            db.next1();
        }

        for (GetIdGenerateDate element : getIdGenerateDates)
            element.start();

        for (GetIdGenerateDate element : getIdGenerateDates) {
            element.join();
            buffer.addAll(element.buffer);
        }

        General.lock.unlock1();
        return buffer;
    }

    //Поток преобразования массива карты id и количества

    public static class GetMaxThread extends Thread {
        public final ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> data;
        public final ArrayList<TreeSet<Integer>> indices;
        public final int begin, end;

        public final ArrayList<IdDB> buffer;

        public GetMaxThread(int begin, int end, ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> data, ArrayList<TreeSet<Integer>> indices) {
            this.data = data;
            this.begin = begin;
            this.end = end;
            this.indices = indices;
            this.buffer = new ArrayList<>(end - begin);
        }

        @Override
        public void run() {
            for (int a = begin; a < end; ++a) {
                Map.Entry<Integer, TreeMap<Integer, Integer>> temp = data.get(a);
                buffer.add(new IdDB(temp.getKey(), IDUsersGenerate.getMax(temp.getValue()), indices.get(a)));
            }
        }
    }

    //Фукция добавления индексов

    public static class GetIndices extends Thread {
        public final int begin, end;
        public final ArrayList<IdDB> data;
        public final TreeMap<Integer, TreeSet<Integer>> map;

        public final ArrayList<TreeSet<Integer>> buffer;

        public GetIndices(int begin, int end, ArrayList<IdDB> data, TreeMap<Integer, TreeSet<Integer>> map) {
            this.begin = begin;
            this.end = end;
            this.data = data;
            this.map = map;
            buffer = new ArrayList<>(end - begin);
        }

        @Override
        public void run() {
            for (int a = begin; a < end; ++a) {
                IdDB temp = data.get(a);
                TreeSet<Integer> set = new TreeSet<>();
                for (int element : temp.data)
                    set.addAll(map.get(element));
                buffer.add(set);
            }
        }
    }

    //Функиця преобразование верхнего уровня id к массиву карты нижнего уровня

    public static ArrayList<IdDB> transformation(final ArrayList<IdDB> data, final TreeMap<Integer, TreeSet<Integer>> map) throws InterruptedException {
        TreeMap<Integer, TreeMap<Integer, Integer>> maps = new TreeMap<>();
        ArrayList<TreeSet<Integer>> indices = new ArrayList<>();

        for (IdDB element : data) {
            for (int id : element.data) {
                TreeMap<Integer, Integer> temp = maps.computeIfAbsent(element.userId, s -> new TreeMap<>());
                temp.put(element.id, temp.getOrDefault(element.id, 0) + 1);
            }
        }

        ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> temp = new ArrayList<>(maps.entrySet());
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(temp.size(), General.threadCount);
        GetMaxThread[] getMaxThreads = new GetMaxThread[db.thread];
        GetIndices[] getIndices = new GetIndices[db.thread];
        ArrayList<IdDB> buffer = new ArrayList<>(temp.size());

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            getIndices[a] = new GetIndices(db.begin, db.end, data, map);
            db.next1();
        } db.reset();

        for (GetIndices element : getIndices)
            element.start();

        for (GetIndices element : getIndices) {
            element.join();
            indices.addAll(element.buffer);
        }

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            getMaxThreads[a] = new GetMaxThread(db.begin, db.end, temp, indices);
            db.next1();
        }

        for (GetMaxThread element : getMaxThreads)
            element.start();

        for (GetMaxThread element : getMaxThreads) {
            element.join();
            buffer.addAll(element.buffer);
        } return buffer;
    }

    //Функции фильтрации id всех уровней

    public static ArrayList<IdDB> getUserIdGenerateGenerate(int index, ArrayList<Integer> data, int level) throws InterruptedException {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> temp = ChainBase.getChainGenerate(data, level - 2);
        ArrayList<IdDB> last = getUserIdGenerateGenerateLast(index, temp.getLast());

        for (int a = data.size() - 2; a > 0; ++a)
            last = transformation(last, temp.get(a - 1));
        return last;
    }

    public static ArrayList<IdDB> getUserIdGenerateGenerate(int index, ArrayList<Integer> data, int level, long date) throws InterruptedException {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> temp = ChainBase.getChainGenerate(data, level - 2, date);
        ArrayList<IdDB> last = getUserIdGenerateGenerateLast(index, temp.getLast());

        for (int a = data.size() - 2; a > 0; ++a)
            last = transformation(last, temp.get(a - 1));
        return last;
    }

    public static ArrayList<IdDB> getUserIdGenerate(int index, ArrayList<Integer> data, int level) throws InterruptedException {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> temp = ChainBase.getChain(data, level - 2);
        System.out.println(temp.size());
        ArrayList<IdDB> last = getUserIdGenerateGenerateLast(index, temp.getLast());

        for (int a = data.size() - 2; a > 0; ++a)
            last = transformation(last, temp.get(a - 1));
        return last;
    }

    public static ArrayList<IdDB> getUserIdGenerate(int index, ArrayList<Integer> data, int level, long date) throws InterruptedException {
        ArrayList<TreeMap<Integer, TreeSet<Integer>>> temp = ChainBase.getChain(data, level - 2, date);
        ArrayList<IdDB> last = getUserIdGenerateGenerateLast(index, temp.getLast());

        for (int a = data.size() - 2; a > 0; ++a)
            last = transformation(last, temp.get(a - 1));
        return last;
    }

    //Поток очистки финального id

    public static class IdCleaner extends Thread {
        public final ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> data;
        public final ArrayList<Integer> buffer;
        public final int begin, end, id;

        public IdCleaner(int begin, int end, int id, ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> data) {
            this.begin = begin;
            this.end = end;
            this.data = data;
            this.id = id;
            this.buffer = new ArrayList<>(end - begin);
        }

        public void run() {
            for (int a = begin; a < end; ++a) {
                Map.Entry<Integer, TreeMap<Integer, Integer>> temp = data.get(a);
                int result = IDUsersGenerate.getMax(data.get(a).getValue());
                if (result == id) buffer.add(temp.getKey());
            }
        }
    }

    //Функция финального расчёта

    public static ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> transformationFinal(ArrayList<IdDB> data, int id) {
        TreeMap<Integer, TreeMap<Integer, Integer>> buffer = new TreeMap<>();
        for (IdDB element : data)
            for (int elem : element.data) {
                TreeMap<Integer, Integer> temp = buffer.computeIfAbsent(elem, s -> new TreeMap<>());
                temp.put(element.id, temp.getOrDefault(element.id, 0) + 1);
            }
        return new ArrayList<>(buffer.entrySet());
    }

    public static ArrayList<Integer> transformationFinalFilter(ArrayList<IdDB> data, int id) throws InterruptedException {
        ArrayList<Map.Entry<Integer, TreeMap<Integer, Integer>>> maps = transformationFinal(data, id);
        Utils.DecomposeTheQuantity db = new Utils.DecomposeTheQuantity(maps.size(), General.threadCount);
        IdCleaner[] idCleaners = new IdCleaner[db.thread];
        ArrayList<Integer> buffer = new ArrayList<>();

        for (int a = 0; a < db.thread; ++a) {
            db.next0();
            idCleaners[a] = new IdCleaner(db.begin, db.end, id, maps);
            db.next1();
        }

        for (IdCleaner element : idCleaners)
            element.start();

        for (IdCleaner element : idCleaners) {
            element.join();
            buffer.addAll(element.buffer);
        } return buffer;
    }

    //Финальные функции генарии id уровня

    public static ArrayList<Integer> getFilterUserIdGenerateGenerate(int index, int id, ArrayList<Integer> data, int level) throws InterruptedException {
        return transformationFinalFilter(getUserIdGenerateGenerate(index, data, level), id);
    }

    public static ArrayList<Integer> getFilterUserIdGenerateGenerate(int index, int id, ArrayList<Integer> data, int level, long date) throws InterruptedException {
        return transformationFinalFilter(getUserIdGenerateGenerate(index, data, level, date), id);
    }

    public static ArrayList<Integer> getFilterUserIdGenerate(int index, int id, ArrayList<Integer> data, int level) throws InterruptedException {
        return transformationFinalFilter(getUserIdGenerate(index, data, level), id);
    }

    public static ArrayList<Integer> getFilterUserIdGenerate(int index, int id, ArrayList<Integer> data, int level, long date) throws InterruptedException {
        return transformationFinalFilter(getUserIdGenerate(index, data, level, date), id);
    }
}