# Blockchain Assessment 2 Project

A Python implementation of a secure DLT-based inventory management system for a blockchain technology group assessment.

The system simulates four inventory nodes and demonstrates secure record insertion, consensus-based approval, secure record retrieval, multi-signature verification, and encrypted response delivery.

## Features

### Part 1: Secure Record Insertion

- RSA-based record signing and verification
- Encrypted record transfer between inventory nodes
- Unanimous consensus for approving new records
- Local inventory updates after successful approval

### Part 2: Secure Record Retrieval

- Query submission by a procurement officer
- Inventory agreement check across all nodes
- Harn-style multi-signature approval
- RSA-encrypted response delivery and user-side decryption

## Technologies Used

- Python
- RSA cryptographic calculations
- Custom hashing and modular arithmetic
- Command-line interface

## Project Files

- `Inventory.py` - shared inventory, record, RSA, signing, verification, and transfer logic
- `part1.py` - secure record insertion and consensus workflow
- `part2.py` - secure record retrieval, multi-signature approval, and encrypted response workflow
- `packageAtoB.txt` - sample encrypted package output

## Contributions

This was completed as a group project.

## Note

This project is a local simulation for academic demonstration purposes and does not implement a full distributed network.