package org.sagebionetworks.bridge.sdk.models;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.ScopeOfSharing;

public class ScopeOption {

    private final ScopeOfSharing sharing;
    
    public ScopeOption(ScopeOfSharing sharing) {
        checkNotNull(sharing);
        this.sharing = sharing;
    }
    
    public ScopeOfSharing getScope() {
        return sharing;
    }
    
}
