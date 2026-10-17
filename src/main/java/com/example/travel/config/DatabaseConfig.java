package com.example.travel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.io.File;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    @Value("${SPRING_DATASOURCE_URL:#{null}}")
    private String springDatasourceUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        String dbUrl = springDatasourceUrl != null ? springDatasourceUrl : databaseUrl;

        // If running on Railway / Render with PostgreSQL
        if (dbUrl != null && (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://"))) {
            try {
                URI uri = new URI(dbUrl);
                String userInfo = uri.getUserInfo();
                String username = "";
                String password = "";
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    password = parts[1];
                }
                
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath(); // includes leading slash e.g. /railway
                
                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                
                System.out.println("Using PostgreSQL Database at: " + host + ":" + port + path);
                return DataSourceBuilder.create()
                        .driverClassName("org.postgresql.Driver")
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .build();
            } catch (Exception e) {
                System.err.println("Failed to parse PostgreSQL DATABASE_URL: " + e.getMessage() + ", falling back to persistent file DB.");
            }
        }

        // Ensure ./data directory exists for persistent local storage
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Persistent file-based H2 database: survives application restarts and computer reboots!
        String h2FileUrl = "jdbc:h2:file:./data/traveldb;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE";
        System.out.println("Using Persistent File Database at: ./data/traveldb");
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url(h2FileUrl)
                .username("sa")
                .password("")
                .build();
    }
}
