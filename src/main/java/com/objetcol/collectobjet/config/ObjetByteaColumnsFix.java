package com.objetcol.collectobjet.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Si la base a été créée avec titre/description en BYTEA (ancien schéma), PostgreSQL
 * ne peut pas appliquer {@code LOWER()} dans les requêtes JPA — correction unique au démarrage.
 */
@Component
@Order(0)
public class ObjetByteaColumnsFix implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ObjetByteaColumnsFix.class);

    private final DataSource dataSource;

    public ObjetByteaColumnsFix(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection c = dataSource.getConnection()) {
            if (!"PostgreSQL".equalsIgnoreCase(c.getMetaData().getDatabaseProductName())) {
                return;
            }
            fixIfBytea(c, "titre", "varchar(255)");
            fixIfBytea(c, "description", "text");
        } catch (Exception e) {
            log.warn("Correction BYTEA sur objets ignorée ou impossible : {}", e.getMessage());
        }
    }

    private static void fixIfBytea(Connection c, String column, String sqlType) throws Exception {
        if (!isBytea(c, column)) {
            return;
        }
        String sql = String.format(
                "ALTER TABLE objets ALTER COLUMN %s TYPE %s USING convert_from(%s, 'UTF8')",
                column, sqlType, column);
        try (Statement st = c.createStatement()) {
            st.execute(sql);
            log.warn("Schéma corrigé : colonne objets.{} était BYTEA, convertie en {}.", column, sqlType);
        }
    }

    private static boolean isBytea(Connection c, String column) throws Exception {
        String q = """
                SELECT data_type FROM information_schema.columns
                WHERE table_schema = 'public' AND table_name = 'objets' AND column_name = ?
                """;
        try (PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, column);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && "bytea".equalsIgnoreCase(rs.getString(1));
            }
        }
    }
}
