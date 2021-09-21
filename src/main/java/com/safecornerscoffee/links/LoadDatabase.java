package com.safecornerscoffee.links;

import com.safecornerscoffee.links.employee.Employee;
import com.safecornerscoffee.links.employee.EmployeeRepository;
import com.safecornerscoffee.links.order.Order;
import com.safecornerscoffee.links.order.OrderRepository;
import com.safecornerscoffee.links.order.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    public CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

        return args -> {
            employeeRepository.save(new Employee("Emma", "Stone", "Actress"));
            employeeRepository.save(new Employee("Scarlett", "Johansson", "Actress"));

            employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));

            orderRepository.save(new Order("MBP M1", OrderStatus.COMPLETED));
            orderRepository.save(new Order("MBP Intel", OrderStatus.IN_PROGRESS));

            orderRepository.findAll().forEach(order -> log.info("Preloaded " + order));
        };
    }
}
