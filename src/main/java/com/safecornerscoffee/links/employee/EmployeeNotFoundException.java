package com.safecornerscoffee.links.employee;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long id) {
        super(String.format("Could Not Find Employee{id=%d}", id));
    }
}
