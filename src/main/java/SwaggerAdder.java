import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Objects;

public class SwaggerAdder {

    public static JSONObject definitionsJson = new JSONObject();
    public static final Integer starterLen = 9;


    private static JSONObject fillResponses(Element e, String method) {
        JSONObject resJs = new JSONObject();
        JSONObject successJs = new JSONObject();
        successJs.put("description", "successful operation");
        resJs.put("200", successJs);
        return resJs;
    }

    private static JSONArray fillParameters(Element e, String method) {
        JSONArray parameters = new JSONArray();
        NodeList cases = e.getElementsByTagName("Case");

        for (int i = 0; i != cases.getLength(); i++) {
            Element c = (Element) cases.item(i);
            String url = c.getAttribute("url");

            Boolean hasFilter = Tools.checkFilter(url);
            Integer pos = url.indexOf("?$select=");
            if (pos > 0) {
                Integer begin = url.indexOf("?$select=") + starterLen;
                String inputQuery;
                if (hasFilter) {
                    Integer end = url.indexOf("filter");
                    inputQuery = url.substring(begin, end - 6);
                } else {
                    inputQuery = url.substring(begin);
                }

                List<String> queries = Tools.getAllSelect(inputQuery);
                for (String q : queries) {
                    JSONObject pJson = new JSONObject();
                    pJson.put("name", q);
                    pJson.put("type", "string");
                    parameters.put(pJson);
                }
            }

            if (Objects.equals(method, "post") || Objects.equals(method, "patch")) {
                JSONObject pJson = new JSONObject();
                JSONObject schemaJson = new JSONObject();

                schemaJson.put("$ref", "#/definitions/" + GetAllEntityType.entityName);
                pJson.put("name", "body");
                pJson.put("in", "body");
                pJson.put("schema", schemaJson);
                parameters.put(pJson);

            }

        }
        //

//        JSONObject X_Tenant_IDJson = putInheaderAuth("X-Tenant-ID", "1");
//        JSONObject Host_Token = putInheaderAuth("SCP-Virtual-Host-Token", "a27b0361f160d2ffaa696c51851b2793");
//        JSONObject ServiceLayer_Cookie = putInheaderAuth("SCP-ServiceLayer-Cookie", "B1SESSION=");

//        parameters.put(X_Tenant_IDJson);
//        parameters.put(Host_Token);
//        parameters.put(ServiceLayer_Cookie);

        //

        return parameters;
    }

    private static JSONObject putInheaderAuth(String name, String defaultVal) {
        JSONObject header = new JSONObject();
        header.put("name", name);
        header.put("in", "header");
        header.put("required", true);
        header.put("type", "string");
        header.put("default", defaultVal);
        return header;
    }

    private static JSONObject fillPathJson(Document doc) {
        Integer index = 0;
        JSONObject pathJson = new JSONObject();
        Node dataf = doc.getElementsByTagName("Functions").item(index);
        NodeList pathFnc = ((Element) dataf).getElementsByTagName("Function");
        Node datat = doc.getElementsByTagName("Resource").item(index);
        Element e = (Element) datat;
        String resourceName = e.getAttribute("name");

        for (int i = 0; i != pathFnc.getLength(); i++) {
            Element p = (Element) pathFnc.item(i);
            String method = p.getAttribute("method");
            String resource = p.getAttribute("resource_path");
            String resource_path = "/" + resource;
            JSONObject eachFucJson = new JSONObject();
            JSONObject meJson = new JSONObject();
            JSONArray tagList = new JSONArray();
            tagList.put(resourceName);
            meJson.put("tags", tagList);

            String description = p.getElementsByTagName("Description").item(0).getTextContent();
            meJson.put("description", description);

            JSONArray consumes = new JSONArray();
            consumes.put("application/json");
            consumes.put("application/xml");
            meJson.put("consumes", consumes);

            JSONArray produces = new JSONArray();
            produces.put("application/json");
            produces.put("application/xml");
            meJson.put("produces", produces);
            Element example = (Element) p.getElementsByTagName("Example").item(0);

//            JSONArray parameters = new JSONArray();
//            JSONObject pJson = new JSONObject();
            // TODO parameters  responses
            JSONArray parameters = fillParameters(example, method);

            meJson.put("parameters", parameters);

            JSONObject responses = fillResponses(example, method);

            meJson.put("responses", responses);

            if (pathJson.has(resource_path)) {
                ((JSONObject) pathJson.get(resource_path)).put(method, meJson);
            } else {
                eachFucJson.put(method, meJson);
                pathJson.put(resource_path, eachFucJson);
            }
        }
        return pathJson;
    }

    private static JSONArray fillTagJson(Document doc) {
        JSONArray tagsJson = new JSONArray();
        Integer index = 0;
        Node datat = doc.getElementsByTagName("Resource").item(index);
        Element e = (Element) datat;
        String summary = e.getElementsByTagName("Summary").item(0).getTextContent();
        String resourceName = e.getAttribute("name");
        JSONObject tag = new JSONObject();
        tag.put("name", resourceName);

        tag.put("description", summary);
        tagsJson.put(tag);
        return tagsJson;
    }

    private static JSONObject fillDefinition(Document doc) {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    public static JSONObject getTargetJson(Document doc, String outputPath) {
        JSONObject overAll = new JSONObject();
        overAll.put("swagger", "3.0");
        JSONObject infoJson = new JSONObject();
        infoJson.put("description", "This is a sample server Petstore server.");
        infoJson.put("version", "1.0.0");
        infoJson.put("title", "Swagger For Service Layer");

        overAll.put("info", infoJson);
        overAll.put("host", Modify.HOSTNAME);
        overAll.put("basePath", "/b1s/v1/");
        JSONArray tagsJson = fillTagJson(doc);
        overAll.put("tags", tagsJson);
        overAll.put("schemes", "https");


        JSONObject path = fillPathJson(doc);
        overAll.put("paths", path);

        overAll.put("definitions", definitionsJson);
        GetAllEntityType.write2File(overAll, outputPath);

        return overAll;
    }

    //扫描所有的枚举类型，放到definitions 里面
    public static void enumTypeDefine(Document doc) {
        Node datat = doc.getElementsByTagName("DataTypes").item(0);
        if (datat == null) {
            return;
        }
        NodeList emTypes = ((Element) datat).getElementsByTagName("EnumType");
        for (int i = 0; i != emTypes.getLength(); i++) {
            Element el = (Element) emTypes.item(i);
            String emTypeName = el.getAttribute("Name");

            JSONObject emInfo = new JSONObject();
            emInfo.put("type", "string");
            JSONArray membs = new JSONArray();
            NodeList mls = el.getElementsByTagName("Member");
            for (int j = 0; j != mls.getLength(); j++) {
                Element mb = (Element) mls.item(j);
                String mbName = mb.getAttribute("Name");
                membs.put(mbName);
            }
            emInfo.put("enum", membs);
            definitionsJson.put(emTypeName, emInfo);
        }
    }

    // 扫描所有的复杂类型，把它们放到definitions 里面
    public static void complexDefine(Document doc) {
        Node datat = doc.getElementsByTagName("DataTypes").item(0);
        if (datat == null) {
            return;
        }

        NodeList complexTypes = ((Element) datat).getElementsByTagName("ComplexType");
        for (int i = 0; i != complexTypes.getLength(); i++) {
            Element el = (Element) complexTypes.item(i);
            String complexTypeName = el.getAttribute("name");
            JSONObject comInfo = new JSONObject();
            comInfo.put("type", "object");
            NodeList pls = el.getElementsByTagName("Property");
            JSONObject each = new JSONObject();
            for (int j = 0; j != pls.getLength(); j++) {
                Element pro = (Element) pls.item(j);
                String pName = pro.getAttribute("Name");
                String type = pro.getAttribute("Type");

                //检查数组情况
                boolean isCollection = Tools.checkCollection(type);
                if (!isCollection) {
                    //检查是否是简单类型
                    if (ParseXML.allBases.contains(type)) {
                        //是简单类型
                        JSONObject simpJs = new JSONObject();
                        simpJs.put("type", type);
                        each.put(pName, simpJs);
                    } else {
                        JSONObject unSimpJs = new JSONObject();
                        String prex = "#/definitions/";
                        unSimpJs.put("$ref", prex + type);
                        each.put(pName, unSimpJs);
                    }
                } else {
                    //集合情况
                    type = type.substring(11, type.length() - 1);

                    if (ParseXML.allBases.contains(type)) {
                        JSONObject simpJs = new JSONObject();
                        simpJs.put("type", "array");
                        JSONObject itemsJs = new JSONObject();
                        itemsJs.put("type", type);
                        simpJs.put("items", itemsJs);
                        each.put(pName, simpJs);
                    } else {
                        JSONObject itemsJs = new JSONObject();
                        JSONObject unSimpJs = new JSONObject();
                        String prex = "#/definitions/";
                        unSimpJs.put("type", "array");
                        itemsJs.put("$ref", prex + type);
                        unSimpJs.put("items", itemsJs);
                        each.put(pName, unSimpJs);
                    }
                }
            }
            comInfo.put("properties", each);

            definitionsJson.put(complexTypeName, comInfo);
        }
    }

    public static void packageBigJsonType() {
        String etName = GetAllEntityType.entityName;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "object");
        jsonObject.put("properties", GetAllEntityType.etPropertiesJson);

        definitionsJson.put(etName, jsonObject);
    }
}
