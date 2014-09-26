package org.sagebionetworks.bridge.sdk;

import java.io.IOException;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

final class HostName {

    private static String[] schemes = { "http", "https" };
    private static UrlValidator validator = new UrlValidator(schemes);

    private static String URL = "http://localhost:9000/";

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
        } else if (hostname.equals(URL)) {
            return true;
        } else {
            return false;
        }
    }

    static String getUrl() { return URL; }

    static void setUrl(String url) {
        if (!validator.isValid(url)) {
            throw new IllegalArgumentException("url is not a valid URL: " + "\"" + url + "\"");
        } else if (!isConnectableUrl(url, 1000)) {
            throw new IllegalArgumentException("Cannot connect to URL: " + "\"" + url + "\"");
        }
        URL = url;
    }
}
