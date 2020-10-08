package app.messages;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
@ComponentScan("app.messages")
public class AppConfig {
	
	private DataSource dataSource;
	
	public AppConfig(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setPackagesToScan("app.messages");
		return sessionFactoryBean;
	}
	
	@Bean
	public FilterRegistrationBean<AuditingFilter> auditingFilterRegistrationBean(){
		FilterRegistrationBean<AuditingFilter> registration = new FilterRegistrationBean<>();
		AuditingFilter filter = new AuditingFilter();
		registration.setFilter(filter);
		registration.setOrder(Integer.MAX_VALUE);
		registration.setUrlPatterns(Arrays.asList("/messages/*"));
		return registration;
		
	}
}