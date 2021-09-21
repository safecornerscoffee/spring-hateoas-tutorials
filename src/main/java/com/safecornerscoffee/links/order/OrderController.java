package com.safecornerscoffee.links.order;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class OrderController {

    private final OrderRepository repository;
    private final OrderModelAssembler assembler;

    public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all() {
        List<EntityModel<Order>> orders = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityModel<Order>> place(@RequestBody Order order) {

        order.setStatus(OrderStatus.IN_PROGRESS);
        EntityModel<Order> entityModel = assembler.toModel(repository.save(order));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {

        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.CANCELLED);
            Order savedOrder = repository.save(order);
            return ResponseEntity.ok(assembler.toModel(savedOrder));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(methodNotAllowedOn(order));

    }

    private Problem methodNotAllowedOn(Order order) {
        return Problem.create()
                .withTitle("Method not allowed")
                .withDetail("Can not cancel an order that is in the " + order.getStatus() + " status");
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {

        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.COMPLETED);
            return ResponseEntity.ok(assembler.toModel(repository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(methodNotAllowedOn(order));
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<EntityModel<Order>> replaceOrder(@RequestBody Order newOrder, @PathVariable Long id) {
        Order updatedOrder = repository.findById(id)
                .map(order -> {
                    order.setDescription(newOrder.getDescription());
                    order.setStatus(newOrder.getStatus());
                    return repository.save(order);
                })
                .orElseGet(() -> {
                    newOrder.setId(id);
                    return repository.save(newOrder);
                });

        EntityModel<Order> entityModel = assembler.toModel(updatedOrder);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
