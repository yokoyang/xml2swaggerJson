import model.ComplexType;
import model.EnumType;
import model.Property;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Tools {
    public static List<String> baseTypePool() {
        List<String> basePool = new ArrayList<>();
//        basePool.add("Edm.String");
//        basePool.add("Edm.Int32");
//        basePool.add("Edm.DateTime");
//        basePool.add("Edm.Double");
        basePool.add("integer");
        basePool.add("number");
        basePool.add("string");
        basePool.add("boolean");
        return basePool;
    }

    public static List<String> getAllSelect(String urlStr) {
        List<String> allSelector = new ArrayList<>();
        allSelector.addAll(Arrays.asList(urlStr.split(",")));
        return allSelector;
    }

    public static boolean checkFilter(String str) {
        return str.contains("$filter=");
    }

    public static boolean checkComplex(String typeName, NodeList complexTypes, boolean isCollection) {
        if (isCollection) {
            String realName = typeName.substring(11, typeName.length() - 1);
            return getTargetElement(realName, complexTypes) != null;
        } else {
            return getTargetElement(typeName, complexTypes) != null;
        }
    }

    public static boolean checkCollection(String typeName) {
        return typeName.length() >= 10 && Objects.equals(typeName.substring(0, 10), "Collection");
    }

    public static Element getTargetElement(String name, NodeList complexTypes) {
        for (int i = 0; i != complexTypes.getLength(); i++) {
            Element tar = ((Element) complexTypes.item(i));
            if (Objects.equals((tar.getAttribute("name")), name)) {
                return tar;
            }
        }
        return null;
    }

    public static List<Property> getPropertyListByComplexName(String complexName, NodeList complexTypes) {
        //property list for entity
        List<Property> propertyList = new ArrayList<>();
        Element tarEle = getTargetElement(complexName, complexTypes);
        if (tarEle == null) {
            return null;
        }

        NodeList pnList = tarEle.getElementsByTagName("Property");
        for (int i = 0; i != pnList.getLength(); i++) {
            Property property = new Property();
            Node pn = pnList.item(i);
            Element pe = (Element) pn;
            property.setPropertyName(pe.getAttribute("Name"));
            //check the type
            String typeName = pe.getAttribute("Type");
            boolean isCollection = checkCollection(typeName);

            boolean isComplex = checkComplex(typeName, complexTypes, isCollection);
            property.setCollection(isCollection);

            //简单类型或者枚举
            if (!isComplex) {

                String realName;
                if (isCollection) {
                    realName = typeName.substring(11, typeName.length() - 1);
                } else {
                    realName = typeName;
                }

                if (ParseXML.allBases.contains(realName)) {
                    //如果是简单类型
                    property.setSimpleType(true);
                    property.setBaseTypeName(realName);
                } else {
                    //如果是枚举
                    property.setEnumType(getEnum(ParseXML.allEnums, realName));
                    property.setSimpleType(false);
                    property.setEnumTypeName(realName);
                }


            }
            //如果是复杂类型
            else {
                property.setSimpleType(false);
                ComplexType complexType = new ComplexType();
                String realName;
                if (isCollection) {
                    realName = typeName.substring(11, typeName.length() - 1);
                } else {
                    realName = typeName;
                }

                complexType.setComplexTypeName(realName);
                complexType.setPropertyList(getPropertyListByComplexName(realName, complexTypes));
                property.setComplexType(complexType);
            }
            propertyList.add(property);

        }
        return propertyList;
    }

    private static EnumType getEnum(List<EnumType> enumPool, String enumName) {
        for (EnumType e : enumPool) {
            if (Objects.equals(e.getEnumTypeName(), enumName)) {
                return e;
            }
        }
        return null;
    }

    private static ComplexType getCom(List<ComplexType> complexPool, String comName) {
        for (ComplexType c : complexPool) {
            if (Objects.equals(c.getComplexTypeName(), comName)) {
                return c;
            }
        }
        return null;
    }

    public static List<EnumType> enumTypePool(NodeList enumNodes) {
        List<EnumType> enumPool = new ArrayList<>();
        for (int i = 0; i != enumNodes.getLength(); i++) {
            EnumType enumType = new EnumType();
            Element enumEl = (Element) enumNodes.item(i);
            enumType.setEnumTypeName(enumEl.getAttribute("Name"));
            try {
                NodeList ns = enumEl.getElementsByTagName("Member");
                List<String> memberNameList = new ArrayList<>();

                for (int j = 0; j != ns.getLength(); j++) {
                    String memberName = ((Element) ns.item(j)).getAttribute("Name");
                    memberNameList.add(memberName);
                }
                enumType.setMemberNames(memberNameList);
                enumPool.add(enumType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return enumPool;
    }

    public static Property propertyHandler(Node propertyNode) {
        Property property = new Property();

        Element propertyElement = (Element) propertyNode;
        String typeName = propertyElement.getAttribute("Type");
        boolean isCollection = checkCollection(typeName);
        property.setCollection(isCollection);
        String realName;
        if (isCollection) {
            realName = typeName.substring(11, typeName.length() - 1);
        } else {
            realName = typeName;
        }
        EnumType enumType = getEnum(ParseXML.allEnums, realName);
        ComplexType complexType = getCom(ParseXML.complexTypeList, realName);
        //检查real Name在枚举还是复杂类型还是简单类型，放进去
        if (ParseXML.allBases.contains(realName)) {
            property.setBaseTypeName(realName);
        } else if (enumType != null) {
            property.setEnumType(enumType);
            property.setSimpleType(false);
        } else {
            property.setCollection(true);
            property.setSimpleType(false);
            property.setComplexType(complexType);
        }
        property.setPropertyName(propertyElement.getAttribute("Name"));
        return property;
    }


}
