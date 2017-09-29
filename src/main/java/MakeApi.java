import model.DataTypes;
import org.json.JSONObject;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MakeApi {
    private static final String dirName = "C:\\Users\\i332340\\Desktop\\scp\\makeSwagger\\src\\main\\resources\\apis";
    private static final String outDirName = "C:\\Users\\i332340\\Desktop\\scp\\makeSwagger\\src\\main\\resources\\output\\";

    public static void makeOneAPi(String inputFilePath, String outputFilePath) {
        ParseXML.allBases = Tools.baseTypePool();
        try {

            File fXmlFile = new File(inputFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            DataTypes dataTypes = ParseXML.convertToDataTypes(doc);
            GetAllEntityType getAllEntityType = new GetAllEntityType();
            getAllEntityType.dataTypetoJson(dataTypes);
            SwaggerAdder.complexDefine(doc);
            SwaggerAdder.enumTypeDefine(doc);
            SwaggerAdder.packageBigJsonType();
            SwaggerAdder.getTargetJson(doc, outputFilePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MakeClear() {
        ParseXML.allBases.clear();
        ParseXML.allEnums.clear();
        ParseXML.complexTypeList.clear();
        SwaggerAdder.definitionsJson = new JSONObject();
        GetAllEntityType.etPropertiesJson = new JSONObject();

    }

    //批处理，先得到apis文件夹下所有的文件，然后把它们的文件名拿到
    private static List<String> getXMLFiles(String dirName) {
        List<String> xmlFiles = new ArrayList<>();

        File dir = new File(dirName);
        String[] children = dir.list();
        if (children == null) {
            System.out.println("目录不存在或它不是一个目录");
        } else {
            xmlFiles.addAll(Arrays.asList(children));
        }
        return xmlFiles;
    }

    //TODO bug: getElementsByTagName("Property") 有些文件没有EntityType
    public static void main(String argv[]) {
        Integer loop = 0;
        List<String> xmlFiles = getXMLFiles(dirName);
        for (String xmlFile : xmlFiles) {
            loop += 1;
            String inputXMLPath = "C:\\Users\\i332340\\Desktop\\scp\\makeSwagger\\src\\main\\resources\\apis\\" + xmlFile;
            Integer len = inputXMLPath.length();
            String name = inputXMLPath.substring(inputXMLPath.lastIndexOf("\\") + 1, len - 4);
            String outputJsonPath = outDirName + name + ".json";
            MakeApi.makeOneAPi(inputXMLPath, outputJsonPath);
            System.out.println(loop);
            MakeClear();

        }

    }
}
