# In-Memory Ledger System Overview

This ledger system provides a simple yet effective way to manage financial transactions and accounts using double-entry bookkeeping principles. Through RESTful APIs, it allows creating and retrieving accounts, recording transactions with debit and credit entries, and querying transaction history and account balances. Each transaction consists of multiple entries that debit or credit different accounts, ensuring that the total debits equal total credits, thereby maintaining the accounting balance. The system stores data in-memory for fast access and supports paginated transaction history retrieval to efficiently handle large datasets.

## Key Features and API Capabilities

- **Account Management**
    - Create accounts with specific types (Asset, Liability)
    - Retrieve the list of all accounts
    - Query the current balance of an account

- **Transaction Handling**
    - Create transactions with multiple debit and credit entries
    - Each transaction updates account balances accordingly
    - Transactions include descriptions and timestamps for auditability

- **Transaction History**
    - Retrieve the full history of transactions with detailed entries

- **Double-Entry Bookkeeping Compliance**
    - Ensures every transaction maintains balance by equalizing debits and credits
    - Supports clear financial tracking and reporting

# Trade-offs in Implementing the In-Memory Ledger System

- **In-Memory Storage vs. Persistence**
    - *Pro:* Fast access and simple implementation
    - *Con:* Data lost on restart, no long-term durability or recovery

- **Simplicity vs. Scalability**
    - *Pro:* Easier to develop and maintain for small to medium datasets
    - *Con:* Without pagination, retrieving large transaction histories can cause performance and memory issues

- **Fetching All Transactions at Once**
    - *Pro:* Simpler API and client logic since all data is returned in one call
    - *Con:* Poor performance and increased network load with large datasets; slower response times

- **Double-Entry Bookkeeping Enforcement**
    - *Pro:* Maintains financial integrity
    - *Con:* Complexity in validation remains regardless of pagination

- **Concurrency and Consistency**
    - *Pro:* Use of concurrent collections helps thread safety
    - *Con:* Without pagination, large concurrent requests can still degrade performance

- **Limited Flexibility for Growth**
    - *Pro:* Faster initial development
    - *Con:* Future need for pagination and filtering may require significant refactoring

- **No Idempotency Handling**
    - *Pro:* Simpler transaction creation logic
    - *Con:* Risk of duplicate transactions on retries, leading to inconsistent ledger state

- **No Authentication and Authorization (Authn/Authz)**
    - *Pro:* Easier to develop and test initially
    - *Con:* No access control — anyone can read or modify accounts and transactions, posing serious security risks

- **No Logging or Auditing**
    - *Pro:* Less overhead and simpler codebase
    - *Con:* Difficult to trace changes, diagnose issues, or meet compliance and audit requirements

# Assumptions

1. Using `ConcurrentHashMap` for storing Account and Transaction entities is sufficient for your initial use case, avoiding database complexity.
2. Data will be lost when the application restarts — acceptable for dev/testing.
3. Account and Transaction IDs are generated using `AtomicInteger`. (No external UUIDs or distributed ID generation used.)
4. Transactions are valid only if total debits equal total credits. No additional business rules (e.g., account type constraints) were enforced.
5. Account types are treated as plain strings (e.g., `"Asset"`, `"Liability"`) with no enforced enum or validation logic.
6. Basic error handling:
    7. Errors like invalid account ID or unbalanced transactions return HTTP 400 (Bad Request).
    8. Generic server exceptions return HTTP 500 (Internal Server Error).
    9. No granular error messages or error codes implemented.
10. Internal domain models (`Account`, `Transaction`, etc.) are separate from OpenAPI-generated DTOs. We use mappers (manually or via facade) to convert between them.
11. The `date` field is set based on request input and not auto-assigned or validated.
12. For simplicity, `Double` was used instead of `BigDecimal`. This may cause rounding issues in real-world financial systems.
13. Clear layering using Controller → Facade → Service → Mapper. Facades help reduce dependency clutter in controllers.
14. Currency system is not considered, no currency conversion supported.

## Project Stack

- **Java 17** — The project is built using Java 17.
- **Spring Boot** — Used as the primary framework to build a robust and scalable web server for the ledger system.
- **Maven** — The build tool used for dependency management, project compilation, packaging, and running tests.

---

## Running the Project

The project includes two helper scripts:

- `run.sh` — This script ensures Java 17 and Maven are installed on your system, builds the project, and starts the web server.
- `test.sh` — Once the web server is up and running, this script executes API tests that simulate typical use cases for the banking ledger system.

---

### Prerequisites

- A Unix-like environment (Linux, WSL in Windows).
- `sudo` privileges (required to install Java 17 and Maven if they are missing).

---

### How to Run

1. **Start the Web Server**

   Run the `run.sh` script from the project root directory:

   ```bash
   ./run.sh

2. **Run curl requests simulating ledger entries for banking system**

   Run the `test.sh` script from the project root directory:

  ```bash
  ./test.sh
