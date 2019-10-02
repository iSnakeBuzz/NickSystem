package com.isnakebuzz.nicksystem.Database.Core;

import com.isnakebuzz.nicksystem.Main;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;

public class MySQL {

    public static String username;
    public static String password;
    public static String database;
    public static String host;
    public static int port;
    public static Connection con;
    public static boolean isEnabled;

    static {
        MySQL.isEnabled = false;
        MySQL.username = "";
        MySQL.password = "";
        MySQL.database = "";
        MySQL.host = "";
        MySQL.port = 3306;
    }

    public static void connect(Main plugin) {
        if (!isConnected()) {
            try {
                if (MySQL.isEnabled) {
                    final Properties properties = new Properties();
                    properties.setProperty("user", MySQL.username);
                    properties.setProperty("password", MySQL.password);
                    properties.setProperty("autoReconnect", "true");
                    properties.setProperty("verifyServerCertificate", "false");
                    properties.setProperty("useSSL", "false");
                    properties.setProperty("requireSSL", "false");
                    MySQL.con = DriverManager.getConnection("jdbc:mysql://" + MySQL.host + ":" + MySQL.port + "/" + MySQL.database, properties);
                    plugin.log("NickSystem", "Has been connected to MySQL Connection.");
                } else {
                    File db = new File(plugin.getDataFolder(), MySQL.database + ".db");
                    if (!db.exists()) {
                        try {
                            db.createNewFile();
                        } catch (IOException e) {
                            plugin.getLogger().log(Level.SEVERE, "File write error: " + MySQL.database + ".db");
                        }
                    }
                    Class.forName("org.sqlite.JDBC");
                    MySQL.con = DriverManager.getConnection("jdbc:sqlite:" + db);
                    plugin.log("NickSystem", "Has been loaded SQLite File.");

                }
            } catch (SQLException e) {
                plugin.log("NickSystem", "&cError connecting to MySQL Connection");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                plugin.log("NickSystem", "&cError loading SQLite Connection");
            }
        }
    }

    public static void disconnect() {
        if (isConnected()) {
            try {
                MySQL.con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        return MySQL.con != null;
    }

    public static void update(final String qry) {
        if (isConnected()) {
            try {
                MySQL.con.createStatement().executeUpdate(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResultSet getResult(final String qry) {
        if (isConnected()) {
            try {
                return MySQL.con.createStatement().executeQuery(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getFirstString(final ResultSet rs, final int l, final String re, final int t) {
        try {
            while (rs.next()) {
                if (rs.getString(l).equalsIgnoreCase(re)) {
                    return rs.getString(t);
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    public static int getFirstInt(final ResultSet rs, final int l, final String re, final int t) {
        try {
            while (rs.next()) {
                if (rs.getString(l).equalsIgnoreCase(re)) {
                    return rs.getInt(t);
                }
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    public static Connection getConnection() {
        return MySQL.con;
    }

    public static ResultSet query(final String query) throws SQLException {
        final Statement stmt = MySQL.con.createStatement();
        try {
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
