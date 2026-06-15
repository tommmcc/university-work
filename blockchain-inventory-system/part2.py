# ------ Part 2 ----
'''
Task3:
When an authorised external user submits a query to retrieve the quantity of a specific inventory item, the
distributed inventory system must ensure that the returned result is jointly verified, authenticated, and
securely delivered.
'''
'''
Task3 contribution - secure record retrieval

This file keeps Part 2 separate from Part 1 so it does not interfere with the
record insertion, signing, verification, or consensus code.

This implementation covers:
- Procurement Officer query submission
- inventory lookup across the inventory nodes
- PKG key setup using the provided values
- Harn-style identity-based multi-signature approval
- aggregation and verification of partial signatures
- RSA protection of the approved response
- user-side recovery of the protected response
'''

from Inventory import Inventory, Record


# ------------------ Helper Functions ------------------

def gcd(a, b):
    while b != 0:
        a, b = b, a % b
    return a


def mod_inverse(e, phi):
    a = phi
    b = e
    x_0 = 0
    x_1 = 1

    while b != 0:
        q = a // b

        temp_a = a
        a = b
        b = temp_a % b

        temp_x0 = x_0
        x_0 = x_1
        x_1 = temp_x0 - q * x_1

    if a != 1:
        return None

    return x_0 % phi


# simple deterministic hash function, kept similar to Inventory.py style
def simple_hash(message, fixed_length=32):
    hash_value = 0
    prime_number = 31

    for char in message:
        hash_value = (hash_value * prime_number + ord(char)) % (2 ** fixed_length)

    return hash_value


def multiply_mod(values, n):
    result = 1

    for value in values:
        result = (result * value) % n

    return result


# encrypts a string one character at a time so the message never exceeds n
def rsa_encrypt_string(message, e, n):
    encrypted_values = []

    for char in message:
        encrypted_char = pow(ord(char), e, n)
        encrypted_values.append(str(encrypted_char))

    return ",".join(encrypted_values)


def rsa_decrypt_string(encrypted_message, d, n):
    decrypted_message = ""

    encrypted_values = encrypted_message.split(",")

    for value in encrypted_values:
        decrypted_char = pow(int(value), d, n)
        decrypted_message += chr(decrypted_char)

    return decrypted_message


# ------------------ PKG Object ------------------

class PKG:
    def __init__(self, p, q, e):
        self.p = p
        self.q = q
        self.e = e

    def generate_keys(self):
        self.n = self.p * self.q
        phi = (self.p - 1) * (self.q - 1)

        while gcd(self.e, phi) != 1:
            self.e += 2

        self.d = mod_inverse(self.e, phi)

        self.public_key = (self.n, self.e)
        self.private_key = (self.n, self.d)

        return self.private_key, self.public_key

    # PKG generates the private signing key for each inventory identity
    # secret_key = ID^d mod n
    def generate_secret_key(self, identity):
        return pow(identity, self.d, self.n)


# ------------------ Procurement Officer ------------------

class ProcurementOfficer:
    def __init__(self, p, q, e):
        self.p = p
        self.q = q
        self.e = e

    def generate_keys(self):
        self.n = self.p * self.q
        phi = (self.p - 1) * (self.q - 1)

        while gcd(self.e, phi) != 1:
            self.e += 2

        self.d = mod_inverse(self.e, phi)

        self.public_key = (self.n, self.e)
        self.private_key = (self.n, self.d)

        return self.private_key, self.public_key

    def submit_query(self, item_id):
        print(f"\n[Procurement Officer] Requesting quantity for item ID: {item_id}")
        return item_id

    def decrypt_response(self, encrypted_response):
        decrypted_response = rsa_decrypt_string(
            encrypted_response,
            self.private_key[1],
            self.private_key[0]
        )

        print(f"[Procurement Officer] Decrypted response: {decrypted_response}")
        return decrypted_response


# ------------------ Harn-Style Multi-Signature ------------------

class HarnMultiSignature:
    def __init__(self, pkg, node_details):
        self.pkg = pkg
        self.node_details = node_details

        # PKG assigns each node a private signing key based on identity
        for node in self.node_details:
            node["secret_key"] = self.pkg.generate_secret_key(node["identity"])

            # public random component used for verification
            node["public_random"] = pow(
                node["random_value"],
                self.pkg.e,
                self.pkg.n
            )

    # partial signature = secret_key * random^hash(message) mod n
    def generate_partial_signature(self, node, message):
        message_hash = simple_hash(message)

        partial_signature = (
            node["secret_key"] *
            pow(node["random_value"], message_hash, self.pkg.n)
        ) % self.pkg.n

        return partial_signature

    # verifies one inventory node's partial signature
    def verify_partial_signature(self, node, partial_signature, message):
        message_hash = simple_hash(message)

        left_side = pow(partial_signature, self.pkg.e, self.pkg.n)

        right_side = (
            node["identity"] *
            pow(node["public_random"], message_hash, self.pkg.n)
        ) % self.pkg.n

        return left_side == right_side

    # combines all valid partial signatures into one multi-signature
    def aggregate_signatures(self, partial_signatures):
        return multiply_mod(partial_signatures, self.pkg.n)

    # verifies the final aggregated multi-signature
    def verify_aggregate_signature(self, aggregate_signature, approved_nodes, message):
        message_hash = simple_hash(message)

        identities = []
        public_randoms = []

        for node in approved_nodes:
            identities.append(node["identity"])
            public_randoms.append(node["public_random"])

        combined_identity = multiply_mod(identities, self.pkg.n)
        combined_public_random = multiply_mod(public_randoms, self.pkg.n)

        left_side = pow(aggregate_signature, self.pkg.e, self.pkg.n)

        right_side = (
            combined_identity *
            pow(combined_public_random, message_hash, self.pkg.n)
        ) % self.pkg.n

        return left_side == right_side


# ------------------ Query System ------------------

class QuerySystem:
    def __init__(self, inventories, officer, multi_signature):
        self.inventories = inventories
        self.officer = officer
        self.multi_signature = multi_signature

    def get_node_quantity(self, inventory, item_id):
        for record in inventory.records:
            if record.item_id == item_id:
                return record.item_qty

        return None

    def get_agreed_quantity(self, item_id):
        quantities = []

        for inventory in self.inventories:
            quantity = self.get_node_quantity(inventory, item_id)

            print(f"[Inventory {inventory.name}] Local query result: {quantity}")

            if quantity is not None:
                quantities.append(quantity)

        if len(quantities) == 0:
            return None

        # checks if all returned quantities match
        first_quantity = quantities[0]

        for quantity in quantities:
            if quantity != first_quantity:
                return None

        return first_quantity

    def multi_signature_approval(self, result_message):
        print("\n[System] Starting multi-signature approval...")

        partial_signatures = []
        approved_nodes = []

        for node in self.multi_signature.node_details:
            partial_signature = self.multi_signature.generate_partial_signature(
                node,
                result_message
            )

            is_valid = self.multi_signature.verify_partial_signature(
                node,
                partial_signature,
                result_message
            )

            print(f"[Inventory {node['name']}] Partial signature: {partial_signature}")
            print(f"[Inventory {node['name']}] Partial signature valid: {is_valid}")

            if is_valid:
                partial_signatures.append(partial_signature)
                approved_nodes.append(node)

        # require all four inventory nodes to approve
        if len(approved_nodes) != 4:
            print("[System] Multi-signature approval failed.")
            return False, None

        aggregate_signature = self.multi_signature.aggregate_signatures(partial_signatures)

        aggregate_valid = self.multi_signature.verify_aggregate_signature(
            aggregate_signature,
            approved_nodes,
            result_message
        )

        print(f"\n[System] Aggregated multi-signature: {aggregate_signature}")
        print(f"[System] Aggregated multi-signature valid: {aggregate_valid}")

        if aggregate_valid:
            return True, aggregate_signature
        else:
            return False, None

    def encrypt_response(self, response_message):
        encrypted_response = rsa_encrypt_string(
            response_message,
            self.officer.public_key[1],
            self.officer.public_key[0]
        )

        print("\n[System] Encrypted response generated successfully.")
        print(f"[System] Encrypted response preview: {encrypted_response[:120]}...")
        print(f"[System] Encrypted response length: {len(encrypted_response)} characters")
        print(f"[System] Encryption changed the message: {encrypted_response != response_message}")
        return encrypted_response

    def process_query(self, item_id):
        print("\n========== SECURE QUERY WORKFLOW ==========")

        quantity = self.get_agreed_quantity(item_id)

        if quantity is None:
            print("[System] Query failed. Item not found or inventory nodes disagree.")
            print("========== END QUERY ==========\n")
            return

        result_message = f"Item {item_id} quantity is {quantity}"

        print(f"\n[System] Proposed query result: {result_message}")

        approved, aggregate_signature = self.multi_signature_approval(result_message)

        if not approved:
            print("[System] Query result was not approved by all inventory nodes.")
            print("========== END QUERY ==========\n")
            return

        response_message = (
            f"{result_message} | "
            f"Aggregate Signature: {aggregate_signature}"
        )

        encrypted_response = self.encrypt_response(response_message)

        recovered_response = self.officer.decrypt_response(encrypted_response)

        print(f"[System] Recovery successful: {recovered_response == response_message}")
        print("========== END QUERY ==========\n")


# ------------------ Demo Setup ------------------

# Each inventory is initialised here so part2.py can run independently.
# This simulates the state after Part 1 has already accepted and stored records.

inver_A = Inventory(
    1210613765735147311106936311866593978079938707,
    1247842850282035753615951347964437248190231863,
    815459040813953176289801,
    "A"
)

inver_B = Inventory(
    787435686772982288169641922308628444877260947,
    1325305233886096053310340418467385397239375379,
    692450682143089563609787,
    "B"
)

inver_C = Inventory(
    1014247300991039444864201518275018240361205111,
    904030450302158058469475048755214591704639633,
    1158749422015035388438057,
    "C"
)

inver_D = Inventory(
    1287737200891425621338551020762858710281638317,
    1330909125725073469794953234151525201084537607,
    33981230465225879849295979,
    "D"
)

inver_A.generate_keys()
inver_B.generate_keys()
inver_C.generate_keys()
inver_D.generate_keys()

# same records are added to every node to simulate a consistent distributed inventory state
accepted_records = [
    Record(1, "32", "12", "D"),
    Record(2, "20", "14", "C"),
    Record(3, "22", "16", "B"),
    Record(4, "12", "18", "A")
]

for record in accepted_records:
    inver_A.add_record(Record(record.item_id, record.item_qty, record.item_price, record.location))
    inver_B.add_record(Record(record.item_id, record.item_qty, record.item_price, record.location))
    inver_C.add_record(Record(record.item_id, record.item_qty, record.item_price, record.location))
    inver_D.add_record(Record(record.item_id, record.item_qty, record.item_price, record.location))


# ------------------ PKG Setup ------------------

pkg = PKG(
    1004162036461488639338597000466705179253226703,
    950133741151267522116252385927940618264103623,
    973028207197278907211
)

pkg.generate_keys()


# ------------------ Procurement Officer Setup ------------------

officer = ProcurementOfficer(
    1080954735722463992988394149602856332100628417,
    1158106283320086444890911863299879973542293243,
    106506253943651610547613
)

officer.generate_keys()


# ------------------ Multi-Signature Setup ------------------

node_details = [
    {
        "name": "A",
        "identity": 126,
        "random_value": 621
    },
    {
        "name": "B",
        "identity": 127,
        "random_value": 721
    },
    {
        "name": "C",
        "identity": 128,
        "random_value": 821
    },
    {
        "name": "D",
        "identity": 129,
        "random_value": 921
    }
]

multi_signature = HarnMultiSignature(pkg, node_details)


# ------------------ Run Demo ------------------

query_system = QuerySystem(
    [inver_A, inver_B, inver_C, inver_D],
    officer,
    multi_signature
)

print("=== Secure Record Retrieval Demo ===")
print("This demo queries the distributed inventory system and returns the result securely.")

try:
    user_input = input("Enter item ID to query, or press Enter to query item 4: ")

    if user_input.strip() == "":
        item_requested = officer.submit_query(4)
    else:
        item_requested = officer.submit_query(int(user_input))

    query_system.process_query(item_requested)

except:
    print("Invalid input. Query cancelled.")