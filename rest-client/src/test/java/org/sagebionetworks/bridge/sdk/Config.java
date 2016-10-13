package org.sagebionetworks.bridge.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by liujoshua on 10/12/16.
 */
public class Config {
  public static final Config instance = new Config();

  private static final String CONFIG_FILE = "/bridge-sdk.properties";
  public final Properties properties;

  private Config() {
    properties = new Properties();

    // read properties from user config file
    loadProperties(System.getProperty("user.home") + CONFIG_FILE, properties);

    // overwrite from environment variables and system properties
    for (Props key : Props.values()) {
      String value = System.getenv(key.name());
      if (value == null) {
        value = System.getProperty(key.name());
      }
      if (value != null) {
        properties.setProperty(key.getPropertyName(), value);
      }
    }
  }

  private void loadProperties(final String fileName, final Properties properties) {
    File file = new File(fileName);
    if (file.exists()) {
      try (InputStream in = new FileInputStream(file)) {
        properties.load(in);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public enum Props {
    // These all require an entry in bridge-sdk.properties (accounts are optional).
    ACCOUNT_PASSWORD(),
    ADMIN_EMAIL(),
    ADMIN_PASSWORD(),
    DEV_NAME(),
    ENV(),
    LOG_LEVEL(),
    SDK_VERSION(),
    STUDY_IDENTIFIER();

    public String getPropertyName() {
      return this.name().replace("_", ".").toLowerCase();
    }
  }
}
