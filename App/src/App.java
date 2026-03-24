import java.util.*;

// =======================
// CUSTOM EXCEPTION (UC7)
// =======================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String msg) {
        super(msg);
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
        return inventory.getOrDefault(type, -1);
    }

    public void updateAvailability(String type, int count) throws InvalidBookingException {
        if (count < 0) throw new InvalidBookingException("Negative inventory not allowed");
        inventory.put(type, count);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public void display() {
        System.out.println("\n=== INVENTORY ===");
        for (String t : inventory.keySet()) {
            System.out.println(t + " -> " + inventory.get(t));
        }
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
    private Queue<Reservation> q = new LinkedList<>();

    public void add(Reservation r) { q.offer(r); }
    public Reservation next() { return q.poll(); }
    public boolean isEmpty() { return q.isEmpty(); }
}

// =======================
// HISTORY (UC6)
// =======================
class BookingHistory {
    private List<Reservation> confirmed = new ArrayList<>();
    private List<Reservation> cancelled = new ArrayList<>();

    public void addConfirmed(Reservation r) { confirmed.add(r); }
    public void addCancelled(Reservation r) { cancelled.add(r); }

    public List<Reservation> getConfirmed() { return confirmed; }

    public boolean exists(String id) {
        for (Reservation r : confirmed) {
            if (r.getId().equals(id)) return true;
        }
        return false;
    }

    public Reservation removeConfirmed(String id) {
        Iterator<Reservation> it = confirmed.iterator();
        while (it.hasNext()) {
            Reservation r = it.next();
            if (r.getId().equals(id)) {
                it.remove();
                return r;
            }
        }
        return null;
    }

    public void show() {
        System.out.println("\n=== CONFIRMED BOOKINGS ===");
        for (Reservation r : confirmed) {
            System.out.println(r.getId() + " | " + r.getGuest());
        }

        System.out.println("\n=== CANCELLED BOOKINGS ===");
        for (Reservation r : cancelled) {
            System.out.println(r.getId() + " | " + r.getGuest());
        }
    }
}

// =======================
// BOOKING SERVICE (UC4)
// =======================
class BookingService {
    private RoomInventory inventory;
    private BookingHistory history;

    // Track allocations
    private Map<String, Stack<String>> roomAllocations = new HashMap<>();

    private int counter = 1;

    public BookingService(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    private String generateRoomId(String type) {
        return type.replace(" ", "").toUpperCase() + "-" + (counter++);
    }

    public void process(BookingQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.next();

            try {
                if (!inventory.isValidRoomType(r.getRoomType()))
                    throw new InvalidBookingException("Invalid room type");

                int available = inventory.getAvailability(r.getRoomType());
                if (available <= 0)
                    throw new InvalidBookingException("No rooms available");

                String roomId = generateRoomId(r.getRoomType());

                roomAllocations.putIfAbsent(r.getRoomType(), new Stack<>());
                roomAllocations.get(r.getRoomType()).push(roomId);

                inventory.updateAvailability(r.getRoomType(), available - 1);

                history.addConfirmed(r);

                System.out.println("CONFIRMED: " + r.getGuest() + " | " + roomId);

            } catch (InvalidBookingException e) {
                System.out.println("ERROR: " + r.getGuest() + " -> " + e.getMessage());
            }
        }
    }

    public Map<String, Stack<String>> getAllocations() {
        return roomAllocations;
    }
}

// =======================
// CANCELLATION SERVICE (UC8 🔥)
// =======================
class CancellationService {

    private RoomInventory inventory;
    private BookingHistory history;
    private Map<String, Stack<String>> allocations;

    public CancellationService(RoomInventory inventory,
                               BookingHistory history,
                               Map<String, Stack<String>> allocations) {
        this.inventory = inventory;
        this.history = history;
        this.allocations = allocations;
    }

    public void cancel(String reservationId) {

        System.out.println("\nProcessing cancellation for: " + reservationId);

        // Validate existence
        if (!history.exists(reservationId)) {
            System.out.println("Cancellation FAILED: Reservation not found");
            return;
        }

        // Remove from confirmed
        Reservation r = history.removeConfirmed(reservationId);

        if (r == null) {
            System.out.println("Already cancelled or invalid");
            return;
        }

        String type = r.getRoomType();

        // Rollback using Stack (LIFO)
        Stack<String> stack = allocations.get(type);

        if (stack == null || stack.isEmpty()) {
            System.out.println("ERROR: No allocation found");
            return;
        }

        String releasedRoomId = stack.pop(); // LIFO rollback

        try {
            int current = inventory.getAvailability(type);
            inventory.updateAvailability(type, current + 1); // restore

            history.addCancelled(r);

            System.out.println("CANCELLED: " + r.getGuest() +
                    " | Released Room ID: " + releasedRoomId);

        } catch (InvalidBookingException e) {
            System.out.println("Rollback ERROR: " + e.getMessage());
        }
    }
}

// =======================
// MAIN (FULL SYSTEM)
// =======================
public class Main {
    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);

        BookingHistory history = new BookingHistory();
        BookingQueue queue = new BookingQueue();

        queue.add(new Reservation("R1", "Wasim", "Single Room"));
        queue.add(new Reservation("R2", "Ali", "Single Room"));

        BookingService booking = new BookingService(inventory, history);
        booking.process(queue);

        // Cancellation
        CancellationService cancel = new CancellationService(
                inventory, history, booking.getAllocations()
        );

        cancel.cancel("R2"); // valid
        cancel.cancel("R3"); // invalid

        // Final State
        history.show();
        inventory.display();
    }
}