package net.javajk.jk.js;

import net.javajk.jk.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Dependencies {

    private Set<String> dependencies = new HashSet<>();
    private List<String> all;
    private Repositories repositories;


    public Dependencies(Repositories repositories) {
        this.repositories = repositories;
    }

    public List<String> getAll() {
        if (all == null) {
            all = new ArrayList<>(dependencies);
        }
        return all;
    }

    public void add(String name) throws IOException {
        String[] dep = name.split(":");
        if (dep.length == 4){
            add(dep[0], dep[1], dep[3]);
        } else if (dep.length == 3){
            add(dep[0], dep[1], dep[2]);
        } else if (dep.length == 2){
            add(dep[0], dep[1], "last");
        } else {
            throw new RuntimeException("Unknown dependency. " + name);
        }
    }

    public void add(String group, String name, String version) throws IOException {
        if ("last".equals(version)){
            throw new RuntimeException("Not found dependency. group = " + group + ", name = " + name + ", version = " + version);
        }

        String nameDep = group.replaceAll("\\.", "/") + "/" + name + "/" + version + "/" + name + "-" + version + ".jar";
        String path = Main.mainFolder + "libs/" + nameDep;
        if (dependencies.contains(path)){
            return;
        }

        File pathFile = new File(path);
        if (pathFile.exists()){
            dependencies.add(path);
            return;
        }

        Os.getInstance().mkdir(path.substring(0, path.lastIndexOf("/")));
        //http://central.maven.org/maven2/                  org/apache/logging/log4j/log4j-core/2.5/log4j-core-2.5.jar
        //http://search.maven.org/remotecontent?filepath=   org/apache/logging/log4j/log4j-core/2.5/log4j-core-2.5.jar
        //'org.apache.logging.log4j:log4j-core:jar:2.5'

        List<String> reps = repositories.getReps();



        boolean result = false;
        for (int i = 0; i < reps.size(); i++) {
            String url = reps.get(i) + nameDep;
            try {
                downloadUsingNIO(url, path);
                result = true;
                break;
            } catch (IOException e) {
            }
        }
        if (!result){
            throw new RuntimeException("Not found dependency. group = " + group + ", name = " + name + ", version = " + version);
        }
        dependencies.add(path);

    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
