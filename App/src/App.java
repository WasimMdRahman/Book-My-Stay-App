public class App {

    import java.util.*;

    // Room Class
    class Room {
        int roomId;
        String type;
        boolean isBooked;

        Room(int roomId, String type) {
            this.roomId = roomId;
            this.type = type;
            this.isBooked = false;
        }
    }

    // Booking Class
    class Booking {
        int bookingId;
        String customerName;
        int roomId;

        Booking(int bookingId, String customerName, int roomId) {
            this.bookingId = bookingId;
            this.customerName = customerName;
            this.roomId = roomId;
        }
    }

    // Main System Class
    class HotelSystem {

        private Map<Integer, Room> rooms = new HashMap<>();
        private Queue<Booking> bookingQueue = new LinkedList<>();
        private Set<Integer> bookedRooms = new HashSet<>();

        private int bookingCounter = 1;

        // Add Room
        public void addRoom(int id, String type) {
            rooms.put(id, new Room(id, type));
            System.out.println("Room added: " + id);
        }

        // Book Room
        public void bookRoom(String customerName, int roomId) {
            if (!rooms.containsKey(roomId)) {
                System.out.println("Room does not exist.");
                return;
            }

            if (bookedRooms.contains(roomId)) {
                System.out.println("Room already booked!");
                return;
            }

            Booking booking = new Booking(bookingCounter++, customerName, roomId);
            bookingQueue.add(booking);
            bookedRooms.add(roomId);
            rooms.get(roomId).isBooked = true;

            System.out.println("Booking successful for " + customerName);
        }

        // View Bookings
        public void viewBookings() {
            if (bookingQueue.isEmpty()) {
                System.out.println("No bookings yet.");
                return;
            }

            for (Booking b : bookingQueue) {
                System.out.println("Booking ID: " + b.bookingId +
                        ", Name: " + b.customerName +
                        ", Room: " + b.roomId);
            }
        }
    }

    // Driver Class
    public class Main {
        public static void main(String[] args) {

            HotelSystem system = new HotelSystem();

            // Adding Rooms
            system.addRoom(101, "Single");
            system.addRoom(102, "Double");

            // Booking Rooms
            system.bookRoom("Wasim", 101);
            system.bookRoom("Rahman", 101); // Should fail
            system.bookRoom("Ali", 102);

            // View Bookings
            system.viewBookings();
        }
    }
}
