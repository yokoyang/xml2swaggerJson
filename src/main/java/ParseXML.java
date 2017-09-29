import model.*;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class ParseXML {
    public static List<String> allBases;
    public static List<EnumType> allEnums;
    public static List<ComplexType> complexTypeList;


    public static DataTypes convertToDataTypes(Document doc) {
        Node datat = doc.getElementsByTagName("DataTypes").item(0);
        DataTypes dataTypes = new DataTypes();

        String description = "";
        if (datat == null) {
            return dataTypes;
        }
        Node desc = ((Element) datat).getElementsByTagName("Description").item(0);
        if (desc != null) {
            description = desc.getTextContent();
        }
        NodeList complexTypes = ((Element) datat).getElementsByTagName("ComplexType");
        NodeList enumNodes = ((Element) datat).getElementsByTagName("EnumType");
        ParseXML.allEnums = Tools.enumTypePool(enumNodes);
        ParseXML.complexTypeList = new ArrayList<>();

        EntityType entityType = new EntityType();
        Node entityNode = ((Element) datat).getElementsByTagName("EntityType").item(0);

        // entity setPropertyList
        if (entityNode == null) {
            return null;
        }
        NodeList propertyNodes = ((Element) entityNode).getElementsByTagName("Property");
        List<Property> propertyList = new ArrayList<>();


        for (int i = 0; i != complexTypes.getLength(); i++) {
            ComplexType cm = new ComplexType();
            Element el = (Element) complexTypes.item(i);
            String complexTypeName = el.getAttribute("name");
            cm.setComplexTypeName(complexTypeName);

            //setPropertyList()
            List<Property> propertyList1 = Tools.getPropertyListByComplexName(complexTypeName, complexTypes);
            cm.setPropertyList(propertyList1);

            ParseXML.complexTypeList.add(cm);
        }
        for (int i = 0; i != propertyNodes.getLength(); i++) {
            propertyList.add(Tools.propertyHandler(propertyNodes.item(i)));
        }

        //package entityType finished
        entityType.setEntityName(((Element) entityNode).getAttribute("name"));
        entityType.setPropertyList(propertyList);

        dataTypes.setDataTypesDescription(description);
        dataTypes.setComplexTypeList(complexTypeList);
        dataTypes.setEntityType(entityType);
        dataTypes.setEnumTypeList(allEnums);
        Tools.baseTypePool();
        Tools.enumTypePool(enumNodes);
        return dataTypes;
    }

}
