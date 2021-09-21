package com.safecornerscoffee.links.order;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super(String.format("Could not found Order{id=%s}", id));
    }
}
