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
        try (Connection connection = dataSource.getConnection()) {
            // 1. Create Schema
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA IF NOT EXISTS " + tenantId);
                // THIS IS THE KEY FIX: Force the session to use the tenant schema
                statement.execute("SET search_path TO " + tenantId);
            }

            // 2. Run Liquibase
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            database.setDefaultSchemaName(tenantId);
            database.setLiquibaseSchemaName(tenantId); // Keep tracking tables inside the tenant schema

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts(), new LabelExpression());
            System.out.println("User Service tables created for tenant: " + tenantId);

        } catch (Exception e) {
            throw new RuntimeException("Error initializing User DB for: " + tenantId, e);
        }
    }
}