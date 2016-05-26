project.name = "jk";
project.version = "0.1";
project.mainClass = "net.javajk.jk.Main";


plugin.apply("build");
//plugin.apply("idea")

task.add("install", function(param){
    task.run("build");

    var jsRecource = "src/main/resources";
    var jar = project.folder.jar + "/" + project.name + "-" + project.version + ".jar";

    if (param.length == 1){
        jk.mainFolder = java.lang.System.getProperty("user.home")+ "/.jk/wrapper/test/";
    } else {
        jk.mainFolder = java.lang.System.getProperty("user.home")+ "/.jk/wrapper/0.1/";
    }

    os.delete(jk.mainFolder);
    os.mkdir(jk.mainFolder);
    os.copy(jsRecource, jk.mainFolder)
    os.copy("js/", jk.mainFolder + "/js/")
    os.copy(jar, jk.mainFolder + project.name + "-" + project.version + ".jar")

//    os.delete("/usr/bin/jk")  Нужно удалять из под root
/*
    if (param.length == 1){
        os.exec("sudo ln -s " + jk.mainFolder + "bin/jk /usr/bin/jkt")
    } else {
        os.exec("sudo ln -s " + jk.mainFolder + "bin/jk /usr/bin/jk")
    }
*/
    /*
     andrei@dns:~/project/jk/jk$ javac -Xlint:-options -sourcepath src/main/java -d build/classes/main src/main/java/net/javajk/jk/Main.java
     andrei@dns:~/project/jk/jk$ java -classpath build/classes/main net.javajk.jk.Main
     andrei@dns:~/project/jk/jk$ jar -cmf build/tmp/manifest.mf build/jar/jk-0.1.jar -C build/classes/main .
     */

});


task.add("test1", function(param){
    os.exec(["sudo", "uname", "-a"])
})

task.add("test2", function(param){
    print(task.run("test1", 1,2,3,4,5,5))
})


//dep.add('org.apache.logging.log4j:log4j-core:jar:2.5')
dep.add('com.google.code.gson:gson:2.5')

