import java.util.*;

// =======================
// ABSTRACT ROOM (UC0)
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

// =======================
// ROOM TYPES
// =======================
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

    public Map<String, Integer> getAllInventory() {
        return inventory;
    }
}

// =======================
// SEARCH SERVICE (UC2)
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

    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added: " + reservation.getGuestName());
    }

    public void viewRequests() {
        System.out.println("\n=== BOOKING REQUEST QUEUE ===");

        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }

        for (Reservation r : queue) {
            System.out.println("Guest: " + r.getGuestName() +
                    " | Room: " + r.getRoomType());
        }
    }

    public Reservation peekNext() {
        return queue.peek();
    }
}

// =======================
// MAIN CLASS (RUN EVERYTHING)
// =======================
public class Main {
    public static void main(String[] args) {

        // -----------------------
        // UC1: Inventory Setup
        // -----------------------
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 0);
        inventory.addRoomType("Suite Room", 2);

        // -----------------------
        // UC0: Room Objects
        // -----------------------
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // -----------------------
        // UC2: Search (Read Only)
        // -----------------------
        SearchService searchService = new SearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        // -----------------------
        // UC3: Booking Requests
        // -----------------------
        BookingQueue queue = new BookingQueue();

        queue.addRequest(new Reservation("Wasim", "Single Room"));
        queue.addRequest(new Reservation("Ali", "Double Room"));
        queue.addRequest(new Reservation("Rahman", "Suite Room"));

        queue.viewRequests();

        // Peek (No Processing)
        Reservation next = queue.peekNext();
        if (next != null) {
            System.out.println("\nNext to process: " + next.getGuestName());
        }

        System.out.println("\nNo allocation done yet (Inventory unchanged).");
    }
}