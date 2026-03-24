import java.util.*;

// =======================
// CUSTOM EXCEPTION (UC7 🔥)
// =======================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// =======================
// ROOM (UC0)
// =======================
abstract class Room {
    private String roomType;
    private int beds;
    private double price;

    public Room(String roomType, int beds, double price) {
        this.roomType = roomType;
        this.beds = beds;
        this.price = price;
    }

    public String getRoomType() { return roomType; }
    public double getPrice() { return price; }
}

// =======================
// INVENTORY (UC1)
// =======================
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, -1); // -1 = invalid type
    }

    public void updateAvailability(String type, int newCount) throws InvalidBookingException {
        if (newCount < 0) {
            throw new InvalidBookingException("Inventory cannot be negative for " + type);
        }
        inventory.put(type, newCount);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }
}

// =======================
// RESERVATION
// =======================
class Reservation {
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
// QUEUE (UC3)
// =======================
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) {
        queue.offer(r);
    }

    public Reservation next() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// =======================
// VALIDATOR (UC7 🔥)
// =======================
class BookingValidator {

    public static void validate(Reservation r, RoomInventory inventory)
            throws InvalidBookingException {

        // Validate nulls
        if (r.getGuest() == null || r.getGuest().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        // Validate room type
        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }

        // Validate availability
        int available = inventory.getAvailability(r.getRoomType());
        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for " + r.getRoomType());
        }
    }
}

// =======================
// BOOKING HISTORY (UC6)
// =======================
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }
}

// =======================
// BOOKING SERVICE (UC4 + UC7)
// =======================
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;

    private Map<String, Set<String>> allocated = new HashMap<>();
    private int counter = 1;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    private String generateRoomId(String type) {
        return type.replace(" ", "").toUpperCase() + "-" + (counter++);
    }

    public void process(BookingQueue queue) {

        System.out.println("\n=== PROCESSING BOOKINGS ===");

        while (!queue.isEmpty()) {

            Reservation r = queue.next();

            try {
                // 🔥 VALIDATION (Fail Fast)
                BookingValidator.validate(r, inventory);

                int available = inventory.getAvailability(r.getRoomType());

                String roomId = generateRoomId(r.getRoomType());

                allocated.putIfAbsent(r.getRoomType(), new HashSet<>());
                allocated.get(r.getRoomType()).add(roomId);

                // 🔥 SAFE UPDATE
                inventory.updateAvailability(r.getRoomType(), available - 1);

                history.add(r);

                System.out.println("CONFIRMED: " + r.getGuest() +
                        " | Room ID: " + roomId);

            } catch (InvalidBookingException e) {
                // 🔥 GRACEFUL FAILURE
                System.out.println("ERROR for " + r.getGuest() + ": " + e.getMessage());
            }
        }
    }
}

// =======================
// REPORT (UC6)
// =======================
class ReportService {
    private BookingHistory history;

    public ReportService(BookingHistory history) {
        this.history = history;
    }

    public void showAll() {
        System.out.println("\n=== BOOKING HISTORY ===");
        for (Reservation r : history.getAll()) {
            System.out.println(r.getId() + " | " + r.getGuest() +
                    " | " + r.getRoomType());
        }
    }
}

// =======================
// MAIN (FULL SYSTEM)
// =======================
public class Main {
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 1);
        inventory.addRoomType("Suite Room", 1);

        BookingHistory history = new BookingHistory();
        BookingQueue queue = new BookingQueue();

        // Valid + Invalid Cases
        queue.add(new Reservation("R1", "Wasim", "Single Room"));
        queue.add(new Reservation("R2", "", "Single Room")); // invalid name
        queue.add(new Reservation("R3", "Ali", "Invalid Room")); // invalid type
        queue.add(new Reservation("R4", "Rahman", "Single Room")); // no availability

        BookingService service = new BookingService(inventory, history);
        service.process(queue);

        ReportService report = new ReportService(history);
        report.showAll();
    }
}