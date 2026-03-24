import java.util.*;

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
    public int getBeds() { return beds; }
    public double getPrice() { return price; }

    public abstract void displayRoomDetails();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 1000); }
    public void displayRoomDetails() {
        System.out.println(getRoomType() + " | ₹" + getPrice());
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 2000); }
    public void displayRoomDetails() {
        System.out.println(getRoomType() + " | ₹" + getPrice());
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 5000); }
    public void displayRoomDetails() {
        System.out.println(getRoomType() + " | ₹" + getPrice());
    }
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
        return inventory.getOrDefault(type, 0);
    }

    public void updateAvailability(String type, int newCount) {
        inventory.put(type, newCount);
    }
}

// =======================
// RESERVATION
// =======================
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// =======================
// QUEUE (UC3)
// =======================
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// =======================
// BOOKING HISTORY (UC6 🔥)
// =======================
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    // Store confirmed booking
    public void add(Reservation reservation) {
        history.add(reservation);
    }

    // Retrieve all bookings
    public List<Reservation> getAll() {
        return history;
    }
}

// =======================
// BOOKING SERVICE (UC4 + UC6 integration)
// =======================
class BookingService {

    private RoomInventory inventory;
    private BookingHistory history;

    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private int roomCounter = 1;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    private String generateRoomId(String type) {
        return type.replace(" ", "").toUpperCase() + "-" + (roomCounter++);
    }

    public void processBookings(BookingQueue queue) {

        while (!queue.isEmpty()) {

            Reservation req = queue.getNext();
            int available = inventory.getAvailability(req.getRoomType());

            if (available <= 0) {
                System.out.println("FAILED: " + req.getGuestName());
                continue;
            }

            String roomId = generateRoomId(req.getRoomType());

            allocatedRooms.putIfAbsent(req.getRoomType(), new HashSet<>());
            allocatedRooms.get(req.getRoomType()).add(roomId);

            inventory.updateAvailability(req.getRoomType(), available - 1);

            // 🔥 Store in history
            history.add(req);

            System.out.println("CONFIRMED: " + req.getGuestName() +
                    " | Room ID: " + roomId);
        }
    }
}

// =======================
// REPORT SERVICE (UC6 🔥)
// =======================
class ReportService {

    private BookingHistory history;

    public ReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all bookings
    public void showAllBookings() {
        System.out.println("\n=== BOOKING HISTORY ===");

        for (Reservation r : history.getAll()) {
            System.out.println("ID: " + r.getReservationId() +
                    " | Guest: " + r.getGuestName() +
                    " | Room: " + r.getRoomType());
        }
    }

    // Summary Report
    public void generateSummary() {
        System.out.println("\n=== SUMMARY REPORT ===");

        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : history.getAll()) {
            countMap.put(r.getRoomType(),
                    countMap.getOrDefault(r.getRoomType(), 0) + 1);
        }

        for (String type : countMap.keySet()) {
            System.out.println(type + " booked: " + countMap.get(type));
        }
    }
}

// =======================
// MAIN (FULL SYSTEM)
// =======================
public class Main {
    public static void main(String[] args) {

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Suite Room", 1);

        // History
        BookingHistory history = new BookingHistory();

        // Queue
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("R1", "Wasim", "Single Room"));
        queue.addRequest(new Reservation("R2", "Ali", "Single Room"));
        queue.addRequest(new Reservation("R3", "Rahman", "Suite Room"));

        // Booking
        BookingService bookingService = new BookingService(inventory, history);
        bookingService.processBookings(queue);

        // Reporting
        ReportService report = new ReportService(history);
        report.showAllBookings();
        report.generateSummary();
    }
}