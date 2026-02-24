from __future__ import annotations


class CAccount:
    """
    Checking account.

    Rules:
    - Deposits must be >= minimum amount (minAmt).
    """

    bsbNo = 1246  # class variable (static)

    def __init__(self, accNo: str, accType: str, bal: float, minAmt: float = 50.00):
        self.__accNo = str(accNo)
        self.__accType = str(accType)
        self.__bal = float(bal)
        self.__minAmt = float(minAmt)

    def getAccNo(self) -> str:
        return self.__accNo

    def getAccType(self) -> str:
        return self.__accType

    def getBal(self) -> float:
        return self.__bal

    def getMinAmt(self) -> float:
        return self.__minAmt

    def deposit(self) -> None:
        """
        Prompts the user for a deposit value and applies checking rules.
        """
        try:
            value = float(input("How much would you like to deposit: ").strip())
        except ValueError:
            print("Invalid input. Please enter a number.")
            return

        if value < self.__minAmt:
            print(f"Account Number: {self.__accNo}")
            print(f"Deposit: {value}")
            print(f"Minimum Amount: {self.__minAmt}")
            print("Insufficient funds")
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
            f"Minimum Amount: ${self.__minAmt}\n"
            f"Branch Number: {self.bsbNo}"
        )
            

     
    


         
            
                        



    


        

                                                     


    