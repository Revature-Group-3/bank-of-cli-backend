package com.revature.bankofcli.dto.transaction;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(  // Adds `type` property to indicate bank transaction type
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({ // Defines the values `type` can take depending on bank transaction type
    @JsonSubTypes.Type(value = DepositRequest.class, name = "DEPOSIT"),
    @JsonSubTypes.Type(value = WithdrawalRequest.class, name = "WITHDRAWAL"),
    @JsonSubTypes.Type(value = TransferRequest.class, name = "TRANSFER")
})
public abstract class AbstractTransactionRequest {
    @NotNull(message = "Please enter an amount.")
    @Positive(message = "Please enter a valid amount.")
    @Max(value = 1000000, message = "The transaction amount exceeds the maximum allowed limit of $10,000.")
    private Long amountInCents;

    private String memo;

    public Long getAmountInCents() {
        return amountInCents;
    }

    public void setAmountInCents(Long amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
