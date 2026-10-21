package com.revature.bankofcli.dto;

/** Body of every error response. Matches docs/json_format/unsuccessful_responses/. */
public record ApiError(int status, String message, String timestamp) {}