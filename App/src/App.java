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

    public String getRoomType() {
        return roomType;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    public abstract void displayRoomDetails();
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 1000);
    }

    public void displayRoomDetails() {
        System.out.println("Type: " + getRoomType() +
                ", Beds: " + getBeds() +
                ", Price: ₹" + getPrice());
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 2000);
    }

    public void displayRoomDetails() {
        System.out.println("Type: " + getRoomType() +
                ", Beds: " + getBeds() +
                ", Price: ₹" + getPrice());
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 5000);
    }

    public void displayRoomDetails() {
        System.out.println("Type: " + getRoomType() +
                ", Beds: " + getBeds() +
                ", Price: ₹" + getPrice());
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

    public void displayInventory() {
        System.out.println("\n=== CURRENT INVENTORY ===");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " -> " + e.getValue());
        }
    }
}

// =======================
// SEARCH (UC2)
// =======================
class SearchService {
    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(Room[] rooms) {
        System.out.println("\n=== AVAILABLE ROOMS ===");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getRoomType());

            if (available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available);
                System.out.println("----------------------");
            }
        }
    }
}

// =======================
// RESERVATION (UC3)
// =======================
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// =======================
// BOOKING QUEUE (UC3)
// =======================
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
        System.out.println("Request added: " + r.getGuestName());
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO removal
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// =======================
// BOOKING SERVICE (UC4 🔥)
// =======================
class BookingService {

    private RoomInventory inventory;

    // Track allocated room IDs per type
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    private int roomCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    // Generate Unique Room ID
    private String generateRoomId(String roomType) {
        return roomType.replace(" ", "").toUpperCase() + "-" + (roomCounter++);
    }

    // Process Queue
    public void processBookings(BookingQueue queue) {

        System.out.println("\n=== PROCESSING BOOKINGS ===");

        while (!queue.isEmpty()) {

            Reservation req = queue.getNextRequest();

            String type = req.getRoomType();
            int available = inventory.getAvailability(type);

            // Check availability
            if (available <= 0) {
                System.out.println("Booking FAILED for " + req.getGuestName() +
                        " (No rooms available)");
                continue;
            }

            // Generate unique room ID
            String roomId = generateRoomId(type);

            // Ensure Set exists
            allocatedRooms.putIfAbsent(type, new HashSet<>());

            // Add to set (prevents duplicate automatically)
            allocatedRooms.get(type).add(roomId);

            // Update inventory (atomic step)
            inventory.updateAvailability(type, available - 1);

            // Confirm booking
            System.out.println("Booking CONFIRMED:");
            System.out.println("Guest: " + req.getGuestName());
            System.out.println("Room Type: " + type);
            System.out.println("Room ID: " + roomId);
            System.out.println("----------------------");
        }
    }

    // View Allocated Rooms
    public void displayAllocations() {
        System.out.println("\n=== ALLOCATED ROOMS ===");

        for (String type : allocatedRooms.keySet()) {
            System.out.println(type + " -> " + allocatedRooms.get(type));
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
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // Room Objects
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // Search
        SearchService search = new SearchService(inventory);
        search.searchAvailableRooms(rooms);

        // Booking Queue
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Wasim", "Single Room"));
        queue.addRequest(new Reservation("Ali", "Single Room"));
        queue.addRequest(new Reservation("Rahman", "Single Room")); // Should fail
        queue.addRequest(new Reservation("Zaid", "Suite Room"));

        // Booking Service
        BookingService bookingService = new BookingService(inventory);
        bookingService.processBookings(queue);

        // Results
        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}