from __future__ import annotations


class SAccount:
    """
    Savings account.

    Rules:
    - Deposits must be <= maximum amount (maxAmt).
    """

    bsbNo = 1246  # class variable (static)

    def __init__(self, accNo: str, accType: str, bal: float, maxAmt: float = 500.00):
        self.__accNo = str(accNo)
        self.__accType = str(accType)
        self.__bal = float(bal)
        self.__maxAmt = float(maxAmt)

    def getAccNo(self) -> str:
        return self.__accNo

    def getAccType(self) -> str:
        return self.__accType

    def getBal(self) -> float:
        # Keep consistent with CAccount: return a numeric balance.
        return self.__bal

    def getMaxAmt(self) -> float:
        return self.__maxAmt

    def deposit(self) -> None:
        """
        Prompts the user for a deposit value and applies savings rules.
        """
        try:
            value = float(input("How much would you like to deposit: ").strip())
        except ValueError:
            print("Invalid input. Please enter a number.")
            return

        if value > self.__maxAmt:
            print(f"Account Number: {self.__accNo}")
            print(f"Deposit: {value}")
            print(f"Max Amount: {self.__maxAmt}")
            print("Deposit exceeded maximum amount")
            return

        self.__bal += value
        print(f"Account Number: {self.__accNo}")
        print(f"Deposit: {value}")
        print(f"Balance: {self.__bal}")
        print("Success!")

    def __str__(self) -> str:
        return (
            f"Account Number: {self.__accNo}\n"
            f"Account Type: {self.__accType}\n"
            f"Account Balance: ${self.__bal}\n"
            f"Maximum Amount: ${self.__maxAmt}\n"
            f"Branch Number: {self.bsbNo}"
        )
