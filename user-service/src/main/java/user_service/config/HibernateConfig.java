package user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig {

    @Bean
    @Primary // This tells Spring: "Use this bean, ignore the default one"
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            MultiTenantConnectionProviderImpl connectionProvider, // Use your actual class name here
            SchemaIdentifierResolver tenantResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("user_service.entity");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();

        // Hibernate 6 Multi-Tenancy String Keys (Safer than Constants)
        properties.put("hibernate.multiTenancy", "SCHEMA");
        properties.put("hibernate.multi_tenant_connection_provider", connectionProvider);
        properties.put("hibernate.tenant_identifier_resolver", tenantResolver);

        // Standard Hibernate settings
        properties.put("hibernate.hbm2ddl.auto", "none"); // Crucial for multi-tenancy
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");

        em.setJpaPropertyMap(properties);
        return em;
    }
}