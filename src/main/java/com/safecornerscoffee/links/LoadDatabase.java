package com.safecornerscoffee.links;

import com.safecornerscoffee.links.employee.Employee;
import com.safecornerscoffee.links.employee.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    public CommandLineRunner initDatabase(EmployeeRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(new Employee("Emma", "Stone", "Actress")));
            log.info("Preloading " + repository.save(new Employee("Scarlett", "Johansson", "Actress")));
        };
    }
}
