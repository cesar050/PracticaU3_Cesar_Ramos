package com.tercero.controller.dao.implement;

//import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Scanner;
import com.tercero.controller.tda.list.LinkedList;
import com.google.gson.Gson;

public class AdapterDao<T> implements InterfazDao<T> {
    @SuppressWarnings("FieldMayBeFinal")
    private Class<T> clazz;
    private Gson g;

    public static String URL = "media/";
    //public static String URL = "media" + File.separator;

    public AdapterDao(Class clazz) {
        this.clazz = clazz;
        this.g = new Gson();
    }

    @Override
    public void persist(T object) throws Exception {
        LinkedList<T> list = listAll();
        list.add(object);
        String info = g.toJson(list.toArray());
        saveFile(info);
    }

    @Override
    public void merge(T object, Integer index) throws Exception {
        LinkedList<T> list = listAll();
        list.update(object, index);
        String info = g.toJson(list.toArray());
        saveFile(info);
    }

    @Override
    public LinkedList listAll() {
        LinkedList list = new LinkedList<>();
        try {
            String data = readFile();

            T[] matrix = (T[]) g.fromJson(data, java.lang.reflect.Array.newInstance(clazz, 0).getClass());
            list.toList(matrix);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Integer index) throws Exception {
        LinkedList<T> list = listAll();
        T[] temp = (T[]) list.toArray();
        for (int i = 0; i < temp.length; i++) {
            if (getIdent(temp[i]).intValue() == index.intValue()) {
                return temp[i];
            }
        }
        return null;
    }

    private Integer getIdent(T obj) {
        try {
            Method metod = null;
            for (Method m : clazz.getMethods()) {
                if (m.getName().equalsIgnoreCase("getId")) {
                    metod = m;
                    break;
                }
            }
            if (metod == null) {
                for (Method m : clazz.getSuperclass().getMethods()) {
                    if (m.getName().equalsIgnoreCase("getId")) {
                        metod = m;
                        break;
                    }
                }
            }
            if (metod != null)
                return (Integer) metod.invoke(obj);
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    // private Integer getIdent(T obj){
    // try {
    // Method method = null;
    // for (Method m : clazz.getMethods()) {
    // if (m.getName().equalsIgnoreCase("getId")) {
    // method = m;
    // break;
    // }
    // }
    // if (method == null) {
    // for (Method m : clazz.getSuperclass().getMethods()) {
    // if (m.getName().equalsIgnoreCase("getId")) {
    // method = m;
    // break;
    // }
    // }
    // }
    // if (method != null) {
    // return (Integer) method.invoke(obj); // casting papu
    // }
    // } catch (Exception e) {
    // //return -1;
    // }
    // return -1;
    // }

    @Override
    public void delete(Integer index) throws Exception {
        LinkedList<T> list = listAll();
        list.delete(index - 1);
        String info = g.toJson(list.toArray());
        saveFile(info);
    }

    @SuppressWarnings("ConvertToTryWithResources")
    private String readFile() throws Exception {
        Scanner in = new Scanner(new FileReader(URL + clazz.getSimpleName() + ".json"));
        StringBuilder sb = new StringBuilder();
        while (in.hasNext()) {
            sb.append(in.nextLine());
        }
        in.close();
        return sb.toString();
    }

    protected void updateListFile(LinkedList<T> list) throws Exception {
        String info = g.toJson(list.toArray());
        saveFile(info);
    }

    private void saveFile(String data) throws Exception {
        FileWriter f = new FileWriter(URL + clazz.getSimpleName() + ".json");
        f.write(data);
        f.flush();
        f.close();
    }
}
