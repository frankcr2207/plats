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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mysqlUnoEntityManagerFactory", 
		transactionManagerRef = "mysqlUnoTransactionManager", basePackages = { "com.cdm.repository" })

public class MysqlUnoDBConfig {
	
	@Primary
	@Bean(name = "mysqlUnoDataSource")
	@ConfigurationProperties(prefix = "spring.mysqluno.datasource")
	public DataSource dataSource2() {
		return DataSourceBuilder.create().build();
	}
	
	@Primary
	@Bean(name = "mysqlUnoEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean mysqlUnoEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("mysqlUnoDataSource") DataSource dataSource) {
		HashMap<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "none");
		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		return builder.dataSource(dataSource).properties(properties)
				.packages("com.cdm.domain").build();
	}
	
	@Primary
	@Bean(name = "mysqlUnoTransactionManager")
	public PlatformTransactionManager mysqlUnoTransactionManager(
			@Qualifier("mysqlUnoEntityManagerFactory") EntityManagerFactory bookEntityManagerFactory) {
		return new JpaTransactionManager(bookEntityManagerFactory);
	}
}
