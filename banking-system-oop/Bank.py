from __future__ import annotations

from pathlib import Path
from typing import List, Union

from CAccount import CAccount
from SAccount import SAccount
from Customer import Customer

AccountType = Union[CAccount, SAccount]


class Bank:
    @staticmethod
    def main() -> None:
        account_list: List[AccountType] = []
        customer_list: List[Customer] = []

        base_dir = Path(__file__).parent
        caccounts_path = base_dir / "CAccounts.txt"
        saccounts_path = base_dir / "SAccounts.txt"
        customers_path = base_dir / "Customers.txt"
        receipts_path = base_dir / "BankReceipts.txt"

        # -------------------------
        # Load checking accounts
        # -------------------------
        with caccounts_path.open("r", encoding="utf-8") as fr:
            for ln in fr:
                ln = ln.strip()
                if not ln:
                    continue
                temp = ln.split(";")

                # Format:
                # accNo;accType;bal
                # accNo;accType;bal;minAmt
                if len(temp) == 3:
                    ca = CAccount(temp[0], temp[1], float(temp[2]))
                else:
                    ca = CAccount(temp[0], temp[1], float(temp[2]), float(temp[3]))
                account_list.append(ca)

        # -------------------------
        # Load savings accounts
        # -------------------------
        with saccounts_path.open("r", encoding="utf-8") as fr:
            for ln in fr:
                ln = ln.strip()
                if not ln:
                    continue
                temp = ln.split(";")

                # Format:
                # accNo;accType;bal
                # accNo;accType;bal;maxAmt
                if len(temp) == 3:
                    sa = SAccount(temp[0], temp[1], float(temp[2]))
                else:
                    sa = SAccount(temp[0], temp[1], float(temp[2]), float(temp[3]))
                account_list.append(sa)

        # -------------------------
        # Load customers + assign accounts 
        # -------------------------
        idx = 0
        with customers_path.open("r", encoding="utf-8") as fr:
            for ln in fr:
                ln = ln.strip()
                if not ln:
                    continue
                temp = ln.split(";")

                # custNo;name;age;city
                # account assignment is index-based 
                new_cust = Customer(int(temp[0]), temp[1], int(temp[2]), temp[3], account_list[idx])
                customer_list.append(new_cust)
                idx += 1

        # -------------------------
        # Display customers 
        # -------------------------
        for i, person in enumerate(customer_list, start=1):
            print(f"Customer {i}")
            print(f"Customer number: {person.getCustNo()}")
            print(f"Customer name: {person.getCustNm()}")
            print(f"Customer age: {person.getAge()}")
            print(f"Customer city: {person.getCity()}")
            print(person.getAccObj())
            print("---------------------------------------")

        # -------------------------
        # Display first 3 checking accounts 
        # -------------------------
        for chq_details in range(0, 3):
            acc = account_list[chq_details]
            print(f"The account number for this account is: {acc.getAccNo()}")
            print(f"This account type is: {acc.getAccType()}")
            print(f"The current balance of this account is: {acc.getBal()}")
            # These first 3 are checking accounts
            if isinstance(acc, CAccount):
                print(f"The minimum deposit amount is: {acc.getMinAmt()}")
            print("---------------------------------------")

        # -------------------------
        # Display next 3 savings accounts 
        # -------------------------
        for savings_details in range(3, 6):
            acc = account_list[savings_details]
            print(f"The account number for this account is: {acc.getAccNo()}")
            print(f"This account type is: {acc.getAccType()}")
            print(f"The current balance of this account is: {acc.getBal()}")
            # These are savings accounts
            if isinstance(acc, SAccount):
                print(f"The maximum deposit amount is: {acc.getMaxAmt()}")
            print("---------------------------------------")

        # -------------------------
        # Perform deposits 
        # -------------------------
        account_list[0].deposit()
        account_list[5].deposit()

        account_list[0].deposit()
        account_list[5].deposit()

        # -------------------------
        # Write receipt file for two accounts 
        # -------------------------
        with receipts_path.open("w", encoding="utf-8") as fr:
            fr.write(f"Account Number: {account_list[0].getAccNo()}\n")
            fr.write(f"Account Type: {account_list[0].getAccType()}\n")
            fr.write(f"Account Balance: ${account_list[0].getBal()}\n")
            fr.write("\n--------\n\n")

            fr.write(f"Account Number: {account_list[5].getAccNo()}\n")
            fr.write(f"Account Type: {account_list[5].getAccType()}\n")
            fr.write(f"Account Balance: ${account_list[5].getBal()}\n")
            fr.write("\n--------\n")

        print("A receipt file has been made detailing these transactions.")
        print("---------")
        print("Thank you & Goodbye!")


if __name__ == "__main__":
    Bank.main()

