package no.cantara.jau.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper {

    private static final Logger log = LoggerFactory.getLogger(PropertiesHelper.class);

    public static final String CONFIG_SERVICE_URL_KEY = "configservice.url";
    public static final String CLIENT_NAME_PROPERTY_DEFAULT_VALUE = "Default clientName";
    public static final String CONFIG_FILENAME = "config.properties";

    private static final String CLIENT_NAME_PROPERTY_KEY = "clientName";
    private static final String ARTIFACT_ID = "configservice.artifactid";
    private static final String CONFIG_SERVICE_USERNAME_KEY = "configservice.username";
    private static final String CONFIG_SERVICE_PASSWORD_KEY = "configservice.password";
    private static final String VERSION_PROPERTY_KEY = "version";
    private static final String IS_RUNNING_INTERVAL_KEY = "isrunninginterval";
    private static final String UPDATE_INTERVAL_KEY = "updateinterval";
    private static final int DEFAULT_UPDATE_INTERVAL = 3 * 60; // seconds
    private static final int DEFAULT_IS_RUNNING_INTERVAL = 10; // seconds

    public static Properties getProperties() {
        Properties properties = new Properties();
        try {
            properties.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(CONFIG_FILENAME));
        } catch (NullPointerException | IOException e) {
            log.debug("{} not found on classpath.", CONFIG_FILENAME);
        }
        return properties;
    }

    public static String getStringProperty(final Properties properties, String propertyKey, String defaultValue) {
        String property = properties.getProperty(propertyKey, defaultValue);
        if (property == null) {
            property = System.getProperty(propertyKey);
        }
        return property;
    }

    public static Integer getIntProperty(final Properties properties, String propertyKey, Integer defaultValue) {
        String property = getStringProperty(properties, propertyKey, null);
        if (property == null) {
            return defaultValue;
        }
        return Integer.valueOf(property);
    }

    public static String getClientName() {
        return getStringProperty(getProperties(), CLIENT_NAME_PROPERTY_KEY, CLIENT_NAME_PROPERTY_DEFAULT_VALUE);
    }

    public static String getArtifactId() {
        return getStringProperty(getProperties(), ARTIFACT_ID, null);
    }

    public static String getServiceConfigUrl() {
        return getStringProperty(getProperties(), CONFIG_SERVICE_URL_KEY, null);
    }

    public static String getUsername() {
        return getStringProperty(getProperties(), CONFIG_SERVICE_USERNAME_KEY, null);
    }

    public static String getPassword() {
        return getStringProperty(getProperties(), CONFIG_SERVICE_PASSWORD_KEY, null);
    }

    public static int getUpdateInterval() {
        return getIntProperty(getProperties(), UPDATE_INTERVAL_KEY, DEFAULT_UPDATE_INTERVAL);
    }

    public static int getIsRunningInterval() {
        return getIntProperty(getProperties(), IS_RUNNING_INTERVAL_KEY, DEFAULT_IS_RUNNING_INTERVAL);
    }

    public static String getVersion() {
        return getStringProperty(getProperties(), VERSION_PROPERTY_KEY, null);
    }
}
