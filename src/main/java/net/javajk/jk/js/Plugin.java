package net.javajk.jk.js;

import net.javajk.jk.Main;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Plugin {
    private Set<String> plugins = new HashSet<>();


    private static Plugin instance = null;

    public static Plugin getInstance(){
        if (instance == null){
            synchronized (Plugin.class){
                if (instance == null){
                    instance = new Plugin();
                }
            }
        }
        return instance;
    }

    public void apply(String name) throws ScriptException, NoSuchMethodException {
        runPlugin(name);
    }

    public void apply(List<String> names) throws ScriptException, NoSuchMethodException {
        for (int i = 0; i < names.size(); i++) {
            runPlugin(names.get(i));
        }
    }

    private void runPlugin(String name) throws ScriptException, NoSuchMethodException {
        if (plugins.contains(name)){
            return;
        }
        File file = new File(Main.mainFolder + "/js/plugins/" + name + "/main.js");

        if (file.exists()){
//            Main.scriptEngine.eval("load('" + file.getAbsolutePath() + "');");

            Invocable invocable = (Invocable) Main.scriptEngine;
            Object result = invocable.invokeFunction("load", file.getAbsolutePath());

            plugins.add(name);
        } else {
            throw new RuntimeException("Plugin `" + name + "` not found.");
        }
    }
}
