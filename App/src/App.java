public class App {
    // Abstract Class
    abstract class Room {
        private String roomType;
        private int beds;
        private double price;

        // Constructor
        public Room(String roomType, int beds, double price) {
            this.roomType = roomType;
            this.beds = beds;
            this.price = price;
        }

        // Encapsulation (Getters)
        public String getRoomType() {
            return roomType;
        }

        public int getBeds() {
            return beds;
        }

        public double getPrice() {
            return price;
        }

        // Abstract Method
        public abstract void displayRoomDetails();
    }

    // Single Room Class
    class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 1000);
        }

        @Override
        public void displayRoomDetails() {
            System.out.println("Type: " + getRoomType());
            System.out.println("Beds: " + getBeds());
            System.out.println("Price: ₹" + getPrice());
        }
    }

    // Double Room Class
    class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 2000);
        }

        @Override
        public void displayRoomDetails() {
            System.out.println("Type: " + getRoomType());
            System.out.println("Beds: " + getBeds());
            System.out.println("Price: ₹" + getPrice());
        }
    }

    // Suite Room Class
    class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 5000);
        }

        @Override
        public void displayRoomDetails() {
            System.out.println("Type: " + getRoomType());
            System.out.println("Beds: " + getBeds());
            System.out.println("Price: ₹" + getPrice());
        }
    }

    // Main Class
    public class Main {
        public static void main(String[] args) {

            // Creating Room Objects (Polymorphism)
            Room single = new SingleRoom();
            Room doubleRoom = new DoubleRoom();
            Room suite = new SuiteRoom();

            // Availability (Simple Variables)
            int singleAvailable = 5;
            int doubleAvailable = 3;
            int suiteAvailable = 2;

            // Display Data
            System.out.println("=== HOTEL ROOM DETAILS ===\n");

            single.displayRoomDetails();
            System.out.println("Available: " + singleAvailable);
            System.out.println("------------------------");

            doubleRoom.displayRoomDetails();
            System.out.println("Available: " + doubleAvailable);
            System.out.println("------------------------");

            suite.displayRoomDetails();
            System.out.println("Available: " + suiteAvailable);
            System.out.println("------------------------");

            System.out.println("\nApplication Terminated.");
        }
    }
}
