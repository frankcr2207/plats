package com.cdm.config;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mysqlDosEntityManagerFactory", 
		transactionManagerRef = "mysqlDosTransactionManager", basePackages = { "com.cdm.repository2" })

public class MysqlDosDBConfig {

	@Bean(name = "mysqlDosDataSource")
	@ConfigurationProperties(prefix = "spring.mysqldos.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "mysqlDosEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean mysqlDosEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("mysqlDosDataSource") DataSource dataSource) {
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "none");
		properties.put("hibernate.format_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		return builder.dataSource(dataSource).properties(properties)
				.packages("com.cdm.domain2").build();
	}

	@Bean(name = "mysqlDosTransactionManager")
	public PlatformTransactionManager mysqlDosTransactionManager(
			@Qualifier("mysqlDosEntityManagerFactory") EntityManagerFactory bookEntityManagerFactory) {
		return new JpaTransactionManager(bookEntityManagerFactory);
	}
}

