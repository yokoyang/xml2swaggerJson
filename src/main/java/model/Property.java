package model;

public class Property {
    private String propertyName;
    private String complexTypeName = null;
    private String enumTypeName = null;
    private boolean isSimpleType = true;
    private boolean isCollection = false;
    private ComplexType complexType = null;
    private EnumType enumType = null;
    private String baseTypeName=null;

    public String getBaseTypeName() {
        return baseTypeName;
    }

    public void setBaseTypeName(String baseTypeName) {
        this.baseTypeName = baseTypeName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getComplexTypeName() {
        return complexTypeName;
    }

    public void setComplexTypeName(String complexTypeName) {
        this.complexTypeName = complexTypeName;
    }

    public String getEnumTypeName() {
        return enumTypeName;
    }

    public void setEnumTypeName(String enumTypeName) {
        this.enumTypeName = enumTypeName;
    }

    public boolean isSimpleType() {
        return isSimpleType;
    }

    public void setSimpleType(boolean simpleType) {
        isSimpleType = simpleType;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public ComplexType getComplexType() {
        return complexType;
    }

    public void setComplexType(ComplexType complexType) {
        this.complexType = complexType;
    }

    public EnumType getEnumType() {
        return enumType;
    }

    public void setEnumType(EnumType enumType) {
        this.enumType = enumType;
    }

    public Property() {
    }
}
