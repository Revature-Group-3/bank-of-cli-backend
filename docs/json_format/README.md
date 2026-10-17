The `json_format/` directory standardizes JSON formats for communication with the API. Request JSON formats are standardized in `json_format/requests/`, while response JSON formats are standardized in `json_format/successful_responses/` and `json_format/unsuccessful_responses/` for successful and unsuccessful responses, respectively.

Important: All three "get_[transaction type].json" will be returned by the SAME endpoint.

Notes on JSON formats:
- `successful_responses/most_recent_transactions_by_account.json` contains a list of transactions; this list can be arbitrarily long, and the file contains three example transactions solely for demonstration purposes. To make a request, we use the URL parameters specified in `requests/most_recent_transactions_by_account.txt`. Note that these URL parameters are subject to change, and we may implement additional URL parameters in the future to clarify how many records to retrieve, page size, etc.

Notes on JSON attribute data types and values:
- IDs are non-negative long integers.
- Money is represented using non-negative integers of *cents*, rather than floating-point dollar values. This is to avoid floating-point precision issues.
- Emails are represented as *case-insensitive* strings that follow proper email format.
- The transaction `type` attribute is a String that can have only values of `"DEPOSIT"`, `"WITHDRAWAL"`, or `"TRANSFER"`.
- The `timestamp` attribute, present in transaction and the unsuccessful responses, is a String containing a formatted timestamp (e.g. `"2024-06-01T00:00:00Z"`).
