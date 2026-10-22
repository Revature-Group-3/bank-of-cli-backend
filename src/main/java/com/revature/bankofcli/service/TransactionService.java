package com.revature.bankofcli.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.revature.bankofcli.dto.transaction.ConductTransactionRequest;
import com.revature.bankofcli.dto.transaction.DepositMoneyRequest;
import com.revature.bankofcli.dto.transaction.RetrieveDepositResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionHistoryResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransactionResponse;
import com.revature.bankofcli.dto.transaction.RetrieveTransferResponse;
import com.revature.bankofcli.dto.transaction.RetrieveWithdrawalResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulDepositResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulTransferResponse;
import com.revature.bankofcli.dto.transaction.SuccessfulWithdrawalResponse;
import com.revature.bankofcli.dto.transaction.TransferMoneyRequest;
import com.revature.bankofcli.dto.transaction.WithdrawMoneyRequest;

import java.util.List;

import com.revature.bankofcli.domain.Account;
import com.revature.bankofcli.domain.BankTransaction;
import com.revature.bankofcli.domain.Deposit;
import com.revature.bankofcli.domain.Transfer;
import com.revature.bankofcli.domain.Withdrawal;

import com.revature.bankofcli.repository.AccountRepository;
import com.revature.bankofcli.repository.BankTransactionRepository;

import com.revature.bankofcli.exception.AccountNotFoundException;
import com.revature.bankofcli.exception.AmountExceedsLimitException;
import com.revature.bankofcli.exception.InsufficientFundsException;
import com.revature.bankofcli.exception.InvalidAmountException;
import com.revature.bankofcli.exception.RecipientNotFoundException;
import com.revature.bankofcli.exception.SelfTransferException;

@Service
public class TransactionService {
    // Repositories
    private final BankTransactionRepository bankTransactionRepository;
    private final AccountRepository accountRepository;

    // Dependency injection for repositories
    public TransactionService(
        BankTransactionRepository bankTransactionRepository,
        AccountRepository accountRepository
    ) {
        this.bankTransactionRepository = bankTransactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Deposit
     * 
     * @Transactional groups the balance update and the new bank_transaction row into one unit:
     * either both happen or neither does.
     */
    @Transactional
    public SuccessfulDepositResponse deposit(Long accountId, DepositMoneyRequest request) {
        Account account = transactionSetup(accountId, request);

        long amountInCents = request.getAmountInCents();

        // Update Account's Balance
        account.setBalanceInCents(account.getBalanceInCents() + amountInCents);

        // Save and return
        return bankTransactionRepository.save(new Deposit(account, amountInCents, null));
    }

    // Withdraw
    @Transactional
    public SuccessfulWithdrawalResponse withdraw(long accountId, WithdrawMoneyRequest request) {
        Account account = transactionSetup(accountId, request);

        // Check if amount is greater than what you currently have
        validateSufficientFunds(account, request);

        // Persist withdrawal record
        Withdrawal withdrawal = bankTransactionRepository.save(new Withdrawal(
            account,
            request.getAmountInCents(),
            null
        ));

        // Set new balance
        account.setBalanceInCents(account.getBalanceInCents() - request.getAmountInCents());

        // Successful withdrawal response
        return new SuccessfulWithdrawalResponse(
            withdrawal.getId(),
            withdrawal.getAmountInCents(),
            account.getBalanceInCents(),
            withdrawal.getTimestamp(),
            account.getEmail()
        );
    }

    @Transactional
    public SuccessfulTransferResponse transfer(Long accountId, TransferMoneyRequest request) {
        Account sender = transactionSetup(accountId, request);

        long amountInCents = request.getAmountInCents();

        if (sender.getEmail().equalsIgnoreCase(request.getRecipientAccountEmail())) {
            throw new SelfTransferException();
        }

        // Error Code 404
        Account recipient = accountRepository.findByEmail(request.getRecipientAccountEmail())
                .orElseThrow(RecipientNotFoundException::new);

        // Business logic / checks
        // Additional Checks
        // error code 400
        if (amountInCents > sender.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }

        // Set new balance
        sender.setBalanceInCents(sender.getBalanceInCents() - amountInCents);
        recipient.setBalanceInCents(recipient.getBalanceInCents() + amountInCents);

        // Persist to database
        return bankTransactionRepository.save(new Transfer(sender, recipient, amountInCents, null));

    }

    @Transactional(readOnly = true)
    public RetrieveTransactionHistoryResponse getTransactionHistory(long accountId) {
        // Should be allowed to be empty
        List<BankTransaction> pastBankTransactions = bankTransactionRepository.findTransactionHistory(accountId);

        // Map to retrieval DTOs
        return new RetrieveTransactionHistoryResponse(mapBankTransactionsToRetrievalDtos(pastBankTransactions));
    }

    /*
     * HELPER METHODS
    */

    // General bank transaction setup:
    // Validate general bank transaction rules and get account
    private Account transactionSetup(long accountId, ConductTransactionRequest request) {
        validateBankTransactionRequest(request);
        return getAccountForBankTransaction(accountId);
    }

    // Validate bank transaction request; this checks general business rules applying to all bank transaction types 
    private void validateBankTransactionRequest(ConductTransactionRequest request) {
        // Error code 400
        if (request.getAmountInCents() <= 0) {
            throw new InvalidAmountException();
        }

        // Error code 400
        if (request.getAmountInCents() > 1_000_000) {
            throw new AmountExceedsLimitException();
        }
    }

    // Validate that account has sufficient funds for withdrawal
    private void validateSufficientFunds(Account account, WithdrawMoneyRequest request) {
        // Error code 400
        if (request.getAmountInCents() > account.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }
    }

    // Validate that sender account has sufficient funds for transfer
    private void validateSufficientFunds(Account senderAccount, TransferMoneyRequest request) {
        // Error code 400
        if (request.getAmountInCents() > senderAccount.getBalanceInCents()) {
            throw new InsufficientFundsException();
        }
    }

    // Loads and row-locks the specified account for bank transaction
    // If account not found, we throw exception
    private Account getAccountForBankTransaction(long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(AccountNotFoundException::new);
    }

    // Maps bank transaction list to bank transaction retrieval dto list
    private List<RetrieveTransactionResponse> mapBankTransactionsToRetrievalDtos(List<BankTransaction> bankTransactions) {
        return bankTransactions.stream()
            // Map each bank transaction type to its corresponding retrieval DTO:
            .map(bankTransaction -> {
                // Deposit
                if (bankTransaction instanceof Deposit deposit)
                    { return mapDepositToRetrievalDto(deposit); }
                // Withdrawal
                else if (bankTransaction instanceof Withdrawal withdrawal)
                    { return mapWithdrawalToRetrievalDto(withdrawal); }
                // Transfer
                else if (bankTransaction instanceof Transfer transfer)
                    { return mapTransferToRetrievalDto(transfer); }
            })
            .toList();
    }

    // Maps deposit to deposit retrieval DTO
    private RetrieveDepositResponse mapDepositToRetrievalDto(Deposit deposit) {
        return new RetrieveDepositResponse(
            deposit.getId(),
            deposit.getAmountInCents(),
            deposit.getTimestamp(),
            deposit.getAccount().getEmail()
        );
    }

    // Maps withdrawal to withdrawal retrieval DTO
    private RetrieveWithdrawalResponse mapWithdrawalToRetrievalDto(Withdrawal withdrawal) {
        return new RetrieveWithdrawalResponse(
            withdrawal.getId(),
            withdrawal.getAmountInCents(),
            withdrawal.getTimestamp(),
            withdrawal.getAccount().getEmail()
        );
    }

    // Maps transfer to to transfer retrieval DTO
    private RetrieveTransferResponse mapTransferToRetrievalDto(Transfer transfer) {
        return new RetrieveTransferResponse(
            transfer.getId(),
            transfer.getAmountInCents(),
            transfer.getTimestamp(),
            transfer.getSenderAccount().getEmail(),
            transfer.getRecipientAccount().getEmail()
        );
    }
}
