package com.example.userservice.service;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Service
public class TenantService {

    private final DataSource dataSource;

    public TenantService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initDatabase(String tenantId) {
        try {
            // 1. Create Schema (Safe to run even if Student Service already created it)
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenantId);
            }

            // 2. Run Liquibase to create 'users' table
            try (Connection connection = dataSource.getConnection()) {
                connection.setSchema(tenantId); // Switch to the tenant schema

                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                // Set default schema so Liquibase knows where to create tables
                database.setDefaultSchemaName(tenantId);

                Liquibase liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.xml",
                        new ClassLoaderResourceAccessor(),
                        database
                );

                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println(" User Service tables created for tenant: " + tenantId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error initializing User DB for: " + tenantId, e);
        }
    }
}