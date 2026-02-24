# Banking System (OOP Foundations)

A simple command-line banking simulation built as an early object-oriented programming project.

It loads customer and account data from text files, displays account details, performs deposit transactions with basic validation rules & writes a receipt file.

## Concepts Demonstrated
- Classes & objects (OOP fundamentals)
- Encapsulation (private fields + getters)
- Polymorphism (`deposit()` implemented differently per account type)
- Aggregation (Customer holds an Account object)
- File I/O (reading `.txt` data, writing a receipt)

## Project Structure
- `Bank.py` – Main entry point (loads data, prints details, runs transactions)
- `CAccount.py` – Checking account (minimum deposit rule)
- `SAccount.py` – Savings account (maximum deposit rule)
- `Customer.py` – Customer model (aggregates an account)
- `CAccounts.txt`, `SAccounts.txt`, `Customers.txt` – Input datasets
- `BankReceipts.txt` – Generated output receipt (created when the program runs)

## How to Run
1. Ensure the `.py` files and the `.txt` data files are in the same folder.
2. Run:

```bash
python Bank.py
