task.add("idea", "Setup idea project", function () {

    os.mkdir(".idea/libraries/");

    //get Idea setting file
    var ideaSettingFile;
    var ideaFoldea = os.walkOnFile(".idea/");
    var allIdeaFile = ideaFoldea.get("files");
    for (var i = 0; i < allIdeaFile.length; i++){
        var fileName = allIdeaFile.get(i);
        if (".iml" == fileName.substring(fileName.length - 4)){
            ideaSettingFile = fileName;
        }
    }

    var allDepName = [];

    //Add library setting
    for (var i = 0; i < dep.getAll().length; i++){
        var thisDep = dep.getAll()[i];
        var name = thisDep.substring(thisDep.lastIndexOf("/") + 1, thisDep.length - 4);

        var fileName = ".idea/libraries/" + name.replace(/[\.-]{1}/g, "_") + ".xml";

        if (os.existsFile(fileName)){
            continue;
        }

        allDepName.push(name);

        var xmlText = '<component name="libraryTable"> \n' +
            '    <library name="' + name + '"> \n' +
            '    <CLASSES> \n' +
            '       <root url="jar://' + thisDep + '!/" /> \n' +
            '    </CLASSES> \n' +
            '    <JAVADOC /> \n' +
            '    <SOURCES /> \n' +
            '    </library> \n' +
            '</component>'

        os.write(fileName, xmlText);
    }


    // add library to project
    var dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
    var builder = dbf.newDocumentBuilder();
    var document = builder.parse(ideaSettingFile);

    var orders = document.getFirstChild().getChildNodes();
    for(var i = 0; i < orders.getLength(); i++){
        var curOrder = orders.item(i);
        if ("component".equals(curOrder.getNodeName())){

            for (var j = 0; j < allDepName.length; j++) {
                print(allDepName[j])
                var orderEntry = document.createElement("orderEntry");
                curOrder.appendChild(orderEntry);
                orderEntry.setAttribute("type", "library");
                orderEntry.setAttribute("name", allDepName[j]);
                orderEntry.setAttribute("level", "project");
            }
            break;
        }
    }

    var transformerFactory = javax.xml.transform.TransformerFactory
        .newInstance();
    var transformer = transformerFactory.newTransformer();
    var source = new javax.xml.transform.dom.DOMSource(document);
    var result = new javax.xml.transform.stream.StreamResult(new java.io.File(ideaSettingFile));
    transformer.transform(source, result);


});