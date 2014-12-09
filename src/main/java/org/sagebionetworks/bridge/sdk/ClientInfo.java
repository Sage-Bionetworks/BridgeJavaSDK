package org.sagebionetworks.bridge.sdk;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public final class ClientInfo {
    
    private String appName = "";
    private String appVersion = "";
    private String device = "";
    private String osName = "";
    private String osVersion = "";
    private String sdkVersion = "";

    public ClientInfo setAppName(String appName) {
        this.appName = nullSafe(appName);
        return this;
    }
    public ClientInfo setAppVersion(String appVersion) {
        this.appVersion = nullSafe(appVersion);
        return this;
    }
    public ClientInfo setDevice(String device) {
        this.device = nullSafe(device);
        return this;
    }
    public ClientInfo setOsName(String osName) {
        this.osName = nullSafe(osName);
        return this;
    }
    public ClientInfo setOsVersion(String osVersion) {
        this.osVersion = nullSafe(osVersion);
        return this;
    }
    public ClientInfo setSdkVersion(String sdkVersion) {
        this.sdkVersion = nullSafe(sdkVersion);
        return this;
    }

    private String nullSafe(String value) {
        return (value != null) ? value : "";
    }
    
    private boolean notBlank(String value) {
        return (!"".equals(value) && value != null);
    }
    
    @Override
    public synchronized String toString() {
        List<String> stanzas = Lists.newArrayListWithCapacity(3);
        if (notBlank(appName) && notBlank(appVersion)) {
            stanzas.add(String.format("%s/%s", appName, appVersion));
        } else if (notBlank(appName)) {
            stanzas.add(appName);
        } else if (notBlank(appVersion)) {
            stanzas.add(appVersion);
        }
        if (notBlank(device) && notBlank(osName)) {
            stanzas.add(String.format("(%s; %s/%s)", device, osName, osVersion));
        } else if (notBlank(device)) {
            stanzas.add(String.format("(%s)", device));
        } else if (notBlank(osName)){
            stanzas.add(String.format("(%s/%s)", osName, osVersion));
        }
        if (notBlank(sdkVersion)) {
            stanzas.add(String.format("BridgeJavaSDK/%s", sdkVersion));    
        } else {
            stanzas.add("BridgeJavaSDK");
        }
        return Joiner.on(" ").join(stanzas);
    }
}
