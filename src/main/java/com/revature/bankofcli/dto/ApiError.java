package com.revature.bankofcli.dto;

//javadoc comment allows you to hover over ApiError anywhere in the program and view the record without opening the file
/** Body of every error response. Matches docs/json_format/unsuccessful_responses/. */
public record ApiError(int status, String message, String timestamp) {}