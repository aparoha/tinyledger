#!/bin/bash

set -euo pipefail

API_URL="http://localhost:8080/api/v1/ledger"

echo "# Creating accounts"

echo "## Create John Doe's Savings Account"
curl -s -X POST "${API_URL}/accounts?name=John+Doe+Savings&type=Liability" -w "\nHTTP Status: %{http_code}\n\n"

echo "## Create Jane Smith's Checking Account"
curl -s -X POST "${API_URL}/accounts?name=Jane+Smith+Checking&type=Liability" -w "\nHTTP Status: %{http_code}\n\n"

echo "## Create Alex Brown's Business Account"
curl -s -X POST "${API_URL}/accounts?name=Alex+Brown+Business&type=Liability" -w "\nHTTP Status: %{http_code}\n\n"

echo "## Create Sarah Green's Investment Account"
curl -s -X POST "${API_URL}/accounts?name=Sarah+Green+Investment&type=Liability" -w "\nHTTP Status: %{http_code}\n\n"

echo "## Create Michael White's Emergency Fund Account"
curl -s -X POST "${API_URL}/accounts?name=Michael+White+Emergency+Fund&type=Liability" -w "\nHTTP Status: %{http_code}\n\n"

echo "## Create Bank's cash account"
curl -s -X POST "${API_URL}/accounts?name=Bank+Cash+Account&type=Asset" -w "\nHTTP Status: %{http_code}\n\n"

echo "# Retrieve all accounts"
curl -s -X GET "${API_URL}/accounts" -H "Accept: application/json" | jq .
echo

echo "# Deposit \$1000 into John Doe Savings Account"
curl -s -X POST "${API_URL}/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Deposit to John Doe Savings",
    "entries": [
      {
        "account": {"id": 6, "name": "Bank Cash Account", "type": "Liability"},
        "amount": 1000.00,
        "entryType": "Debit"
      },
      {
        "account": {"id": 1, "name": "John Doe Savings", "type": "Asset"},
        "amount": 1000.00,
        "entryType": "Credit"
      }
    ]
  }' -w "\nHTTP Status: %{http_code}\n\n"

echo "# Deposit \$1500 into Jane Smith Checking Account"
curl -s -X POST "${API_URL}/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Deposit to Jane Smith Checking",
    "entries": [
      {
        "account": {"id": 6, "name": "Bank Cash Account", "type": "Liability"},
        "amount": 1500.00,
        "entryType": "Debit"
      },
      {
        "account": {"id": 2, "name": "Jane Smith Checking", "type": "Asset"},
        "amount": 1500.00,
        "entryType": "Credit"
      }
    ]
  }' -w "\nHTTP Status: %{http_code}\n\n"

echo "# Check balances"
echo "Balance of John Doe Savings Account:"
curl -s -X GET "${API_URL}/accounts/1/balance" -H "Accept: application/json"
echo -e "\n"

echo "Balance of Jane Smith Checking Account:"
curl -s -X GET "${API_URL}/accounts/2/balance" -H "Accept: application/json"
echo -e "\n"

echo "# Transfer \$500 from John Doe Savings to Jane Smith Checking"
curl -s -X POST "${API_URL}/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Transfer from John Doe Savings to Jane Smith Checking",
    "entries": [
      {
        "account": {"id": 1, "name": "John Doe Savings", "type": "Asset"},
        "amount": 500.00,
        "entryType": "Debit"
      },
      {
        "account": {"id": 2, "name": "Jane Smith Checking", "type": "Asset"},
        "amount": 500.00,
        "entryType": "Credit"
      }
    ]
  }' -w "\nHTTP Status: %{http_code}\n\n"

echo "# Balances after transfer"
echo "Balance of John Doe Savings Account:"
curl -s -X GET "${API_URL}/accounts/1/balance" -H "Accept: application/json"
echo -e "\n"

echo "Balance of Jane Smith Checking Account:"
curl -s -X GET "${API_URL}/accounts/2/balance" -H "Accept: application/json"
echo -e "\n"

echo "# Full Transaction History"
curl -s -X GET "${API_URL}/transactions/history" -H "Accept: application/json" | jq .
echo
