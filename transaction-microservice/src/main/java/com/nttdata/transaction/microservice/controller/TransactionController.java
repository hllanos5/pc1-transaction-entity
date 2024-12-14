package com.nttdata.transaction.microservice.controller;

import com.mongodb.DuplicateKeyException;
import com.nttdata.transaction.microservice.api.TransactionApi;
import com.nttdata.transaction.microservice.mapper.TransactionMapper;
import com.nttdata.transaction.microservice.service.TransactionService;
import com.nttdata.transaction.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
public class TransactionController implements TransactionApi {

    private static final String TIMESTAMP = "timestamp";
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> createTransaction(Mono<Transaction> transaction, ServerWebExchange exchange) {
        Map<String, Object> response =  new HashMap<>();
        return transactionService.save(transaction.map(transactionMapper::toDocument))
                .map(transactionMapper::toModel)
                .map(c -> {
                    response.put("transaction",c);
                    response.put("message", "Successful transaction saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }).onErrorResume(getThrowableMonoFunction(response));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteTransaction(String id, ServerWebExchange exchange) {
        return transactionService.findById(id)
                .flatMap(c -> transactionService.delete(c)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> updateTransaction(String id, Mono<Transaction> transaction, ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        return transactionService.update(id, transaction.map(transactionMapper::toDocument))
                .map(transactionMapper::toModel)
                .map(c -> {
                    response.put("transaction", c);
                    response.put("message", "Successful transaction saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .onErrorResume(WebExchangeBindException.class, getThrowableMonoFunction(response))
                .onErrorResume(DuplicateKeyException.class, getThrowableDuplicate(response))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public  Mono<ResponseEntity<Flux<Map<String, Object>>>> findAllTransaction(ServerWebExchange exchange) {
        Flux<com.nttdata.transaction.microservice.domain.Transaction> clientsFlux = transactionService.findAll();

        Flux<Map<String, Object>> productsMapFlux = clientsFlux.map(transaction -> {
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("id", transaction.getId());
            transactionMap.put("clientId", transaction.getClientId());
            transactionMap.put("type", transaction.getType());
            transactionMap.put("amount", transaction.getAmount());
            transactionMap.put("createAt", transaction.getCreatedAt());
            return transactionMap;
        });

        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .body(productsMapFlux));

    }

    @Override
    public Mono<ResponseEntity<Flux<Map<String, Object>>>> getTransactionByClientId(String clientId, ServerWebExchange exchange) {
        Flux<com.nttdata.transaction.microservice.domain.Transaction> clientsFlux = transactionService.findByClientId(clientId);

        Flux<Map<String, Object>> transactionMapFlux = clientsFlux.map(transaction -> {
            Map<String, Object> transactionMap = new HashMap<>();
            transactionMap.put("id", transaction.getId());
            transactionMap.put("clientId", transaction.getClientId());
            transactionMap.put("type", transaction.getType());
            transactionMap.put("amount", transaction.getAmount());
            transactionMap.put("createAt", transaction.getCreatedAt());
            return transactionMap;
        });

        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionMapFlux));

    }

    @Override
    public Mono<ResponseEntity<Transaction>> getTransactionById(String id, ServerWebExchange exchange) {
        return transactionService.findById(id)
                .map(transactionMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableMonoFunction(Map<String, Object> response){
        return t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "Campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }

    private static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableDuplicate(Map<String, Object> response){
        return t -> Mono.just(t).cast(DuplicateKeyException.class)
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }

}
