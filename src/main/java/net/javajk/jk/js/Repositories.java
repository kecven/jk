package net.javajk.jk.js;

import java.util.*;

/**
 * Created by andrei on 17.05.16.
 */
public class Repositories {

    private static Repositories instance;

    public static Repositories getInstance(){
        if (instance == null){
            synchronized (Repositories.class){
                if (instance == null){
                    instance = new Repositories();
                }
            }
        }
        return instance;
    }

    private Repositories() {
        add("http://central.maven.org/maven2/");
    }

    private Map<String, Integer> repositories = new HashMap<>();
    private List<String> reps;

    public void add(String name){
        add(name, 1);
    }

    public void add(String name, int power){
        reps = null;
        repositories.put(name, power);
    }


    public List<String> getReps(){
        if (reps != null){
            return reps;
        }

        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(repositories.entrySet());
        Collections.sort(list, (a, b) -> b.getValue() - a.getValue());

        List<String> result = new ArrayList<>(list.size());
        list.forEach(e -> result.add(e.getKey()));

        reps = result;
        return reps;

    }
}
