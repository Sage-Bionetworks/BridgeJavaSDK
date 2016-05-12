package org.sagebionetworks.bridge.sdk;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public final class ClientInfo {
    
    private final String appName;
    private final Integer appVersion;
    private final String device;
    private final String osName;
    private final String osVersion;
    private final Integer sdkVersion;

    ClientInfo(String appName, Integer appVersion, String device, String osName, String osVersion, Integer sdkVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.device = device;
        this.osName = osName;
        this.osVersion = osVersion;
        this.sdkVersion = sdkVersion;
    }
    
    public static class Builder {
        private String appName;
        private Integer appVersion;
        private String device;
        private String osName;
        private String osVersion;
        private Integer sdkVersion;
        
        public Builder() {
            this.osName = System.getProperty("os.name");
            this.osVersion = System.getProperty("os.version");
            this.sdkVersion = Integer.parseInt(ClientProvider.getConfig().getSdkVersion());
        }
        /**
         * The name of the application that is using this SDK to contact the Bridge server.
         * @param appName
         * @return
         */
        public Builder withAppName(String appName) {
            this.appName = appName;
            return this;
        }
        /**
         * The application version of the application using this SDK. This must be a number.
         * @param appVersion
         * @return
         */
        public Builder withAppVersion(Integer appVersion) {
            this.appVersion = appVersion;
            return this;
        }
        /**
         * Information about the device on which this application is running (particularly interesting 
         * for mobile clients).
         * @param device
         * @return
         */
        public Builder withDevice(String device) {
            this.device = device;
            return this;
        }
        /**
         * The name of the operating system on which the application is running. By default, this is the 
         * value retrieved by <code>System.getProperty("os.name")</code>.
         * @param osName
         * @return
         */
        public Builder withOsName(String osName) {
            this.osName = osName;
            return this;
        }
        /**
         * The version of the operating system on which the application is running. By default, this is the 
         * value retrieved by <code>System.getProperty("os.version")</code>.
         * @param osVersion
         * @return
         */
        public Builder withOsVersion(String osVersion) {
            this.osVersion = osVersion;
            return this;
        }
        // Private setter for tests
        Builder withSdkVersion(Integer sdkVersion) {
            this.sdkVersion = sdkVersion;
            return this;
        }
        public ClientInfo build() {
            return new ClientInfo(appName, appVersion, device, osName, osVersion, sdkVersion);
        }
    }
    
    @Override
    public synchronized String toString() {
        List<String> stanzas = Lists.newArrayListWithCapacity(3);
        if (isNotBlank(appName) && appVersion != null) {
            stanzas.add(String.format("%s/%s", appName, appVersion));
        }
        if (isNotBlank(device) && isNotBlank(osName) && osVersion != null) {
            stanzas.add(String.format("(%s; %s/%s)", device, osName, osVersion));
        }
        if (sdkVersion != null) {
            stanzas.add(String.format("BridgeJavaSDK/%s", sdkVersion));    
        }
        return Joiner.on(" ").join(stanzas);
    }
}
