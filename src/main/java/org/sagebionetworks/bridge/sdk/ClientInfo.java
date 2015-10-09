package org.sagebionetworks.bridge.sdk;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public final class ClientInfo {
    
    private String appName = "";
    private Integer appVersion;
    private String device = "";
    private String osName = "";
    private String osVersion = "";
    private String sdkVersion = "";

    ClientInfo(boolean defaultValues) {
        if (defaultValues) {
            osName = System.getProperty("os.name");
            osVersion = System.getProperty("os.version");
            sdkVersion = ClientProvider.getConfig().getSdkVersion();
        }
    }
    
    /**
     * The name of the application that is using this SDK to contact the Bridge server.
     * @param appName
     * @return
     */
    public synchronized ClientInfo withAppName(String appName) {
        this.appName = emptyStringIfNull(appName);
        return this;
    }
    /**
     * The application version of the application using this SDK. This must be a number.
     * @param appVersion
     * @return
     */
    public synchronized ClientInfo withAppVersion(Integer appVersion) {
        this.appVersion = appVersion;
        return this;
    }
    /**
     * Information about the device on which this application is running (particularly interesting 
     * for mobile clients).
     * @param device
     * @return
     */
    public synchronized ClientInfo withDevice(String device) {
        this.device = emptyStringIfNull(device);
        return this;
    }
    /**
     * The name of the operating system on which the application is running. By default, this is the 
     * value retrieved by <code>System.getProperty("os.name")</code>.
     * @param osName
     * @return
     */
    public synchronized ClientInfo withOsName(String osName) {
        this.osName = emptyStringIfNull(osName);
        return this;
    }
    /**
     * The version of the operating system on which the application is running. By default, this is the 
     * value retrieved by <code>System.getProperty("os.version")</code>.
     * @param osVersion
     * @return
     */
    public synchronized ClientInfo withOsVersion(String osVersion) {
        this.osVersion = emptyStringIfNull(osVersion);
        return this;
    }
    // Private setter for tests
    synchronized ClientInfo withSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
        return this;
    }

    private String emptyStringIfNull(String value) {
        return (value != null) ? value : "";
    }
    
    @Override
    public synchronized String toString() {
        List<String> stanzas = Lists.newArrayListWithCapacity(3);
        if (isNotBlank(appName) && appVersion != null) {
            stanzas.add(String.format("%s/%s", appName, appVersion));
        } else if (isNotBlank(appName)) {
            stanzas.add(appName);
        } else if (appVersion != null) {
            stanzas.add(Integer.toString(appVersion));
        }
        if (isNotBlank(device) && isNotBlank(osName)) {
            stanzas.add(String.format("(%s; %s %s)", device, osName, osVersion));
        } else if (isNotBlank(device)) {
            stanzas.add(String.format("(%s)", device));
        } else if (isNotBlank(osName)){
            stanzas.add(String.format("(%s; %s)", osName, osVersion));
        }
        if (isNotBlank(sdkVersion)) {
            stanzas.add(String.format("BridgeJavaSDK/%s", sdkVersion));    
        } else {
            stanzas.add("BridgeJavaSDK");
        }
        return Joiner.on(" ").join(stanzas);
    }
}
