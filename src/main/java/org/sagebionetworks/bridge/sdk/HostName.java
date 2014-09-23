package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

public class HostName {

    private static String[] schemes = { "http", "https" };
    private static UrlValidator validator = new UrlValidator(schemes);

    private static String LOCAL = "http://localhost:9000/";
    private static String DEV = "http://pd-dev.sagebridge.org/";
    private static String STAGING = "http://pd-staging.sagebridge.org/";
    private static String PROD = "http://pd.sagebridge.org/";

    public static boolean isConnectableUrl(String url, int timeout) {
        if (!validator.isValid(url)) {
            throw new IllegalArgumentException("URL is not a valid one: " + url);
        } else if (timeout <= 0 || 10 * 1000 <= timeout) {
            throw new IllegalArgumentException("timeout isn't in the valid range (0 < timeout < 10 minutes): "
                    + timeout);
        }
        url = url.replaceFirst("https", "http");
        try {
            Response response = Request.Head(url).connectTimeout(timeout).execute();
            int statusCode = response.returnResponse().getStatusLine().getStatusCode();
            return (200 <= statusCode && statusCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    public static boolean isValidHostName(String hostname) {
        if (hostname == null) {
            throw new IllegalArgumentException("hostname was null.");
        } else if (hostname.equals(LOCAL) || hostname.equals(DEV) || hostname.equals(STAGING) || hostname.equals(PROD)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getLocal() { return LOCAL; }
    public static String getDev() { return DEV; }
    public static String getStaging() { return STAGING; }
    public static String getProd() { return PROD; }

    public static void setLocal(String localUrl) {
        if (!validator.isValid(localUrl)) {
            throw new IllegalArgumentException("localUrl is not a valid URL: " + localUrl);
        }
        LOCAL = localUrl;
    }

    public static void setDev(String devUrl) {
        if (!validator.isValid(devUrl)) {
            throw new IllegalArgumentException("devUrl is not a valid URL: " + devUrl);
        }
        DEV = devUrl;
    }

    public static void setStaging(String stagingUrl) {
        if (!validator.isValid(stagingUrl)) {
            throw new IllegalArgumentException("stagingUrl is not a valid URL: " + stagingUrl);
        }
        STAGING = stagingUrl;
    }

    public static void setProd(String prodUrl) {
        if (!validator.isValid(prodUrl)) {
            throw new IllegalArgumentException("prodUrl is not a valid URL: " + prodUrl);
        }
        PROD = prodUrl;
    }
}
