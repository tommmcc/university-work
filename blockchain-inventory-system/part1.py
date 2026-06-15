
# ------ Part 1 ------ 
from Inventory import *

# Initialising invertorys
inver_A =  Inventory(1210613765735147311106936311866593978079938707,1247842850282035753615951347964437248190231863, 815459040813953176289801, "A")
inver_B = Inventory(787435686772982288169641922308628444877260947, 1325305233886096053310340418467385397239375379, 692450682143089563609787, "B")
inver_C = Inventory(1014247300991039444864201518275018240361205111, 904030450302158058469475048755214591704639633,158749422015035388438057, "C" )
inver_D = Inventory(1287737200891425621338551020762858710281638317,1330909125725073469794953234151525201084537607, 33981230465225879849295979, "D")

# Initialising keys for each inventory
private_key_A, public_key_A = inver_A.generate_keys()
private_key_B, public_key_B = inver_B.generate_keys()
private_key_C, public_key_C = inver_C.generate_keys()
private_key_D, public_key_D = inver_D.generate_keys()


# Function is used to unanimous consensus where each record receives the signature and the hashed message and then verifies if valid. If all inventorys are True then add the record else Reject the Record
def unanimous_consensus(sender, record_index, all_inventorys):
    message  = sender.records[record_index].get_record()
    hashed = hash(message)
    signature = sender.sign_record(hashed) 

    # we can create an array in which allows us toi implement and check if  all the inventorys accpeted  the record
    votes = []

    for inventory in all_inventorys:
        valid = inventory.verification(hashed,signature,sender.e,sender.n)

        if valid:
            print(f"\nInventory {inventory.name} votes ACCEPT")
            votes.append(True)
        else:
            print(f"\nInventory {inventory.name} votes REJECTED")
            votes.append(False)
    
    # Now after adding all the votes we will need to check if all votes were true
    # if the votes are not all true then we don't add it and reject 
    if all(votes):
        print("\nAll Inventorys agreed and unanioums consensus has been reached")

        components = message.split(",")

        for inventory in all_inventorys:

            # Check if record already exists
            already_exists = False

            for record in inventory.records:
                if record.get_record() == message:
                    already_exists = True
                    break

            # Only add if not already there
            if not already_exists:
                new_record = Record(
                    int(components[0]),
                    components[1],
                    components[2],
                    components[3]
                )

                inventory.add_record(new_record)

        print("Record added in All inventories sucessfully")

    else:
        print("\nConsesnsus failed. Record Rejected")
            

# ------= Creating UI for use to interact with ---- 
# Added mappings to make it easier to refactor code
inventories = {
    "A": inver_A,
    "B": inver_B,
    "C": inver_C,
    "D": inver_D
}

# differnet tasks to help
def main_menu():
    print("\n=== MAIN MENU ===")
    print("1. Task 1")
    print("2. Task 2")
    print("Type 'exit' to quit")


def task1_menu():
    print("\n=== TASK 1 MENU ===")
    print("1. View Inventories")
    print("2. Add Record")
    print("3. Send & Verify Record")
    print("Type 'back' to return")

def task2_menu():
    print("\n=== TASK 2 MENU ===")
    print("1. View Inventories")
    print("2. Run Unanimous Consensus")
    print("Type 'back' to return")


def task2_consensus_ui():

    print("\n--- UNANIMOUS CONSENSUS ---")

    sender_obj,sender_name = select_inventory("Select sender")
    #check if there exitsts an sender 
    if sender_obj is None:
        return

    # checks if the record index exists 
    record_index = select_record(sender_obj)
    if record_index is None:
        return

    all_nodes = [inver_A, inver_B, inver_C, inver_D]

    unanimous_consensus(sender_obj, record_index, all_nodes)

    print("\nUpdated Inventories:")
    show_inventories()

def show_inventories():
    print("\n--- ALL INVENTORIES ---")
    inver_A.info_records()
    inver_B.info_records()
    inver_C.info_records()
    inver_D.info_records()  

def select_inventory(prompt):
    print("\nSelect Inventory:")
    for key in inventories:
        print(f"{key}", end="  ")
    print()

    choice = input(f"{prompt} (A-D): ").upper()

    if choice in inventories:
        return inventories[choice], choice
    else:
        print("Invalid selection.")
        return None, None


def select_record(inventory):
    if len(inventory.records) == 0:
        print("No records available.")
        return None

    print("\nAvailable Records:")
    inventory.info_records()

    try:
        index = int(input("Select record index: "))
        if 0 <= index < len(inventory.records):
            return index
        else:
            print("Invalid index.")
            return None
    except:
        print("Invalid input.")
        return None


def add_record_ui():
    inv_obj, inv_name = select_inventory("Add record to")
    if inv_obj is None:
        return

    try:
        item_id = int(input("Enter item ID: "))
        item_qty = input("Enter quantity: ")
        item_price = input("Enter price: ")

        record = Record(item_id, item_qty, item_price, inv_name)
        inv_obj.add_record(record)

        print(f"\nRecord added to Inventory {inv_name}")
    except:
        print("Invalid input. Record not added.")


def send_verify_ui():
    print("\n--- SEND & VERIFY ---")

    # checking 
    sender_obj, sender_name = select_inventory("Select sender")
    if sender_obj is None:
        return

    receiver_obj, receiver_name = select_inventory("Select receiver")
    if receiver_obj is None or receiver_name == sender_name:
        print("Invalid receiver.")
        return

    record_index = select_record(sender_obj)
    if record_index is None:
        return

    # Main logic for sending 
    print(f"\nSending record [{record_index}] from {sender_name} -> {receiver_name}")

    sender_obj.send_data_to(record_index, receiver_obj)

    filename = f"package{sender_name}to{receiver_name}.txt"
    print(f"Verifying at Inventory {receiver_name}...")

    receiver_obj.recevie_data_from(filename, sender_obj)

    print("\nUpdated Receiver Inventory:")
    receiver_obj.info_records()


#------- Main loop ---- 
print("=== Distributed Inventory System ===")

running = True

while running:
    main_menu()
    choice = input("\nSelect option: ").lower()

    if choice == "exit":
        running = False

    elif choice == "1":
        print("\n--- TASK 1 ---")

        while True:
            task1_menu()
            t1_choice = input("\nChoose option: ").lower()

            if t1_choice == "1":
                show_inventories()

            elif t1_choice == "2":
                add_record_ui()

            elif t1_choice == "3":
                send_verify_ui()

            elif t1_choice == "back":
                break

            else:
                print("Invalid option.")

    elif choice == "2":
        print("\n--- TASK 2 ---")
        while True:
            task2_menu()
            t2_choice = input("\nChoose option: ").lower()

            if t2_choice == "1":
                show_inventories()

            elif t2_choice == "2":
                task2_consensus_ui()

            elif t2_choice == "back":
                break

            else:
                print("Invalid option.")

    else:
        print("Invalid option.")

print("\nGoodbye and haven an amazing day :)")