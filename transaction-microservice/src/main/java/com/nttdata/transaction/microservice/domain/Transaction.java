package com.nttdata.transaction.microservice.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Getter
@Setter
@Document(collection  = "transaction")
public class Transaction {
    @Id
    private String id;
    private String productId;
    private String type;
    private Double amount;
    @CreatedDate
    private Date createdAt;
}
