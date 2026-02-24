from __future__ import annotations

from typing import Union

from CAccount import CAccount
from SAccount import SAccount


AccountType = Union[CAccount, SAccount]


class Customer:
    """
    Bank customer.

    Aggregates an account object (either CAccount or SAccount).
    """

    def __init__(self, custNo: int, custNm: str, age: int, city: str, accObj: AccountType):
        self.__custNo = int(custNo)
        self.__custNm = str(custNm)
        self.__age = int(age)
        self.__city = str(city)
        self.__accObj = accObj

    def getCustNo(self) -> int:
        return self.__custNo

    def getCustNm(self) -> str:
        return self.__custNm

    def getAge(self) -> int:
        return self.__age

    def getCity(self) -> str:
        return self.__city

    def getAccObj(self) -> AccountType:
        return self.__accObj

    def __str__(self) -> str:
        return (
            f"Customer Number: {self.__custNo}\n"
            f"Customer Name: {self.__custNm}\n"
            f"Age: {self.__age}\n"
            f"City: {self.__city}\n"
            f"{self.__accObj}"
        )