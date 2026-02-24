import java.io.*;
import java.util.ArrayList;

public class SnowResort {
    private ArrayList<Accommodation> accommodations;
    private ArrayList<Customer> customers;
    private ArrayList<TravelPackage> packages;
    private ArrayList<Lesson> lessons;
    private ArrayList<LiftPass> liftPasses;

    public SnowResort() {
        accommodations = getInitialAccommodations();
        lessons = getInitialLessons();
        liftPasses = getInitialLiftPasses();

        customers = new ArrayList<>();
        packages = new ArrayList<>();

        // Load initial runtime data
        reloadFromFiles();
    }

    public ArrayList<Accommodation> getAccommodations() {
        return accommodations;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<TravelPackage> getPackages() {
        return packages;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public ArrayList<LiftPass> getLiftPasses() {
        return liftPasses;
    }

    public ArrayList<Accommodation> getAvailableAccommodations() {
        ArrayList<Accommodation> available = new ArrayList<>();
        for (Accommodation a : accommodations) {
            if (a.isAvailable()) available.add(a);
        }
        return available;
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void addPackage(TravelPackage travelPackage) {
        packages.add(travelPackage);
        if (travelPackage.getAccommodation() != null) {
            travelPackage.getAccommodation().setAvailable(false);
        }
    }

    public int getNextCustomerId() {
        int max = 0;
        for (Customer c : customers) {
            if (c != null && c.getId() > max) max = c.getId();
        }
        return max + 1;
    }

    // ------------------ Save / Load ------------------
    public void saveCustomersToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("customers.dat"))) {
            out.writeObject(customers);
        } catch (IOException e) {
            System.out.println("Error saving customer data: " + e.getMessage());
        }
    }

    public void savePackagesToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("packages.dat"))) {
            out.writeObject(packages);
        } catch (IOException e) {
            System.out.println("Error saving package data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Customer> loadCustomersFromFileInternal() {
        ArrayList<Customer> loaded = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("customers.dat"))) {
            loaded = (ArrayList<Customer>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // first run or file missing is OK
        }
        return loaded;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<TravelPackage> loadPackagesFromFileInternal() {
        ArrayList<TravelPackage> loaded = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("packages.dat"))) {
            loaded = (ArrayList<TravelPackage>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // first run or file missing is OK
        }
        return loaded;
    }

    // Teacher fixes: load properly + import customers + set accommodations unavailable
    public void reloadFromFiles() {
        // reset accommodations to available, then re-apply bookings
        for (Accommodation a : accommodations) {
            a.setAvailable(true);
        }

        ArrayList<Customer> loadedCustomers = loadCustomersFromFileInternal();
        ArrayList<TravelPackage> loadedPackages = loadPackagesFromFileInternal();

        customers.clear();
        packages.clear();

        if (loadedCustomers == null || loadedCustomers.isEmpty()) {
            customers.addAll(getInitialCustomers());
        } else {
            customers.addAll(loadedCustomers);
        }

        if (loadedPackages != null) {
            packages.addAll(loadedPackages);
        }

        // Teacher fix: import customers from packages if missing
        importCustomersFromPackagesIfMissing();

        // Teacher fix: accommodation unavailable when importing packages
        markImportedAccommodationAsUnavailable();
    }

    private void importCustomersFromPackagesIfMissing() {
        for (TravelPackage pkg : packages) {
            if (pkg == null || pkg.getCustomer() == null) continue;
            Customer pc = pkg.getCustomer();
            if (!customerExists(pc)) {
                customers.add(pc);
            }
        }
    }

    private boolean customerExists(Customer candidate) {
        if (candidate == null) return false;
        for (Customer c : customers) {
            if (c == null) continue;
            if (c.getId() == candidate.getId()) return true;
            if (c.getCustomerName().equalsIgnoreCase(candidate.getCustomerName())
                    && c.getSkiLevel().equalsIgnoreCase(candidate.getSkiLevel())) {
                return true;
            }
        }
        return false;
    }

    private void markImportedAccommodationAsUnavailable() {
        for (TravelPackage pkg : packages) {
            if (pkg == null || pkg.getAccommodation() == null) continue;

            Accommodation pkgAcc = pkg.getAccommodation();
            Accommodation master = findAccommodationByName(pkgAcc.getName());
            if (master != null) {
                master.setAvailable(false);
                pkgAcc.setAvailable(false);
            } else {
                pkgAcc.setAvailable(false);
            }
        }
    }

    private Accommodation findAccommodationByName(String name) {
        if (name == null) return null;
        for (Accommodation a : accommodations) {
            if (a != null && name.equalsIgnoreCase(a.getName())) return a;
        }
        return null;
    }

    // ------------------ Initial Data ------------------
    public ArrayList<Accommodation> getInitialAccommodations() {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        accommodations.add(new Accommodation("Alpine Ridge Lodge", 100.00, true));
        accommodations.add(new Accommodation("Buller Basin Retreat", 80.00, true));
        accommodations.add(new Accommodation("Stirling Summit Chalet", 150.00, true));
        accommodations.add(new Accommodation("Frosty Spur Lodge", 180.00, true));
        accommodations.add(new Accommodation("Snowgums Hideaway", 120.00, true));
        accommodations.add(new Accommodation("Whitehorse Peaks Chalet", 110.00, true));
        accommodations.add(new Accommodation("Timberline Creek Lodge", 200.00, true));
        accommodations.add(new Accommodation("Bluff View Ski Lodge", 220.00, true));
        accommodations.add(new Accommodation("Koala Ridge Retreat", 170.00, true));
        accommodations.add(new Accommodation("Buller Snowfields Cabins", 130.00, true));
        accommodations.add(new Accommodation("Crystal Valley Lodge", 800.00, true));
        accommodations.add(new Accommodation("Bogong Heights Chalets", 160.00, true));
        return accommodations;
    }

    public ArrayList<Customer> getInitialCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1, "DJ", "Beginner"));
        customers.add(new Customer(2, "Justin", "Intermediate"));
        customers.add(new Customer(3, "Erica", "Expert"));
        return customers;
    }

    public ArrayList<Lesson> getInitialLessons() {
        ArrayList<Lesson> lessons = new ArrayList<>();
        lessons.add(new Lesson("Beginner", 25));
        lessons.add(new Lesson("Intermediate", 20));
        lessons.add(new Lesson("Expert", 15));
        return lessons;
    }

    public ArrayList<LiftPass> getInitialLiftPasses() {
        ArrayList<LiftPass> liftPasses = new ArrayList<>();
        liftPasses.add(new LiftPass("1 Day Pass", 120));
        liftPasses.add(new LiftPass("3 Day Pass", 300));
        return liftPasses;
    }
}
