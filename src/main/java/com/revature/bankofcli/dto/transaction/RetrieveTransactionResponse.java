package com.revature.bankofcli.dto.transaction;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(  // Adds `type` property to indicate bank transaction type
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({ // Defines the values `type` can take depending on bank transaction type
    @JsonSubTypes.Type(value = RetrieveDepositResponse.class, name = "DEPOSIT"),
    @JsonSubTypes.Type(value = RetrieveWithdrawalResponse.class, name = "WITHDRAWAL"),
    @JsonSubTypes.Type(value = RetrieveTransferResponse.class, name = "TRANSFER")
})
public abstract class RetrieveTransactionResponse {
    private long id;
    private long amountInCents;

    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", 
        timezone = "UTC"
    )
    private OffsetDateTime timestamp;

    public RetrieveTransactionResponse(long id, long amountInCents, OffsetDateTime timestamp) {
        this.id = id;
        this.amountInCents = amountInCents;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public long getAmountInCents() {
        return amountInCents;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }
}
