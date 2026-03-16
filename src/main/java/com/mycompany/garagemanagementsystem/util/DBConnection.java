package com.mycompany.garagemanagementsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_NAME = "garage_management";
    private static final String PORT = "8889";
    private static final String URL = "jdbc:mysql://localhost:" + PORT + "/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASS = "root"; // ganti sesuai konfigurasi MySQL Anda

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL tidak ditemukan", e);
            }
        }
        return connection;
    }
}

