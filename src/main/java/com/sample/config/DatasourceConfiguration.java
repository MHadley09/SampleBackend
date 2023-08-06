package com.sample.config;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DatasourceConfiguration {
    @Bean
    protected DSLContext dslContext(Connection connection) {
        return new DefaultDSLContext(connection, SQLDialect.POSTGRES);
    }

    @Bean
    protected DataSourceConnectionProvider dataSourceConnectionProviderTransactionAware(DataSource dataSource) {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    protected Connection connect(DataSourceConnectionProvider dataSourceConnectionProvider){
        return dataSourceConnectionProvider.acquire();
    }
}