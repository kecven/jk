package net.javajk.jk;

import net.javajk.jk.js.Os;
import org.xml.sax.SAXException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class Main {

    public static String[] args;
    //public static String version = "0.1";
//    public static String version = "test";
    public static String mainFolder = getJarPath("net.javajk.jk.Main");

    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    public static final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");

    public static void main(String[] args) {


        long timeStart = System.currentTimeMillis();

        Main.args = args;
//        String mainFolder = System.getProperty("user.home") + "/.jk/" + "wrapper/" + version + "/";
        String fileName = "js/main.js";
        try {

            fileName = mainFolder + fileName;
            String jsCode = new java.lang.String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName).toAbsolutePath()));
            scriptEngine.eval(jsCode);


        } catch (ScriptException e) {
            System.err.println("Runtime error in file " + fileName + ".\n" + e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println("File not found " + fileName + ".\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        double totalTime = (System.currentTimeMillis() - timeStart) / 1000.0;

        char simbolESC = 0x1b;
        //https://habrahabr.ru/post/94647/
        System.out.println(simbolESC + "[1m" + "Total time: " + totalTime + " secs" + simbolESC + "[0m");
    }

    /**
     * Возвращает расположение class файла. Или папку с Jar файлом.
     * @param className
     * @return
     */
    private static String getJarPath(String className) {
        if (!className.startsWith("/")) {
            className = "/" + className;
        }

        className = className.replace('.', '/');
        className = className + ".class";

        URL classUrl = new Main().getClass().getResource(className);
        if (classUrl != null) {
            String temp = classUrl.getFile();

            if (temp.indexOf(".jar!/") != -1){
                temp = temp.substring(0, temp.lastIndexOf(".jar!/"));
                temp = temp.substring(0, temp.lastIndexOf("/") + 1);
            } else {
                temp = temp.substring(0, temp.length() - className.length());
            }

            if (temp.startsWith("file:")) {
                return temp.substring(5);
            }
            return temp;
        } else {
            return "\nClass '" + className +
                    "' not found in \n'" +
                    System.getProperty("java.class.path") + "'";
        }
    }
/*
    public static String getResourceFile(String fileName) throws FileNotFoundException {
        StringBuilder result = new StringBuilder("");

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(fileName);

        if (is == null){
            throw new FileNotFoundException();
        }
        try (Scanner scanner = new Scanner(is)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();
        }

        return result.toString();
    }
*/
}
