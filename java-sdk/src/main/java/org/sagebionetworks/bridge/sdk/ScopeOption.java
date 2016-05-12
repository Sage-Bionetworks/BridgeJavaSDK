package org.sagebionetworks.bridge.sdk;

import static com.google.common.base.Preconditions.checkNotNull;

import org.sagebionetworks.bridge.sdk.models.users.SharingScope;

class ScopeOption {

    private final SharingScope sharing;
    
    public ScopeOption(SharingScope sharing) {
        checkNotNull(sharing);
        this.sharing = sharing;
    }
    
    public SharingScope getScope() {
        return sharing;
    }
    
}
