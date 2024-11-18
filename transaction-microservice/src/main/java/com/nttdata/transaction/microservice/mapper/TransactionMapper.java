package com.nttdata.transaction.microservice.mapper;

import com.nttdata.transaction.model.Transaction;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Date;

@Component
public class TransactionMapper implements EntityMapper<Transaction, com.nttdata.transaction.microservice.domain.Transaction>{

    @Override
    public com.nttdata.transaction.microservice.domain.Transaction toDocument(Transaction model) {
        com.nttdata.transaction.microservice.domain.Transaction transaction= new com.nttdata.transaction.microservice.domain.Transaction();
        BeanUtils.copyProperties(model, transaction);
        transaction.setCreatedAt(new Date());
        return transaction;
    }

    @Override
    public Transaction toModel(com.nttdata.transaction.microservice.domain.Transaction domain) {
        Transaction transaction= new Transaction();
        BeanUtils.copyProperties(domain, transaction);
        transaction.setCreateAt(domain.getCreatedAt().toInstant().atOffset(ZoneOffset.UTC));
        return transaction;
    }
}
