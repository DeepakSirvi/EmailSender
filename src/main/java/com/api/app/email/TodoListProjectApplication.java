package com.api.app.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
public class TodoListProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(TodoListProjectApplication.class, args);
	}
	
	@Bean
     ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

        resolver.setPrefix("templates/"); 
        resolver.setCacheable(false); 
        resolver.setSuffix(".html"); 
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        return resolver;

}
}