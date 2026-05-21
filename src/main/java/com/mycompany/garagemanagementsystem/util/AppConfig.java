package com.mycompany.garagemanagementsystem.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import javax.swing.ImageIcon;

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

    public static String getLoginImagePath() {
        return props.getProperty("app.login.image.path", "");
    }

    /**
     * Load gambar login. Coba filesystem lalu classpath.
     * Return null jika tidak ditemukan.
     */
    public static ImageIcon loadLoginImage(int width, int height) {
        String path = getLoginImagePath();
        if (path == null || path.trim().isEmpty()) return null;

        File file = new File(path);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            if (icon.getIconWidth() > 0) return scaleIcon(icon, width, height);
        }
        String cp = path.startsWith("/") ? path : "/" + path;
        URL url = AppConfig.class.getResource(cp);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            if (icon.getIconWidth() > 0) return scaleIcon(icon, width, height);
        }
        url = AppConfig.class.getClassLoader().getResource(path.startsWith("/") ? path.substring(1) : path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            if (icon.getIconWidth() > 0) return scaleIcon(icon, width, height);
        }
        return null;
    }

    public static String getCompanyPhoneFormatted() {
        return "Telp: " + getCompanyPhone();
    }

    /**
     * Load logo sebagai ImageIcon. Coba dari:
     * 1. Path absolut di filesystem
     * 2. Path relatif di filesystem
     * 3. Classpath resource (misal /logo.png atau logo.png)
     * Return null jika tidak ditemukan.
     */
    public static ImageIcon loadLogo(int width, int height) {
        String path = getLogoPath();
        if (path == null || path.trim().isEmpty()) return null;

        // 1. Coba filesystem (absolut atau relatif)
        File file = new File(path);
        if (file.exists()) {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            if (icon.getIconWidth() > 0) {
                return scaleIcon(icon, width, height);
            }
        }

        // 2. Coba classpath resource
        String classpathPath = path.startsWith("/") ? path : "/" + path;
        URL url = AppConfig.class.getResource(classpathPath);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            if (icon.getIconWidth() > 0) {
                return scaleIcon(icon, width, height);
            }
        }

        // 3. Coba tanpa leading slash
        String noSlash = path.startsWith("/") ? path.substring(1) : path;
        url = AppConfig.class.getClassLoader().getResource(noSlash);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            if (icon.getIconWidth() > 0) {
                return scaleIcon(icon, width, height);
            }
        }

        return null;
    }

    private static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        java.awt.Image img = icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}
