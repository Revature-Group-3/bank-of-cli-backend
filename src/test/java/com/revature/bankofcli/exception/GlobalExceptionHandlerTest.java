package com.revature.bankofcli.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

class GlobalExceptionHandlerTest {

    // A fake request DTO with the same kind of rules as our real ones
    record TestAmountRequest(
            @NotNull(message = "Please enter an amount.")
            @Max(value = 1000000, message = "The transaction amount exceeds the maximum allowed limit of $10,000.")
            Long amountInCents) {}

    // A fake controller whose only job is to throw, so we can see what the handler returns
    @RestController
    static class ThrowingController {
        @GetMapping("/test/insufficient-funds")
        void insufficientFunds() { throw new InsufficientFundsException(); }

        @GetMapping("/test/recipient-not-found")
        void recipientNotFound() { throw new RecipientNotFoundException(); }

        @GetMapping("/test/unexpected")
        void unexpected() { throw new IllegalStateException("something broke"); }

        @PostMapping("/test/validated")
        void validated(@Valid @RequestBody TestAmountRequest request) { }
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void apiExceptionReturnsItsStatusAndContractMessage() throws Exception {
        mockMvc.perform(get("/test/insufficient-funds"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Insufficient funds. The withdrawal/transfer amount exceeds your available balance."))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void notFoundApiExceptionReturns404() throws Exception {
        mockMvc.perform(get("/test/recipient-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("The recipient account could not be found."));
    }

    @Test
    void unexpectedExceptionReturnsGeneric500() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An internal error occurred. Please try again later."));
    }

    // Send $20,000 and expect the contracts limit message
    @Test
    void validationErrorReturnsTheAnnotationMessage() throws Exception {
        mockMvc.perform(post("/test/validated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amountInCents\": 2000000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("The transaction amount exceeds the maximum allowed limit of $10,000."));
    }

    // Send no amount and expect the @NotNull message
    @Test
    void missingFieldReturnsItsNotNullMessage() throws Exception {
        mockMvc.perform(post("/test/validated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Please enter an amount."));
    }
}