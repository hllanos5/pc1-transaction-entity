package com.nttdata.transaction.microservice.service;

import com.nttdata.transaction.microservice.domain.Transaction;
import com.nttdata.transaction.microservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImp implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Mono<Transaction> save(Mono<Transaction> client) {
        return client.flatMap(transactionRepository::insert);
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Flux<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Flux<Transaction> findByClientId(String clientId) {
        return transactionRepository.findByClientId(clientId);
    }

    @Override
    public Mono<Transaction> update(String id, Mono<Transaction> transaction) {
        return transactionRepository.findById(id)
                .flatMap(c -> transaction)
                .doOnNext(e -> e.setId(id))
                .flatMap(transactionRepository::save);

    }

    @Override
    public Mono<Void> delete(Transaction transaction) {
        return transactionRepository.delete(transaction);
    }
}
