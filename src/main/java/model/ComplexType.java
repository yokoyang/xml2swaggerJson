package model;

import java.util.List;

public class ComplexType {
    private String complexTypeName;
    private List<Property> propertyList;

    public String getComplexTypeName() {
        return complexTypeName;
    }

    public void setComplexTypeName(String complexTypeName) {
        this.complexTypeName = complexTypeName;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<Property> propertyList) {
        this.propertyList = propertyList;
    }

    public ComplexType() {
    }
}
