package es.zed.shared.domain.model;

public record Message<B>(String address, B body) { }
