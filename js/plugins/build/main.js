
project.folder.build = "build";
project.folder.compileClasses = project.folder.build + "/classes/main"
project.folder.jar = project.folder.build + "/jar"
project.folder.tmp = project.folder.build + "/tmp"
project.folder.manifest = project.folder.tmp + "/manifest.mf"

var build = {
    javac: {
        options: {
            "-Xlint": "-options"
        }
    },
    java: {
        options: {
        }
    }
}


task.add("compile", "Compile all java class from project", function () {

    os.clearDir(project.folder.compileClasses);

    var javaFile;
    var result;

    var javac = new Array();

   // var javacString = new java.lang.StringBuilder();
    javac.push("javac");
    for (var option in build.javac.options){
        javac.push(option + ":" + build.javac.options[option]);
    }
    javac.push("-sourcepath");
    javac.push(project.folder.code.src);
    javac.push("-d");
    javac.push(project.folder.compileClasses);
    javac.push("-classpath");

    var classpath = new java.lang.StringBuilder();
    classpath.append(project.folder.compileClasses);

    for (var i = 0; i < dep.getAll().length; i++){
        classpath.append(":");
        classpath.append(dep.getAll()[i]);
    }

    javac.push(classpath.toString());

//    javacString = javacString.toString();
/*
    if (project.mainClass != undefined){
        javaFile = project.folder.code.src + "/" + project.mainClass.replace(/\./g, "/") + ".java"
        javac.add(javaFile);
        result = os.exec(javac);
    }
*/

    var allJavaClassFile = os.walkOnFile(project.folder.code.src).files;
    for (var i = 0; i < allJavaClassFile.length; i++){
        if (allJavaClassFile[i].lastIndexOf(".java") != allJavaClassFile[i].length - 5){
            //print("not java", allJavaClassFile[i])
            continue;
        } else {
            javaFile = allJavaClassFile[i];

            var classFile = project.folder.compileClasses + javaFile.replace(project.folder.code.src, '');
            classFile = classFile.substring(0, classFile.length - 5) + '.class';

            var classFile = new java.io.File(classFile);
            if (classFile.exists()){
                continue;
            }
        }

        javac.push(javaFile);

//        result = os.exec(javacString + javaFile);

        /*
         if (result.exitVal != 0){
         return result;
         }
         */
    }
    result = os.exec(javac);

    return result;
});

task.add("run", "Run project", function(){
    var result = task.run("compile");
    if (result.exitVal != 0){
        print("Not all compile");
        return result;
    }
    var mainClass = project.mainClass;

    var args = '';
    for (var i = 1; i < arguments.length; i++){
        args += " " + arguments[i];
    }
    var javaCmd = new Array();

    javaCmd.push("java");


    for (var option in build.java.options){
        javaCmd.push(option + ":" + build.java.options[option]);
    }


    javaCmd.push("-classpath");
    var classpath = new java.lang.StringBuilder();
    classpath.append(project.folder.compileClasses);
    if (dep.getAll().length > 0){
        for (var i = 0; i < dep.getAll().length; i++){
            classpath.append(":");
            classpath.append(dep.getAll()[i]);
        }
    }
    javaCmd.push(classpath.toString());

    javaCmd.push(mainClass)
    javaCmd.push(args)

    result = os.exec(javaCmd);
})


task.add("build", "Build project without library", function(){
    var result = task.run("compile");
    if (result.exitVal != 0){
        print("Not all compile");
        return result;
    }

    var manifest = new java.lang.StringBuilder();
    manifest.append("main-class: " + project.mainClass + "\n");
    if (dep.getAll().length > 0){
        os.mkdir(project.folder.jar + "/libs/");
        manifest.append("class-path: ");
        for (var i = 0; i < dep.getAll().length; i++){
            if (i != 0) {
                manifest.append(":");
            }
            var lib = "libs/" + dep.getAll()[i].substr(dep.getAll()[i].lastIndexOf("/") + 1)
            try {
                os.copy(dep.getAll()[i], project.folder.jar + "/" + lib);
            }catch (e){}
            manifest.append(lib);
        }
        manifest.append("\n");
    }
    manifest = manifest.toString();

    os.mkdir(project.folder.manifest.substr(0, project.folder.manifest.lastIndexOf("/")));
    os.mkdir(project.folder.jar);
    os.copy(project.folder.code.resources, project.folder.compileClasses);
    os.write(project.folder.manifest, manifest);
    result = os.exec("jar -cmf " + project.folder.manifest + " " + project.folder.jar + "/" + project.name + "-" + project.version + ".jar -C " + project.folder.compileClasses + " .");

});

task.add("jar", "Build project with library", function(){
    var result = task.run("compile");
    os.clearDir(project.folder.tmp);
    if (result != true){
        print("Not all compile");
        return result;
    }

    if (dep.getAll().length > 0){
        for (var i = 0; i < dep.getAll().length; i++){
            os.unZip(dep.getAll()[i], project.folder.tmp + "/jar/");
        }
    }

    os.mkdir(project.folder.build);
    os.copy(project.folder.code.resources, project.folder.tmp + "/jar");
    os.copy(project.folder.compileClasses, project.folder.tmp + "/jar");

    os.delete(project.folder.tmp + "/jar/META-INF/");
    result = os.exec("jar -cef " + project.mainClass + " " + project.folder.build + "/" + project.name + "-all-" + project.version + ".jar -C " + project.folder.tmp + "/jar" + " .");
});