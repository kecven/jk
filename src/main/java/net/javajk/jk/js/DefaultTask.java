package net.javajk.jk.js;

import net.javajk.jk.Const;
import net.javajk.jk.Main;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultTask {

    public static void init(String[] param) throws IOException {
        String folder = Os.getInstance().pwd();

        String projectName = "";


        String src = Const.PROJECT_FOLDER_CODE_SRC;
        String resources = Const.PROJECT_FOLDER_CODE_RESOURCES;
        String srcTest = Const.PROJECT_FOLDER_TEST_SRC;
        String resourcesTest = Const.PROJECT_FOLDER_TEST_RESOURCES;


        String mainFile = "HelloWorld";

        String codeMainFile = "" +
                "/**\n" +
                " * Generate by Jk.\n" +
                " */\n" +
                "public class HelloWorld {\n\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello world\");\n" +
                "    }\n\n" +
                "}";

        String codeJkFile = "" +
                "project.name = \"HelloWorld\";\n" +
                "project.version = \"1.0\";\n" +
                "\n" +
                "\n" +
                "plugin.apply(\"build\");\n" +
                "\n";
        if (param.length >= 2){
            String packageName = param[1];
            projectName = packageName.substring(packageName.lastIndexOf('.') + 1);
            folder += "/" + projectName;

            String folderPackage = packageName.replace(".", "/");

            src = folder + "/" + src + "/" + folderPackage;
            resources = folder + "/" + resources + "/" + folderPackage;
            srcTest = folder + "/" + srcTest + "/" + folderPackage;
            resourcesTest = folder + "/" + resourcesTest + "/" + folderPackage;

            codeMainFile = "package " + packageName + ";\n\n\n" + codeMainFile;

            codeJkFile = "project.mainClass = \"" + packageName + ".HelloWorld\";\n" + codeJkFile;
        } else {
            codeJkFile = "project.mainClass = \"HelloWorld\";\n" + codeJkFile;
        }

        Os.getInstance().mkdir(src);
        Os.getInstance().mkdir(resources);
        Os.getInstance().mkdir(srcTest);
        Os.getInstance().mkdir(resourcesTest);

        Os.getInstance().write(src + "/" + mainFile + ".java", codeMainFile);
        Os.getInstance().write(folder + "/" + Const.JK_MAIN_FILE, codeJkFile);
    }


    public static void help(Map<String, Task.OneTask> tasks) {
        System.out.println("Usage: jk [command] [args...]");
        System.out.println("Command list:");

        final StringBuilder[] spaces = new StringBuilder[1];

        tasks.forEach((k,v)->{
            spaces[0] = new StringBuilder();

            for (int i = k.length(); i < 10; i++){
                spaces[0].append(" ");
            }

            String echo = "\t" + k + spaces[0] + "- " + v.description;
            System.out.println(echo);
        });
    }


    public static void clearLib() throws IOException {
        Os.getInstance().delete(Main.mainFolder + "libs/");
    }

    public static void nullTask(){}
}
