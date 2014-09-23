package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

class HostName {

    private static String[] schemes = { "http", "https" };
    private static UrlValidator validator = new UrlValidator(schemes);

    private static String LOCAL = "http://localhost:9000/";
    private static String DEV = "http://pd-dev.sagebridge.org/";
    private static String STAGING = "http://pd-staging.sagebridge.org/";
    private static String PROD = "http://pd.sagebridge.org/";

    public static boolean isConnectableUrl(String url, int timeout) {
        assert validator.isValid(url);
        assert 0 <= timeout && timeout <= 10 * 1000; // must be less than 10 minutes.
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
        if (hostname.equals(LOCAL) || hostname.equals(DEV) || hostname.equals(STAGING) || hostname.equals(PROD)) {
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
        assert validator.isValid(localUrl);
        LOCAL = localUrl;
    }

    public static void setDev(String devUrl) {
        assert validator.isValid(devUrl);
        DEV = devUrl;
    }

    public static void setStaging(String stagingUrl) {
        assert validator.isValid(stagingUrl);
        STAGING = stagingUrl;
    }

    public static void setProd(String prodUrl) {
        assert validator.isValid(prodUrl);
        PROD = prodUrl;
    }
}
