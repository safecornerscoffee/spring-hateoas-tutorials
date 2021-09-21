package com.safecornerscoffee.links.employee;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employees")
    public CollectionModel<EntityModel<Employee>> all() {
        List<Employee> employees = repository.findAll();
        return CollectionModel.of(employees.stream().map(assembler::toModel).collect(Collectors.toList()),
            linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @PostMapping("/employees")
    public EntityModel<Employee> newEmployee(@RequestBody Employee newEmployee) {
        return assembler.toModel(repository.save(newEmployee));
    }

    @GetMapping("/employees/{id}")
    public EntityModel<Employee> one(@PathVariable Long id) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        return assembler.toModel(employee);
    }

    @PutMapping("/employees/{id}")
    public EntityModel<Employee> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                   newEmployee.setId(id);
                   return repository.save(newEmployee);
                });

        return assembler.toModel(updatedEmployee);
    }

    @DeleteMapping("/employees/{id}")
    public void delete(@PathVariable Long id) {
        repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        repository.deleteById(id);
    }
}
