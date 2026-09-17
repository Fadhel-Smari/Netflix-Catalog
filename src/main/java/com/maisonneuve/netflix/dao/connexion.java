package com.maisonneuve.netflix.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connexion {

    private static final String CONFIG_PATH = "/data/database.properties";

    private Connexion() {}

    private static Properties chargerConfiguration() {
        Properties props = new Properties();
        try (InputStream input = Connexion.class.getResourceAsStream(CONFIG_PATH)) {
            if (input == null) {
                throw new IllegalStateException("Erreur : Le fichier " + CONFIG_PATH + " est introuvable.");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture de la configuration BDD", e);
        }
        return props;
    }

    public static Connection getConnexion() throws SQLException {
        Properties props = chargerConfiguration();
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");

        return DriverManager.getConnection(url, user, pass);
    }
}