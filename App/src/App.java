import java.io.*;
import java.util.*;

// =======================
// SERIALIZABLE CLASSES
// =======================
class Reservation implements Serializable {
    private String id;
    private String guest;
    private String roomType;

    public Reservation(String id, String guest, String roomType) {
        this.id = id;
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getId() { return id; }
    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
}

// =======================
// INVENTORY (PERSISTABLE)
// =======================
class RoomInventory implements Serializable {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void updateAvailability(String type, int count) {
        inventory.put(type, count);
    }

    public Map<String, Integer> getAll() {
        return inventory;
    }

    public void display() {
        System.out.println("\n=== INVENTORY ===");
        for (String k : inventory.keySet()) {
            System.out.println(k + " -> " + inventory.get(k));
        }
    }
}

// =======================
// HISTORY (PERSISTABLE)
// =======================
class BookingHistory implements Serializable {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void display() {
        System.out.println("\n=== BOOKING HISTORY ===");
        for (Reservation r : history) {
            System.out.println(r.getId() + " | " + r.getGuest());
        }
    }
}

// =======================
// PERSISTENCE SERVICE (UC10 🔥)
// =======================
class PersistenceService {

    private static final String FILE_NAME = "hotel_state.ser";

    // Save state
    public static void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(inventory);
            oos.writeObject(history);

            System.out.println("\nState SAVED successfully.");

        } catch (IOException e) {
            System.out.println("ERROR saving state: " + e.getMessage());
        }
    }

    // Load state
    public static Object[] load() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory inventory = (RoomInventory) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();

            System.out.println("\nState LOADED successfully.");

            return new Object[]{inventory, history};

        } catch (FileNotFoundException e) {
            System.out.println("\nNo previous state found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nCorrupted data. Resetting system safely.");
        }

        // fallback safe state
        return new Object[]{new RoomInventory(), new BookingHistory()};
    }
}

// =======================
// MAIN (SIMULATE RESTART)
// =======================
public class Main {
    public static void main(String[] args) {

        // 🔥 LOAD STATE (Startup)
        Object[] data = PersistenceService.load();
        RoomInventory inventory = (RoomInventory) data[0];
        BookingHistory history = (BookingHistory) data[1];

        // If fresh start, initialize
        if (inventory.getAll().isEmpty()) {
            inventory.addRoomType("Single Room", 2);
            inventory.addRoomType("Suite Room", 1);
        }

        // Simulate booking
        Reservation r1 = new Reservation("R1", "Wasim", "Single Room");

        if (inventory.getAvailability("Single Room") > 0) {
            inventory.updateAvailability("Single Room",
                    inventory.getAvailability("Single Room") - 1);

            history.add(r1);
            System.out.println("Booking CONFIRMED: " + r1.getGuest());
        }

        // Display current state
        inventory.display();
        history.display();

        // 🔥 SAVE STATE (Shutdown)
        PersistenceService.save(inventory, history);

        System.out.println("\n--- Restart the program to see recovery ---");
    }
}