package org.sagebionetworks.bridge.sdk.models;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.SharingScope;

public class ScopeOption {

    private final SharingScope sharing;
    
    public ScopeOption(SharingScope sharing) {
        checkNotNull(sharing);
        this.sharing = sharing;
    }
    
    public SharingScope getScope() {
        return sharing;
    }
    
}
