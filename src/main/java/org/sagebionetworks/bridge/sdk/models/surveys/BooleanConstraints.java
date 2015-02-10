package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;


public final class BooleanConstraints extends Constraints {

    public BooleanConstraints() {
        setDataType(DataType.BOOLEAN);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allowMultiple ? 1231 : 1237);
        result = prime * result + (allowOther ? 1231 : 1237);
        result = prime * result + Objects.hashCode(dataType);
        result = prime * result + Objects.hashCode(enumeration);
        result = prime * result + Objects.hashCode(rules);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        BooleanConstraints other = (BooleanConstraints) obj;
        return (allowMultiple == other.allowMultiple && allowOther == other.allowOther
                && Objects.equals(dataType, other.dataType) && Objects.equals(enumeration, other.enumeration) 
                && Objects.equals(rules, other.rules));
    }
}
