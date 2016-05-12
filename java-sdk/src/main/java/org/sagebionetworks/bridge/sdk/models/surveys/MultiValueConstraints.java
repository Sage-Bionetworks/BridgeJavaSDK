package org.sagebionetworks.bridge.sdk.models.surveys;

import java.util.Objects;

public final class MultiValueConstraints extends Constraints {
    
    public MultiValueConstraints() {
        setDataType(DataType.STRING);
    }
    
    public MultiValueConstraints(DataType dataType) {
        setDataType(dataType);
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
        MultiValueConstraints other = (MultiValueConstraints) obj;
        return (Objects.equals(dataType, other.dataType) && Objects.equals(enumeration, other.enumeration)
                && Objects.equals(rules, other.rules) && allowMultiple == other.allowMultiple && allowOther == other.allowOther);
    }
}
