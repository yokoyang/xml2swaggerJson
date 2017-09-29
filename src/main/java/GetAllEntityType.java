import model.ComplexType;
import model.DataTypes;
import model.Property;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class GetAllEntityType {
    public static String entityName = "";
    private static String entityDescription = "";
    public static JSONObject etPropertiesJson = new JSONObject();
    public static String defRefName = "";

    private JSONArray parsePList(List<Property> propertyList) {
        JSONArray propertyArray = new JSONArray();

        for (Property p : propertyList) {
//            JSONObject propertyJson = new JSONObject();
            JSONObject pInfo = new JSONObject();

            boolean isCollection = false;
            JSONObject jsonObject = new JSONObject();
            if (p.isCollection()) {
                isCollection = true;
                jsonObject.put("Collection", "true");
            } else {
                jsonObject.put("Collection", "false");
            }
            //不是array的情况
            if (!isCollection) {
                if (p.isSimpleType()) {
                    pInfo.put("type", p.getBaseTypeName());
                } else if (p.getEnumType() != null) {
                    pInfo.put("type", "string");
                    pInfo.put("description", p.getEnumType().getEnumTypeName());
                    JSONArray ems = new JSONArray();
                    for (String memberName : p.getEnumType().getMemberNames()) {
                        ems.put(memberName);
                    }
                    pInfo.put("enum", ems);
                } else {
                    ComplexType cmt = p.getComplexType();
                    String prex = "#/definitions/";
                    if (cmt != null) {
                        String comtName = cmt.getComplexTypeName();
                        defRefName = prex + comtName;
                        pInfo.put("$ref", defRefName);
                    }

                }
            }
            //是Array
            else {
                pInfo.put("type", "array");
                JSONObject itemsJs = new JSONObject();
                if (p.isSimpleType()) {
                    itemsJs.put("type", p.getBaseTypeName());
                } else if (p.getEnumType() != null) {
                    itemsJs.put("description", p.getEnumType().getEnumTypeName());
                    itemsJs.put("type", "string");
                    JSONArray ems = new JSONArray();
                    for (String memberName : p.getEnumType().getMemberNames()) {
                        ems.put(memberName);
                    }
                    itemsJs.put("enum", ems);
                } else {
                    String prex = "#/definitions/";
                    ComplexType cmt = p.getComplexType();
                    if (cmt != null) {
                        String comtName = cmt.getComplexTypeName();
                        defRefName = prex + comtName;
                        itemsJs.put("$ref", defRefName);
                    }
                }
                pInfo.put("items", itemsJs);
            }

            jsonObject.put("PropertyName", p.getPropertyName());
            if (p.isSimpleType()) {
                jsonObject.put("Type", p.getBaseTypeName());
            } else if (p.getEnumType() != null) {
                JSONArray enumsJson = new JSONArray();
                for (String memberName : p.getEnumType().getMemberNames()) {
                    enumsJson.put(memberName);
                }
                JSONObject emJson = new JSONObject();
                emJson.put("Value", enumsJson);
                emJson.put("Name", p.getEnumType().getEnumTypeName());
                jsonObject.put("Type", emJson);
            }
            //复杂类型
            else {
                JSONObject cmJson = new JSONObject();
                ComplexType cmt = p.getComplexType();
                if (cmt != null) {
                    cmJson.put("Name", cmt.getComplexTypeName());
                    List<Property> properties = cmt.getPropertyList();
                    JSONArray jsonArray = parsePList(properties);
                    JSONObject jobj = new JSONObject();
                    jobj.put(cmt.getComplexTypeName(), jsonArray);
                    jsonObject.put("Type", jobj);
                }

            }
            propertyArray.put(jsonObject);
            etPropertiesJson.put(p.getPropertyName(), pInfo);
        }
        return propertyArray;
    }

    public static void write2File(JSONObject jsonObject, String path) {
        File f = new File(path);
        String content = jsonObject.toString();
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
//            fw=new FileWriter(f.getAbsoluteFile(),true);  //true表示可以追加新内容
            fw = new FileWriter(f.getAbsoluteFile()); //表示不追加
            bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void dataTypetoJson(DataTypes dataTypes) {
        JSONObject etJson = new JSONObject();
        JSONObject etInfo = new JSONObject();
        if (dataTypes == null) {
            return;
        }
        JSONArray propertyArray = new JSONArray();
        if(dataTypes.getEntityType()!=null){
            entityName = dataTypes.getEntityType().getEntityName();
            entityDescription = dataTypes.getDataTypesDescription();
            propertyArray = parsePList(dataTypes.getEntityType().getPropertyList());
        }


        etInfo.put("type", "object");

        etJson.put(entityName, etInfo);

        JSONObject jsonObj0 = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("name", entityName);
        jsonObj0.put("Description", entityDescription);

        // TODO Property List

        jsonObject1.put("Property", propertyArray);

        jsonObj0.put("EntityType", jsonObject1);

        etInfo.put("properties", etPropertiesJson);


//        write2File(etInfo, "C:\\Users\\i332340\\Desktop\\scp\\xml2swagger\\src\\test\\test5.json");
    }

}
