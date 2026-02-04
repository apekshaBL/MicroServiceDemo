package com.example.studentservice;

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
            // 1. Create the Schema if it doesn't exist
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenantId);
            }

            // 2. Run Liquibase on that specific Schema
            try (Connection connection = dataSource.getConnection()) {
                connection.setSchema(tenantId); // Switch to new schema

                Database database = DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(new JdbcConnection(connection));

                // Point to your EXISTING changelog file
                database.setDefaultSchemaName(tenantId);

                Liquibase liquibase = new Liquibase(
                        "db/changelog/db.changelog-master.xml", // Your XML file path
                        new ClassLoaderResourceAccessor(),
                        database
                );

                liquibase.update(new Contexts(), new LabelExpression());
                System.out.println(" Liquibase ran successfully for tenant: " + tenantId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error initializing database for tenant: " + tenantId, e);
        }
    }
}