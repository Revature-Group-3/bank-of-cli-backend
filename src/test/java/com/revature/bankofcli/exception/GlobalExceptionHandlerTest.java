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

class GlobalExceptionHandlerTest {

    // A fake controller whose only job is to throw, so we can see what the handler returns
    @RestController
    static class ThrowingController {
        @GetMapping("/test/insufficient-funds")
        void insufficientFunds() { throw new InsufficientFundsException(); }

        @GetMapping("/test/recipient-not-found")
        void recipientNotFound() { throw new RecipientNotFoundException(); }

        @GetMapping("/test/unexpected")
        void unexpected() { throw new IllegalStateException("something broke"); }
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
}