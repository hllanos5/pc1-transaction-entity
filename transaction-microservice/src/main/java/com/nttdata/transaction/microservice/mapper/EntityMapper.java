package com.nttdata.transaction.microservice.mapper;

public interface EntityMapper<D,E>{
    E toDocument(D model);
    D toModel(E domain);
}
