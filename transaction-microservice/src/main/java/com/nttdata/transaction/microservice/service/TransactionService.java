package com.nttdata.transaction.microservice.service;

import com.nttdata.transaction.microservice.domain.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Transaction> save(Mono<Transaction> transaction);
    Mono<Transaction> findById(String id);
    Flux<Transaction> findAll();
    Mono<Transaction> findByProductId(String productId);
    Mono<Transaction> update(String id, Mono<Transaction> transaction);
    Mono<Void> delete(Transaction transaction);
}
