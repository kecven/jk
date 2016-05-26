// tasks will be run in this context
var global = this;

var JK_SYSTEM_CONST_CLASS = Java.type("net.javajk.jk.Const");

var jk = {
    version: Java.type("net.javajk.jk.Main").version,
    mainFile: JK_SYSTEM_CONST_CLASS.JK_MAIN_FILE,
}

jk.mainFolder = Java.type("net.javajk.jk.Main").mainFolder;

var project = {
    mainPackage: "",
    version: "0.1.0",
    build: 0,
    folder: {
        code: {
            src: JK_SYSTEM_CONST_CLASS.PROJECT_FOLDER_CODE_SRC,
            resources: JK_SYSTEM_CONST_CLASS.PROJECT_FOLDER_CODE_RESOURCES
        },
        test: {
            src: JK_SYSTEM_CONST_CLASS.PROJECT_FOLDER_TEST_SRC,
            resources: JK_SYSTEM_CONST_CLASS.PROJECT_FOLDER_TEST_RESOURCES
        },
    },
    mainClass: undefined,
    name: "HelloWorld"
}

var args = Java.type("net.javajk.jk.Main").args;


// local scope for jk internal stuff
(function () {
    //export to global
    var Os = Java.type("net.javajk.jk.js.Os");
    var Task = Java.type("net.javajk.jk.js.Task");
    var Repositories = Java.type("net.javajk.jk.js.Repositories");
    var Dependencies = Java.type("net.javajk.jk.js.Dependencies");
    var Plugin = Java.type("net.javajk.jk.js.Plugin");

    global.os      =           Os.getInstance();
    global.task    =           Task.getInstance();
    global.rep     =           Repositories.getInstance();
    global.dep     =           Dependencies.getInstance();
    global.plugin  =           Plugin.getInstance();

})();



try {
//Run user script
    load(os.pwd() + "/" + jk.mainFile)
} catch (e){}
task.run();
