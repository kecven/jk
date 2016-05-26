package net.javajk.jk.js;

import net.javajk.jk.Main;
import net.javajk.jk.js.optional.DefaultTask;
import net.javajk.jk.js.optional.TaskAction;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


public class Task {

    private static Task instance = null;

    public static Task getInstance(){
        if (instance == null){
            synchronized (Task.class){
                if (instance == null){
                    instance = new Task();
                }
            }
        }
        return instance;
    }

    private Task() {
        addTask("null", "Nothing doing", (e)->{
            DefaultTask.nullTask();
            return null;
        });

        addTask("help", "This help", (e)->{
            DefaultTask.help(tasks);
            return null;
        });

        addTask("init", "Create default directory for your project", (e)->{
            try {
                DefaultTask.init((String[]) e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        });

        addTask("clearLib", "Delete all download libs and download need library from Ethernet", (e)->{
            try {
                DefaultTask.clearLib();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        });

    }


    //--------------------------------------------

    public class OneTask{
        public String name;
        public String description;
        public TaskAction task;
    }

    private final Map<String, OneTask> tasks = new LinkedHashMap<>();

    public Map<String, OneTask> getTasks() {
        return tasks;
    }

    public OneTask getTask(String name) {
        return tasks.get(name);
    }

    /**
     * Разрешает переопределять задачи.
     * @param name
     * @param description
     * @param task
     */
    public void addTask(String name, String description, TaskAction task){
        OneTask oneTask = new OneTask();
        oneTask.description = description;
        oneTask.name = name;
        oneTask.task = task;

        tasks.put(name, oneTask);
    }

    /**
     * Добавление таска, если такой таск уже существует выкенет ошибку
     * @param name
     * @param description
     * @param task
     */
    public void add(String name, String description, TaskAction task){
        // task already add
        if (tasks.containsKey(name)) {
            throw new RuntimeException("task `" + name + "` already add.");
        } else { // Add task
            addTask(name, description, task);
        }
    }

    public void add(String name, TaskAction task, String description){
        add(name, description, task);
    }

    public void add(String name, TaskAction task){
        String description = "Not defined";
        add(name, description, task);
    }

    public Object run(String... args) throws ScriptException, NoSuchMethodException {
            return start(args);
    }

    //start from console
    public Object run() throws ScriptException, NoSuchMethodException {
        if (Main.args.length == 0) {    //without param
            String[] args = new String[1];
            args[0] = "help";
            return start(args);
        } else {
            return start(Main.args);
        }
    }

    private Object start(String[] args) throws ScriptException, NoSuchMethodException {
        if (tasks.containsKey(args[0])){


        //    Invocable invocable = (Invocable) Main.scriptEngine;
            Object result = tasks.get(args[0]).task.run(args);
            //Object result = invocable.invokeFunction("tast.getTask(" + args[0] + ").task", args);
           // System.out.println(":" + args[0]);

            char simbolESC = 0x1b;
            //https://habrahabr.ru/post/94647/
            System.out.println(simbolESC + "[32m" + ":" + args[0] + simbolESC + "[0m");
            return result;
            /*
            var result = tasks[args[0]].task(args)	//exec task
            print(":" + args[0]);
            return result;
            */
        } else {	//task not found
            System.out.println("Task `" + args[0] + "` not found.");
            System.out.println("Try start command help.");
        }
        return null;
    }
}
