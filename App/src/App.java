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
// RESERVATION (UPDATED)
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
// BOOKING SERVICE (UC4)
// =======================
class BookingService {

    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private Map<String, Reservation> confirmedReservations = new HashMap<>();

    private int roomCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
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

            // Store confirmed reservation
            confirmedReservations.put(req.getReservationId(), req);

            System.out.println("CONFIRMED: " + req.getGuestName() +
                    " | Room ID: " + roomId);
        }
    }

    public Map<String, Reservation> getConfirmedReservations() {
        return confirmedReservations;
    }
}

// =======================
// SERVICE (UC5 🔥)
// =======================
class Service {
    private String serviceName;
    private double cost;

    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() { return serviceName; }
    public double getCost() { return cost; }
}

// =======================
// ADD-ON SERVICE MANAGER
// =======================
class AddOnServiceManager {

    // reservationId -> list of services
    private Map<String, List<Service>> serviceMap = new HashMap<>();

    // Add service
    public void addService(String reservationId, Service service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        double total = 0;

        List<Service> services = serviceMap.get(reservationId);

        if (services != null) {
            for (Service s : services) {
                total += s.getCost();
            }
        }

        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        System.out.println("\nServices for Reservation: " + reservationId);

        List<Service> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No services added.");
            return;
        }

        for (Service s : services) {
            System.out.println(s.getServiceName() + " - ₹" + s.getCost());
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotalCost(reservationId));
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

        // Queue
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("R1", "Wasim", "Single Room"));
        queue.addRequest(new Reservation("R2", "Ali", "Single Room"));

        // Booking
        BookingService bookingService = new BookingService(inventory);
        bookingService.processBookings(queue);

        // Add-On Services
        AddOnServiceManager manager = new AddOnServiceManager();

        manager.addService("R1", new Service("Breakfast", 200));
        manager.addService("R1", new Service("WiFi", 100));
        manager.addService("R2", new Service("Airport Pickup", 500));

        // Display
        manager.displayServices("R1");
        manager.displayServices("R2");
    }
}