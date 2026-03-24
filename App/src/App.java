import java.util.*;

// =======================
// INVENTORY (THREAD-SAFE)
// =======================
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public synchronized void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public synchronized int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public synchronized boolean allocateRoom(String type) {
        int available = inventory.getOrDefault(type, 0);

        if (available <= 0) return false;

        inventory.put(type, available - 1);
        return true;
    }

    public synchronized void display() {
        System.out.println("\n=== INVENTORY ===");
        for (String k : inventory.keySet()) {
            System.out.println(k + " -> " + inventory.get(k));
        }
    }
}

// =======================
// RESERVATION
// =======================
class Reservation {
    private String guest;
    private String roomType;

    public Reservation(String guest, String roomType) {
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
}

// =======================
// THREAD-SAFE QUEUE
// =======================
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void add(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation getNext() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

// =======================
// BOOKING PROCESSOR (THREAD)
// =======================
class BookingProcessor implements Runnable {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {

        while (true) {

            Reservation r;

            // 🔥 CRITICAL SECTION (Queue access)
            synchronized (queue) {
                if (queue.isEmpty()) break;
                r = queue.getNext();
            }

            if (r == null) continue;

            // 🔥 CRITICAL SECTION (Inventory update)
            synchronized (inventory) {

                boolean success = inventory.allocateRoom(r.getRoomType());

                if (success) {
                    System.out.println(Thread.currentThread().getName() +
                            " CONFIRMED: " + r.getGuest());
                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " FAILED: " + r.getGuest());
                }
            }
        }
    }
}

// =======================
// MAIN (MULTI-THREAD SIMULATION)
// =======================
public class Main {
    public static void main(String[] args) throws InterruptedException {

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);

        BookingQueue queue = new BookingQueue();

        // Simulate multiple guests (same time)
        queue.add(new Reservation("Wasim", "Single Room"));
        queue.add(new Reservation("Ali", "Single Room"));
        queue.add(new Reservation("Rahman", "Single Room")); // should fail

        // Create threads (simulate concurrent users)
        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "T1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "T2");
        Thread t3 = new Thread(new BookingProcessor(queue, inventory), "T3");

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        t1.join();
        t2.join();
        t3.join();

        // Final state
        inventory.display();
    }
}