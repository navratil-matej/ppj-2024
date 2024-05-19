package net.cuddlebat.ppj2024;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import net.cuddlebat.ppj2024.service.WeatherApiService;
//import net.cuddlebat.ppj2024.service.DataFrameSer 
//import net.cuddlebat.ppj2024.service.WeatherService;
//import net.cuddlebat.ppj2024.configs.JpaConfiguration;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("net.cuddlebat.ppj2024.repositories")
//@Import(JpaConfiguration.class)
//@EnableTransactionManagement
public class Ppj2024Application
{
    @Bean @PostConstruct @PreDestroy
    WeatherApiService weatherApi()
	{
		return new WeatherApiService();
	}
    
	public static void main(String[] args)
	{
		SpringApplication.run(Ppj2024Application.class, args);
	}
}
