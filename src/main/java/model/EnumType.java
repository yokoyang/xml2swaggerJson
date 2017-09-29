package model;

import java.util.List;

public class EnumType {
    private String enumTypeName;
    private List<String> memberNames;

    public String getEnumTypeName() {
        return enumTypeName;
    }

    public void setEnumTypeName(String enumTypeName) {
        this.enumTypeName = enumTypeName;
    }

    public List<String> getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(List<String> memberNames) {
        this.memberNames = memberNames;
    }

    public EnumType() {
    }
}
