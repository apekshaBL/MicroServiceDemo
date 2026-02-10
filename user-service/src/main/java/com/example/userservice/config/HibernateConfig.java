package com.example.userservice.config;

import common.multitenancy.SchemaMultiTenantConnectionProvider;
import common.multitenancy.TenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            SchemaMultiTenantConnectionProvider connectionProvider,
            TenantIdentifierResolver tenantResolver) {
        return hibernateProperties -> {
            hibernateProperties.put("hibernate.multi_tenant_connection_provider", connectionProvider);
            hibernateProperties.put("hibernate.tenant_identifier_resolver", tenantResolver);
        };
    }
}