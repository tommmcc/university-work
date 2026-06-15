#------------- Helper functions ---------------------
# function that takes in a string as input and converts it to an integer
# GCD find the greatest common divisor between two variables and returns the result
def gcd(a, b):
    while b != 0:
        a, b = b, a % b
    return a

# Mod inverse finds the inverse mod  using the extended euclidean algorithm
# finds the number res such that (e * res) % phi  = 1 
def mod_inverse(e, phi):
    # We will need to keep reducing a and b unitl we find the gcd = 1
    a = phi
    b = e
    # place hold variables used to track coefficeinets used to compute the inverse
    x_0 = 0
    x_1 = 1

    while b != 0:
        q = a // b
        # Updating the remainders
        temp_a = a
        a = b
        b = temp_a %b

        
        # Updating the coeffecints
        temp_x0 = x_0
        x_0 = x_1
        x_1 = temp_x0 - q * x_1
    
    # Need to check if gcd(e,phi) != 1, then inverse does not exist
    if a != 1: 
        return None
    # make the result a positive number
    res = x_0 % phi
    return res

# sorting records in array using insertion sort
def sort_records(arr):
    for i in range(1, len(arr)):
        key = arr[i]
        j = i-1

        while j >= 0 and arr[j].item_id > key.item_id:
            arr[j+1]  = arr[j]
            j -= 1
        arr[j +1] = key

    return arr

# Created simple hash function that returns a fixed length ouput hash
def hash(message, fixed_length=32):
    hash_value = 0
    prime_number  = 31
    for char in message:
        # we convert each char into its ascii value eqv 
        # then from here we multiply by a chosen prime and keep it a fixed length inputted
        hash_value = (hash_value * prime_number + ord(char)) % (2**fixed_length)
    return hash_value

# ------------------- Objects --------------------------
class Record:
    def __init__(self, item_id=None, item_qty=None, item_price=None, location=None):
        self.item_id = item_id
        self.item_qty = item_qty
        self.item_price = item_price
        self.location = location

    def get_record(self) -> str:
        item_id = self.item_id if self.item_id is not None else "null"
        item_qty = self.item_qty if self.item_qty is not None else "null"
        item_price = self.item_price if self.item_price is not None else "null"
        location = self.location if self.location is not None else "null"
        return f"{item_id},{item_qty},{item_price},{self.location}"

# We can create an object class where each invertory will have there values for p,q and e respectfully
class Inventory:
    # Initialises the object
    def __init__(self, p , q , e,name):
        self.name = name
        self.p  =  p 
        self.q  =  q 
        self.e  =  e 
        self.records = []
        self.public_keys = {}

    # Generates both public and private keys and returns them
    # Where the private key is: (n,d) and  the public key is: (n,e)
    def generate_keys(self):
        self.n = self.p  * self.q
        # computing phi
        phi = (self.p-1) * (self.q-1)

        # We first need to validate and choose e such that e is a co-prime to phi and is not dibisble by e. So e is 1 < e < phi. 
        #  In other words gcd(e,phi) = 1
        if gcd(self.e,phi) != 1:
            while gcd(self.e , phi) !=1:
                # We +2 to e to increase to next prime
                self.e +=2

         
        # finding d with mod inverse
        self.d = mod_inverse(self.e, phi)

        self.public_key = (self.n,self.e)
        self.private_key = (self.n,self.d)

        return self.private_key,self.public_key

    # Adding a record to the inventory
    def add_record(self, record):
        self.records.append(record)
        self.records = sort_records(self.records)

    
    # function encrypts given messaage
    # C = m^e mod n
    def encrypt(self,value,e,n):
        return  pow(value,e,n)

    # decrypt a given encrypted message 
    # H(m) = C^d mod n
    def decrypt(self,value,d,n):
        return pow(value,d,n)

    # Signing the message record we get
    # S = H(m)^d mod n
    def sign_record(self, hashed_record):
        self.signed_record = pow(hashed_record, self.d, self.n)
        return self.signed_record

    # Verification checks if the signed hash is valid 
    def verification(self, h1,  signed_message, e, n ):
        h2  = pow(signed_message,e,n)
        if h1 == h2:
            return True
        else:
            return False
    
    # Hashing the given record
    def hash_record(self,item_index):
        # we first get the chosen record as a string
        record = self.records[item_index].get_record()
        # then we need to hash the record (function returns 32 length hash)
        hashed_record = hash(record)
        return hashed_record

  
    # Sending the data to another inventory
    def send_data_to(self, item_index, inventory_recevier):
        '''
        Steps:
        1. Hash record
        2. Sign hash with sender private key
        3. Build package = message|signature
        4. Encrypt FULL package using receiver public key
        5. Save encrypted package
        '''

        # hash record
        hashed = self.hash_record(item_index)

        # sign record
        signature = self.sign_record(hashed)

        # original message
        message = self.records[item_index].get_record()

        # package before encryption
        package = f"{message}|{signature}"

        # convert string package into numbers
        encrypted_package = []

        for char in package:
            encrypted_char = self.encrypt(
                ord(char),
                inventory_recevier.e,
                inventory_recevier.n
            )
            encrypted_package.append(str(encrypted_char))

        filename = f"package{self.name}to{inventory_recevier.name}.txt"

        with open(filename, "w") as f:
            f.write(",".join(encrypted_package))

        print(f"Sent encrypted package: {filename}")


    # Receiving data
    def recevie_data_from(self, package_name, inventory_sender):
        '''
        Steps:
        1. Read encrypted package
        2. Decrypt package with own private key
        3. Split message|signature
        4. Hash message
        5. Verify signature using sender public key
        6. Add record if valid
        '''

        # read encrypted file
        with open(package_name, "r") as f:
            encrypted_data = f.read()

        encrypted_values = encrypted_data.split(",")

        # decrypt full package
        decrypted_package = ""

        for value in encrypted_values:
            decrypted_char = self.decrypt(
                int(value),
                self.private_key[1],
                self.n
            )
            decrypted_package += chr(decrypted_char)

        # split message and signature
        package = decrypted_package.split("|")
        message = package[0]
        signed_message = int(package[1])

        # hash received message
        h1 = hash(message)

        # verify sender signature
        if self.verification(
            h1,
            signed_message,
            inventory_sender.e,
            inventory_sender.n
        ):
            print("Record verified...\nAdding Record")

            parts = message.split(",")

            item_id = int(parts[0])
            item_qty = parts[1]
            item_price = parts[2]
            location = parts[3]

            new_record = Record(item_id, item_qty, item_price, location)

            self.add_record(new_record)

        else:
            print("Record Rejected!!!") 

    # Prints information of the keys of the object
    def info_keys(self):
        print(f"\nThe keys of the invertory are: \n Private Key {self.private_key} \n Public Key: {self.public_key}")

    # prints out the records that the invertory has currently stored
    def info_records(self):
        print(f"\n========== INVENTORY {self.name} RECORDS ==========")
        print(f"{'Item ID':<10} {'QTY':<8} {'Price':<10} {'Location':<15}")
        print("-" * 40)

        for record in self.records:
            print(f"{record.item_id:<10} {record.item_qty:<8} {record.item_price:<10} {record.location:<15}")

        print("=======================================\n")