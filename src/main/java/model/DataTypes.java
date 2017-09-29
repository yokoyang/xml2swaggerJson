package model;

import java.util.List;

public class DataTypes {
    private String dataTypesDescription;
    private List<ComplexType> complexTypeList;
    private List<EnumType> enumTypeList;
    private EntityType entityType;

    public String getDataTypesDescription() {
        return dataTypesDescription;
    }

    public void setDataTypesDescription(String dataTypesDescription) {
        this.dataTypesDescription = dataTypesDescription;
    }

    public List<ComplexType> getComplexTypeList() {
        return complexTypeList;
    }

    public void setComplexTypeList(List<ComplexType> complexTypeList) {
        this.complexTypeList = complexTypeList;
    }

    public List<EnumType> getEnumTypeList() {
        return enumTypeList;
    }

    public void setEnumTypeList(List<EnumType> enumTypeList) {
        this.enumTypeList = enumTypeList;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public DataTypes() {
    }
}
