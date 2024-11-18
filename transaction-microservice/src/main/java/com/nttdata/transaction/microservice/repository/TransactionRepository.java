package com.nttdata.transaction.microservice.repository;

import com.nttdata.transaction.microservice.domain.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
    Mono<Transaction> findByProductId(String productId);
}
