package com.mycompany.garagemanagementsystem.util;

import java.io.*;
import java.util.Properties;

/**
 * Konfigurasi aplikasi yang dapat diubah tanpa compile ulang.
 * File konfigurasi: app.properties (di root project atau classpath)
 */
public class AppConfig {

    private static final Properties props = new Properties();
    private static final String CONFIG_FILE = "app.properties";

    // Default values
    private static final String DEFAULT_APP_NAME = "Garage Management System";
    private static final String DEFAULT_COMPANY_NAME = "BENGKEL GARAGE MANAGEMENT";
    private static final String DEFAULT_COMPANY_ADDRESS = "Jl. Raya Otomotif No. 123, Jakarta";
    private static final String DEFAULT_COMPANY_PHONE = "021-555-1234";
    private static final String DEFAULT_LOGO_PATH = "";

    static {
        loadConfig();
    }

    private static void loadConfig() {
        // Try loading from file system first
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                return;
            } catch (IOException ignored) {}
        }
        // Try loading from classpath
        try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {}
    }

    public static String getAppName() {
        return props.getProperty("app.name", DEFAULT_APP_NAME);
    }

    public static String getCompanyName() {
        return props.getProperty("company.name", DEFAULT_COMPANY_NAME);
    }

    public static String getCompanyAddress() {
        return props.getProperty("company.address", DEFAULT_COMPANY_ADDRESS);
    }

    public static String getCompanyPhone() {
        return props.getProperty("company.phone", DEFAULT_COMPANY_PHONE);
    }

    public static String getLogoPath() {
        return props.getProperty("app.logo.path", DEFAULT_LOGO_PATH);
    }

    public static String getCompanyPhoneFormatted() {
        return "Telp: " + getCompanyPhone();
    }
}
