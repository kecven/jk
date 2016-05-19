
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

    os.delete(project.folder.compileClasses);
    os.mkdir(project.folder.compileClasses);

    var javaFile;
    var result;

    var javacString = new java.lang.StringBuilder();
    javacString.append("javac ");
    for (var option in build.javac.options){
        javacString.append(option + ":" + build.javac.options[option] + " ");
    }
    javacString.append("-sourcepath " + project.folder.code.src + " -d " + project.folder.compileClasses + " ");
    javacString.append("-classpath " + project.folder.compileClasses);
    if (dep.getAll().length > 0){
        for (var i = 0; i < dep.getAll().length; i++){
            javacString.append(":");
            javacString.append(dep.getAll()[i]);
        }
    }
    javacString.append(" ");

    javacString = javacString.toString();

    if (project.mainClass != undefined){
        javaFile = project.folder.code.src + "/" + project.mainClass.replace(/\./g, "/") + ".java"

        result = os.exec(javacString + javaFile);
    }


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

        result = os.exec(javacString + javaFile);

        /*
         if (result.exitVal != 0){
         return result;
         }
         */
    }
    return result;
});

task.add("run", "Run project", function(){
    var result = task.run("compile");
    var mainClass = project.mainClass;

    var args = '';
    for (var i = 1; i < arguments.length; i++){
        args += " " + arguments[i];
    }
    var javaString = new java.lang.StringBuilder();

    javaString.append("java ");
    for (var option in build.java.options){
        javaString.append(option + ":" + build.java.options[option] + " ");
    }
    javaString.append("-classpath " + project.folder.compileClasses);
    if (dep.getAll().length > 0){
        for (var i = 0; i < dep.getAll().length; i++){
            javaString.append(":");
            javaString.append(dep.getAll()[i]);
        }
    }
    javaString.append(" ");

    javaString = javaString.toString();

    result = os.exec(javaString + mainClass + args);
})


task.add("build", "Build project without library", function(){
    var result = task.run("compile");
    if (result != true){
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
    os.delete(project.folder.tmp);
    os.mkdir(project.folder.tmp);
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