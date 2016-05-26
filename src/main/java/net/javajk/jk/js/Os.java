package net.javajk.jk.js;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Os {

    private static Os instance = null;

    public static Os getInstance(){
        if (instance == null){
            synchronized (Os.class){
                if(instance == null){
                    instance = new Os();
                }
            }
        }
        return instance;
    }

    private Os() {}

    public String read(String fileName) throws IOException {
        return new java.lang.String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName)));
    }

    public void write(String fileName, String text) throws IOException {
        java.nio.file.Files.write(java.nio.file.Paths.get(fileName), new java.lang.String(text).getBytes());
    }

    //Если хотим создать файл
    public void write(String fileName) throws IOException {
        write(fileName, "");
    }

    public void copy(String from, String to) throws IOException {
        File fromFile = new File(from);
        File toFile = new File(to);

        if (fromFile.isFile()) {
            Files.copy(fromFile.toPath(), toFile.toPath());
        } else if (fromFile.isDirectory()){
            mkdir(to);
            Map<String, List<String>> contains = walkOnFile(from);
            //Создаём папки
            for (int i = 0; i < contains.get("folders").size(); i++){
                String dirName = contains.get("folders").get(i).toString().replace(from, to);
                mkdir(dirName);
            }
            //Копируем файлы
            for (int i = 0; i < contains.get("files").size(); i++){
                String fileName = contains.get("files").get(i).toString().replace(from, to);
                Files.copy(new File(contains.get("files").get(i)).toPath(), new File(fileName).toPath());
            }
        }
    }

    public void move(String from, String to) throws IOException {
        copy(from, to);
        delete(from);
    }

    public String pwd(){
        return System.getProperty("user.dir");
    }

    public void mkdir(String path) throws IOException {
        File files = new File(path);
        if (!files.exists()) {
            if (!files.mkdirs()) {
                throw new IOException("Failed created new directory. `" + path + "`");
            }
        }
    }

    public Map<String, List<String>> walkOnFile(String path) throws IOException {
        List<String> files = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());

        List<String> folders = Files.walk(Paths.get(path))
                .filter(Files::isDirectory)
                .map(Path::toString)
                .collect(Collectors.toList());

        Map<String, List<String>> result = new HashMap<>();

        result.put("files", files);
        result.put("folders", folders);

        return result;
    }

    public boolean existsFile(String file){
        return new File(file).exists();
    }

    public void delete(String src) throws IOException {
        File file = new java.io.File(src);
        if (file.isFile()) {
            java.nio.file.Files.deleteIfExists(file.toPath());
        } else if (file.isDirectory()) {
            clearDir(src);
            java.nio.file.Files.deleteIfExists(file.toPath());
        }
    }
    public void clearDir(String path) throws IOException {
        Map<String, List<String>> wof = walkOnFile(path);
        //Удаляем все файлы
        for (int i = 0; i < wof.get("files").size(); i++){
            java.nio.file.Files.deleteIfExists(new File(wof.get("files").get(i)).toPath());
        }
        //Удаляем все папки, в обратном порядке.
        //В общратном, что бы в начале вдалились дочерние папки, а потом корневые, что бы удаляемые папки были пустыми
        //В цикле for(второй параметр). Так и должно быть, последнюю папку - корень, не удаляем.
        for (int i = wof.get("folders").size() - 1; i > 0; i--){
            java.nio.file.Files.deleteIfExists(new File(wof.get("folders").get(i)).toPath());
        }

    }


    public Map<String, Object> exec(String cmd) throws IOException, InterruptedException {
        return exec(cmd.split(" "));
    }

    public Map<String, Object> exec(String[] cmd) throws IOException, InterruptedException {
        return exec(Arrays.asList(cmd));
    }

    public Map<String, Object> exec(List<String> listCmd) throws IOException, InterruptedException {
        // указываем в конструкторе ProcessBuilder,
        // что нужно запустить программу с произвольным количеством параметров
        ProcessBuilder procBuilder = new ProcessBuilder(listCmd);

        // перенаправляем стандартный поток ошибок на
        // стандартный вывод
        procBuilder.redirectErrorStream(true);

        // запуск программы
        Process process = procBuilder.start();

        // читаем стандартный поток вывода
        // и выводим на экран
        String out = readStream(process.getInputStream());

        // ждем пока завершится вызванная программа
        // и сохраняем код, с которым она завершилась в
        // в переменную exitVal
        int exitVal = process.waitFor();

        Map<String, Object> result = new HashMap<>();
        result.put("out", out);
        result.put("exitVal", exitVal);

        return result;
    }


    public Map<String, Object> run(String cmd) throws IOException, InterruptedException {
        return run(cmd.split(" "));
    }

    public Map<String, Object> run(String[] cmd) throws IOException, InterruptedException {
        return run(Arrays.asList(cmd));
    }

    public Map<String, Object> run(List<String> listCmd) throws IOException, InterruptedException {
        // указываем в конструкторе ProcessBuilder,
        // что нужно запустить программу с произвольным количеством параметров
        ProcessBuilder procBuilder = new ProcessBuilder(listCmd);

        // перенаправляем стандартный поток ошибок на
        // стандартный вывод
        procBuilder.redirectErrorStream(false);

        // запуск программы
        Process process = procBuilder.start();

        // читаем стандартный поток вывода
        // и выводим на экран
        String out = readStream(process.getInputStream());
        String err = readStream(process.getErrorStream());


        // ждем пока завершится вызванная программа
        // и сохраняем код, с которым она завершилась в
        // в переменную exitVal
        int exitVal = process.waitFor();

        Map<String, Object> result = new HashMap<>();
        result.put("out", out);
        result.put("err", err);
        result.put("exitVal", exitVal);

        return result;
    }

    private String readStream(InputStream inputStream) throws IOException {

        InputStreamReader isrStdout = new java.io.InputStreamReader(inputStream);
        BufferedReader brStdout = new java.io.BufferedReader(isrStdout);

        StringBuilder sbOut = new StringBuilder();
        String line;
        while((line = brStdout.readLine()) != null) {
            sbOut.append(line + "\n");
        }
        return sbOut.toString();
    }

    public void unZip(String fileName, String folderName) throws IOException {

        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            System.out.println("File cannot be read");
            return;
        }
        File folder = new File(folderName);

        mkdir(folderName);
        try {
            ZipFile zip = new ZipFile(fileName);
            Enumeration entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File thisFile = new File(folder, entry.getName());

                //Без этого может не сработать, по каким то причинам он может просмотр начать с файлов, а не папок
                mkdir(thisFile.getParent());

                if (entry.isDirectory()) {
                    thisFile.mkdirs();
                } else {
                    write(zip.getInputStream(entry),
                            new BufferedOutputStream(new FileOutputStream(
                                    thisFile)));
                }
            }

            zip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0)
            out.write(buffer, 0, len);
        out.close();
        in.close();
    }
}
